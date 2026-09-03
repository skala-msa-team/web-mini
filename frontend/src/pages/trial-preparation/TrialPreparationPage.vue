<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import PartyStatementStep from '@/features/trial/components/PartyStatementStep.vue'
import TrialBasicInformation from '@/features/trial/components/TrialBasicInformation.vue'
import TrialFinalConfirmation from '@/features/trial/components/TrialFinalConfirmation.vue'
import TrialStepIndicator from '@/features/trial/components/TrialStepIndicator.vue'

const currentStep = ref(1)
const TRIAL_DRAFT_STORAGE_KEY = 'love-war:trial-draft'

const trial = reactive({
  title: '',
  aDisplayName: '',
  bDisplayName: '',
  summary: '',
})

onMounted(() => {
  const storedDraft = sessionStorage.getItem(TRIAL_DRAFT_STORAGE_KEY)

  if (!storedDraft) return

  try {
    const draft = JSON.parse(storedDraft)

    trial.title = draft.title ?? ''
    trial.summary = draft.content ?? ''
  } catch {
    sessionStorage.removeItem(TRIAL_DRAFT_STORAGE_KEY)
  }
})

const parties = reactive({
  A: {
    messages: [
      {
        id: 'a-introduction',
        role: 'ASSISTANT',
        content: '안녕하세요. A측의 입장을 담당한 AI 변호사입니다. 먼저 사건이 어떻게 시작됐는지 편하게 설명해주세요.',
      },
    ],
    draftGenerated: false,
    caseOverview: '',
    keyPoints: [],
    argumentText: '',
    confirmed: false,
  },
  B: {
    messages: [
      {
        id: 'b-introduction',
        role: 'ASSISTANT',
        content: '안녕하세요. B측의 입장을 담당한 AI 변호사입니다. A측과 다른 관점이 있다면 사건의 시작부터 설명해주세요.',
      },
    ],
    draftGenerated: false,
    caseOverview: '',
    keyPoints: [],
    argumentText: '',
    confirmed: false,
  },
})

const currentSide = computed(() => (currentStep.value === 2 ? 'A' : 'B'))

function updateTrial(value) {
  Object.assign(trial, value)
}

function updateParty(value) {
  Object.assign(parties[currentSide.value], value)
}

function confirmParty() {
  parties[currentSide.value].confirmed = true
  currentStep.value += 1
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function goToStep(step) {
  currentStep.value = step
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
</script>

<template>
  <div class="min-h-screen bg-background">
    <main class="mx-auto max-w-[var(--ds-container-max)] px-4 py-10 sm:px-6 sm:py-14">
      <h1 class="mb-8 text-center font-heading text-heading-1 text-[var(--ds-color-primary)]">새로운 재판 열기</h1>

      <div class="mx-auto mb-12 max-w-3xl">
        <TrialStepIndicator :current-step="currentStep" />
      </div>

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
          @back="goToStep(3)"
          @edit="goToStep"
        />
      </div>
    </main>

  </div>
</template>
