<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createTrial } from '@/apis/postApi.js'
import {
  confirmArgument,
  createArgumentDraft,
  createGuideQuestions,
  getTrial,
  saveGuideAnswers,
  saveStatement,
  startTrial as startTrialRequest,
  updateArgumentDraft,
} from '@/apis/trialApi.js'
import PartyStatementStep from '@/components/trial/PartyStatementStep.vue'
import TrialBasicInformation from '@/components/trial/TrialBasicInformation.vue'
import TrialFinalConfirmation from '@/components/trial/TrialFinalConfirmation.vue'
import TrialStepIndicator from '@/components/trial/TrialStepIndicator.vue'

const currentStep = ref(1)
const TRIAL_DRAFT_STORAGE_KEY = 'love-war:trial-draft'
const route = useRoute()
const router = useRouter()
const startPending = ref(false)
const startError = ref('')
const bothConfirmed = ref(false)
const preparationPending = ref(false)
const preparationError = ref('')

function positiveInteger(value) {
  const normalizedValue = Array.isArray(value) ? value[0] : value
  const parsedValue = Number(normalizedValue)

  return Number.isInteger(parsedValue) && parsedValue > 0 ? parsedValue : null
}

const trialId = computed(() => {
  return positiveInteger(route.params.trialId ?? route.query.trialId)
})
const postId = computed(() => positiveInteger(route.query.postId))

const trial = reactive({
  title: '',
  aDisplayName: '',
  bDisplayName: '',
  summary: '',
})

onMounted(async () => {
  const storedDraft = sessionStorage.getItem(TRIAL_DRAFT_STORAGE_KEY)

  if (storedDraft) {
    try {
      const draft = JSON.parse(storedDraft)

      trial.title = draft.title ?? ''
      trial.summary = draft.content ?? ''
    } catch {
      sessionStorage.removeItem(TRIAL_DRAFT_STORAGE_KEY)
    }
  }

  if (!trialId.value) return

  preparationPending.value = true
  try {
    const savedTrial = await getTrial(trialId.value)
    trial.title = savedTrial.title
    trial.summary = savedTrial.content
    trial.aDisplayName = savedTrial.aParty.displayName
    trial.bDisplayName = savedTrial.bParty.displayName
    parties.A.confirmed = Boolean(savedTrial.aParty.ready)
    parties.B.confirmed = Boolean(savedTrial.bParty.ready)
    bothConfirmed.value = parties.A.confirmed && parties.B.confirmed
  } catch (error) {
    preparationError.value = error?.message || '재판 정보를 불러오지 못했습니다.'
  } finally {
    preparationPending.value = false
  }
})

const parties = reactive({
  A: {
    messages: [
      {
        id: 'a-introduction',
        role: 'ASSISTANT',
        content: '안녕하세요. A측의 입장을 담당한 AI 변호사입니다. 사건이 언제 발생했는지 알려주세요.',
      },
    ],
    draftGenerated: false,
    caseOverview: '',
    keyPoints: [],
    argumentText: '',
    confirmed: false,
    confirmedAt: null,
    statementSaved: false,
    guideQuestions: [],
    guideAnswers: [],
    pending: false,
    error: '',
  },
  B: {
    messages: [
      {
        id: 'b-introduction',
        role: 'ASSISTANT',
        content: '안녕하세요. B측의 입장을 담당한 AI 변호사입니다. 사건이 언제 발생했는지 알려주세요.',
      },
    ],
    draftGenerated: false,
    caseOverview: '',
    keyPoints: [],
    argumentText: '',
    confirmed: false,
    confirmedAt: null,
    statementSaved: false,
    guideQuestions: [],
    guideAnswers: [],
    pending: false,
    error: '',
  },
})

const currentSide = computed(() => (currentStep.value === 2 ? 'A' : 'B'))

function updateTrial(value) {
  Object.assign(trial, value)
}

function updateParty(value) {
  Object.assign(parties[currentSide.value], value)
}

async function createTrialAndContinue() {
  if (preparationPending.value) return

  if (trialId.value) {
    goToStep(2)
    return
  }

  if (!postId.value) {
    preparationError.value = '게시글 식별 정보가 없습니다. 게시글 등록부터 다시 진행해주세요.'
    return
  }

  preparationPending.value = true
  preparationError.value = ''

  try {
    const createdTrial = await createTrial(postId.value, {
      visibility: 'PUBLIC',
      aDisplayName: trial.aDisplayName.trim(),
      bDisplayName: trial.bDisplayName.trim(),
    })

    await router.replace({
      name: 'trial-preparation',
      query: { postId: postId.value, trialId: createdTrial.trialId },
    })
    goToStep(2)
  } catch (error) {
    preparationError.value = error?.message || '재판을 생성하지 못했습니다.'
  } finally {
    preparationPending.value = false
  }
}

async function prepareParty(statement) {
  if (!trialId.value) return

  const side = currentSide.value
  const party = parties[side]
  party.pending = true
  party.error = ''

  try {
    await saveStatement(trialId.value, side, statement)
    const response = await createGuideQuestions(trialId.value, side)
    const guideQuestions = response?.questions ?? []

    Object.assign(party, {
      statementSaved: true,
      guideQuestions,
      guideAnswers: [],
      messages: [
        ...party.messages,
        {
          id: `${side}-guide-${guideQuestions[0]?.questionId ?? 'complete'}`,
          role: 'ASSISTANT',
          content: guideQuestions[0]?.question ?? '추가 질문 없이 변론문을 생성할 수 있습니다.',
        },
      ],
    })
  } catch (error) {
    party.error = error?.message || '진술을 저장하지 못했습니다.'
  } finally {
    party.pending = false
  }
}

async function generatePartyDraft(guideAnswers) {
  if (!trialId.value) return

  const side = currentSide.value
  const party = parties[side]
  party.pending = true
  party.error = ''

  try {
    if (guideAnswers.length) {
      const savedAnswers = await saveGuideAnswers(trialId.value, side, {
        answers: guideAnswers,
      })

      if (!savedAnswers.allAnswered) {
        throw new Error('모든 AI 안내 질문에 답변한 뒤 변론문을 생성해주세요.')
      }
    }

    const draft = await createArgumentDraft(trialId.value, side)
    Object.assign(party, {
      draftGenerated: true,
      caseOverview: draft.factSummary,
      keyPoints: guideAnswers.map((answer) => answer.answer),
      argumentText: draft.argumentText,
    })
  } catch (error) {
    party.error = error?.message || '변론문 초안을 생성하지 못했습니다.'
  } finally {
    party.pending = false
  }
}

async function confirmParty() {
  if (!trialId.value) return

  const side = currentSide.value
  const party = parties[side]
  party.pending = true
  party.error = ''

  try {
    const updatedDraft = await updateArgumentDraft(trialId.value, side, {
      factSummary: party.caseOverview.trim(),
      argumentText: party.argumentText.trim(),
    })
    const confirmation = await confirmArgument(trialId.value, side)

    party.caseOverview = updatedDraft.factSummary
    party.argumentText = updatedDraft.argumentText
    party.confirmed = true
    party.confirmedAt = confirmation.confirmedAt
    bothConfirmed.value = Boolean(confirmation.bothConfirmed)
    currentStep.value += 1
    window.scrollTo({ top: 0, behavior: 'smooth' })
  } catch (error) {
    party.error = error?.message || '진술을 확정하지 못했습니다.'
  } finally {
    party.pending = false
  }
}

function goToStep(step) {
  startError.value = ''
  currentStep.value = step
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

async function startTrial() {
  if (startPending.value) return

  if (!trialId.value) {
    startError.value = '재판 식별 정보가 없습니다. 게시글 등록부터 다시 진행해주세요.'
    return
  }

  if (!bothConfirmed.value) {
    startError.value = '양측 진술이 모두 확정되어야 재판을 시작할 수 있습니다.'
    return
  }

  startPending.value = true
  startError.value = ''

  try {
    await startTrialRequest(trialId.value)
    sessionStorage.removeItem(TRIAL_DRAFT_STORAGE_KEY)
    await router.push({ name: 'live-trial', params: { trialId: trialId.value } })
  } catch (error) {
    startError.value = error?.message || '재판을 시작하지 못했습니다. 잠시 후 다시 시도해주세요.'
  } finally {
    startPending.value = false
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

      <div :class="currentStep === 2 || currentStep === 3 ? '' : 'mx-auto max-w-3xl'">
        <p
          v-if="preparationError"
          class="mb-4 rounded-lg bg-[var(--ds-color-error-container)] px-4 py-3 text-sm text-[var(--ds-color-on-error-container)]"
          role="alert"
        >
          {{ preparationError }}
        </p>

        <TrialBasicInformation
          v-if="currentStep === 1"
          :model-value="trial"
          @update:model-value="updateTrial"
          :pending="preparationPending"
          :locked="Boolean(trialId)"
          @next="createTrialAndContinue"
        />

        <PartyStatementStep
          v-else-if="currentStep === 2 || currentStep === 3"
          :key="currentSide"
          :side="currentSide"
          :party="parties[currentSide]"
          :other-party="currentSide === 'B' ? parties.A : null"
          @update:party="updateParty"
          @back="goToStep(currentStep - 1)"
          @prepare="prepareParty"
          @generate-draft="generatePartyDraft"
          @confirm="confirmParty"
        />

        <TrialFinalConfirmation
          v-else
          :trial="trial"
          :parties="parties"
          :both-confirmed="bothConfirmed"
          :start-pending="startPending"
          :start-error="startError"
          @back="goToStep(3)"
          @edit="goToStep"
          @start="startTrial"
        />
      </div>
    </main>

  </div>
</template>
