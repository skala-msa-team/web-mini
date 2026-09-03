<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { trialApi } from '@/api/trialApi.js'
import PartyStatementStep from '@/features/trial/components/PartyStatementStep.vue'
import TrialBasicInformation from '@/features/trial/components/TrialBasicInformation.vue'
import TrialFinalConfirmation from '@/features/trial/components/TrialFinalConfirmation.vue'
import TrialStepIndicator from '@/features/trial/components/TrialStepIndicator.vue'

const route = useRoute()
const router = useRouter()
const currentStep = ref(1)
const pageError = ref('')
const startLoading = ref(false)
const bothConfirmed = ref(false)
const TRIAL_DRAFT_STORAGE_KEY = 'love-war:trial-draft'

const trialId = computed(() => {
  const value = Number(route.query.trialId)
  return Number.isInteger(value) && value > 0 ? value : null
})

const trial = reactive({
  title: '',
  aDisplayName: '',
  bDisplayName: '',
  summary: '',
})

function loadStoredDraft() {
  const storedDraft = sessionStorage.getItem(TRIAL_DRAFT_STORAGE_KEY)
  if (!storedDraft) return

  try {
    const draft = JSON.parse(storedDraft)
    trial.title = draft.title ?? ''
    trial.summary = draft.content ?? ''
  } catch {
    sessionStorage.removeItem(TRIAL_DRAFT_STORAGE_KEY)
  }
}

function createParty(side) {
  return {
    messages: [{
      id: `${side.toLowerCase()}-introduction`,
      role: 'ASSISTANT',
      content: `안녕하세요. ${side}측의 입장을 담당한 AI 변호사입니다. 저장된 진술을 확인하고 안내 질문을 준비할게요.`,
    }],
    questions: [],
    answers: [],
    questionsLoaded: false,
    draftGenerated: false,
    factSummary: '',
    argumentText: '',
    confirmed: false,
    confirmedAt: null,
  }
}

const parties = reactive({
  A: createParty('A'),
  B: createParty('B'),
})

const currentSide = computed(() => (currentStep.value === 2 ? 'A' : 'B'))

async function loadTrial() {
  if (!trialId.value) return

  try {
    const detail = await trialApi.getTrial(trialId.value)
    trial.title = detail.title ?? trial.title
    trial.summary = detail.content ?? trial.summary
    trial.aDisplayName = detail.aParty?.displayName ?? trial.aDisplayName
    trial.bDisplayName = detail.bParty?.displayName ?? trial.bDisplayName
    parties.A.confirmed = Boolean(detail.aParty?.ready)
    parties.B.confirmed = Boolean(detail.bParty?.ready)
    bothConfirmed.value = parties.A.confirmed && parties.B.confirmed
  } catch (error) {
    pageError.value = error.message
  }
}

onMounted(() => {
  loadStoredDraft()
  loadTrial()
})

function updateTrial(value) {
  Object.assign(trial, value)
}

function updateParty(value) {
  Object.assign(parties[currentSide.value], value)
}

function confirmParty(result) {
  const party = parties[currentSide.value]
  party.confirmed = true
  party.confirmedAt = result.confirmedAt
  bothConfirmed.value = Boolean(result.bothConfirmed)
  currentStep.value = currentSide.value === 'A' ? 3 : 4
  pageError.value = ''
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function goToStep(step) {
  if (step >= 2 && !trialId.value) {
    pageError.value = '연동할 재판 ID가 없습니다. 재판 생성 후 다시 시도해 주세요.'
    return
  }

  pageError.value = ''
  currentStep.value = step
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

async function startTrial() {
  if (!trialId.value || !bothConfirmed.value || startLoading.value) return

  startLoading.value = true
  pageError.value = ''
  try {
    await trialApi.startTrial(trialId.value)
    await router.push({ name: 'live-trial', params: { trialId: trialId.value } })
  } catch (error) {
    pageError.value = error.message
  } finally {
    startLoading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-background">
    <main class="mx-auto max-w-[var(--ds-container-max)] px-4 py-10 sm:px-6 sm:py-14">
      <h1 class="mb-8 text-center font-heading text-heading-1 text-[var(--ds-color-primary)]">새로운 재판 열기</h1>

      <div class="mx-auto mb-12 max-w-3xl">
        <TrialStepIndicator :current-step="currentStep" />
      </div>

      <p
        v-if="pageError && currentStep !== 4"
        class="mx-auto mb-5 max-w-3xl rounded-lg border border-destructive/30 bg-destructive/10 px-4 py-3 text-sm text-destructive"
        role="alert"
      >
        {{ pageError }}
      </p>

      <div :class="currentStep === 2 || currentStep === 3 ? '' : 'mx-auto max-w-3xl'">
        <TrialBasicInformation
          v-if="currentStep === 1"
          :model-value="trial"
          @update:model-value="updateTrial"
          @next="goToStep(2)"
        />

        <PartyStatementStep
          v-else-if="currentStep === 2 || currentStep === 3"
          :key="currentSide"
          :trial-id="trialId"
          :side="currentSide"
          :party="parties[currentSide]"
          :other-party="currentSide === 'B' ? parties.A : null"
          @update:party="updateParty"
          @back="goToStep(currentStep - 1)"
          @confirm="confirmParty"
        />

        <TrialFinalConfirmation
          v-else
          :trial="trial"
          :parties="parties"
          :both-confirmed="bothConfirmed"
          :start-loading="startLoading"
          :start-error="pageError"
          @back="goToStep(3)"
          @start="startTrial"
        />
      </div>
    </main>
  </div>
</template>
