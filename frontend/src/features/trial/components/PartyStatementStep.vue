<script setup>
import { computed, nextTick, onMounted, ref, useTemplateRef } from 'vue'
import { Bot, CheckCircle2, FileText, LoaderCircle, Send, Sparkles, UserRound } from '@lucide/vue'
import { trialApi } from '@/api/trialApi.js'

const props = defineProps({
  trialId: { type: Number, required: true },
  side: { type: String, required: true, validator: (value) => ['A', 'B'].includes(value) },
  party: { type: Object, required: true },
  otherParty: { type: Object, default: null },
})

const emit = defineEmits(['update:party', 'back', 'confirm'])
const chatInput = ref('')
const errorMessage = ref('')
const loadingAction = ref('')
const chatLog = useTemplateRef('chatLog')

const answeredCount = computed(() => props.party.answers.length)
const currentQuestion = computed(() => props.party.questions[answeredCount.value] ?? null)
const conversationComplete = computed(() => (
  props.party.questionsLoaded
  && props.party.questions.length > 0
  && answeredCount.value === props.party.questions.length
))
const isBusy = computed(() => Boolean(loadingAction.value))

function updateParty(patch) {
  emit('update:party', { ...props.party, ...patch })
}

async function scrollChatToBottom() {
  await nextTick()
  if (chatLog.value) chatLog.value.scrollTop = chatLog.value.scrollHeight
}

function sendMessage() {
  const content = chatInput.value.trim()
  const question = currentQuestion.value
  if (!content || !question || isBusy.value || props.party.confirmed) return

  const answers = [...props.party.answers, { questionId: question.questionId, answer: content }]
  const messages = [...props.party.messages, { id: crypto.randomUUID(), role: 'USER', content }]
  const nextQuestion = props.party.questions[answers.length]

  messages.push({
    id: crypto.randomUUID(),
    role: 'ASSISTANT',
    content:
      nextQuestion?.question ??
      '필요한 내용을 모두 확인했습니다. ‘진술서 작성 완료’를 누르면 답변을 저장하고 변론문을 생성할게요.',
  })

  chatInput.value = ''
  errorMessage.value = ''
  updateParty({ answers, messages, draftGenerated: false })
  scrollChatToBottom()
}

async function loadGuideQuestions() {
  if (props.party.questionsLoaded || isBusy.value) return

  loadingAction.value = 'questions'
  errorMessage.value = ''
  try {
    const result = await trialApi.createGuideQuestions(props.trialId, props.side)
    const questions = (result.questions ?? []).map((question) => ({
      questionId: question.questionId,
      sequence: question.sequence,
      question: question.question,
    }))

    if (!questions.length) throw new Error('생성된 안내 질문이 없습니다.')

    updateParty({
      questions,
      questionsLoaded: true,
      messages: [
        ...props.party.messages,
        { id: crypto.randomUUID(), role: 'ASSISTANT', content: questions[0].question },
      ],
    })
    await scrollChatToBottom()
  } catch (error) {
    errorMessage.value = error.message
  } finally {
    loadingAction.value = ''
  }
}

async function createDraft() {
  if (!conversationComplete.value || isBusy.value || props.party.confirmed) return

  loadingAction.value = 'draft'
  errorMessage.value = ''
  try {
    const saved = await trialApi.saveGuideAnswers(props.trialId, props.side, {
      answers: props.party.answers,
    })
    if (!saved.allAnswered) {
      throw new Error('모든 안내 질문에 답변해야 변론문을 생성할 수 있습니다.')
    }

    const draft = await trialApi.createArgumentDraft(props.trialId, props.side)
    updateParty({
      draftGenerated: true,
      factSummary: draft.factSummary,
      argumentText: draft.argumentText,
    })
  } catch (error) {
    errorMessage.value = error.message
  } finally {
    loadingAction.value = ''
  }
}

async function confirmDraft() {
  if (!props.party.draftGenerated || !props.party.factSummary.trim() || !props.party.argumentText.trim() || isBusy.value || props.party.confirmed) return

  loadingAction.value = 'confirm'
  errorMessage.value = ''
  try {
    const draft = await trialApi.updateArgumentDraft(props.trialId, props.side, {
      factSummary: props.party.factSummary.trim(),
      argumentText: props.party.argumentText.trim(),
    })
    updateParty({ factSummary: draft.factSummary, argumentText: draft.argumentText })

    const confirmed = await trialApi.confirmArgument(props.trialId, props.side)
    emit('confirm', confirmed)
  } catch (error) {
    errorMessage.value = error.message
  } finally {
    loadingAction.value = ''
  }
}

onMounted(loadGuideQuestions)
</script>

<template>
  <div class="grid gap-5 lg:grid-cols-[minmax(0,1fr)_minmax(0,1fr)]">
    <section class="grid gap-4">
      <div v-if="otherParty" class="rounded-xl border border-border bg-card p-4">
        <h2 class="mb-2 flex items-center gap-2 font-heading font-semibold">
          <FileText class="size-4 text-primary" /> A측 주요 진술 요약
        </h2>
        <p class="rounded-lg bg-muted p-3 text-sm text-muted-foreground">{{ otherParty.factSummary }}</p>
      </div>

      <div class="flex min-h-[36rem] flex-col rounded-xl border border-border bg-card p-5 shadow-interactive">
        <div class="mb-4 flex items-center gap-3 border-b border-border pb-4">
          <span class="grid size-10 place-items-center rounded-full bg-[var(--ds-color-primary-fixed)] text-primary"><Bot class="size-5" /></span>
          <div>
            <h2 class="font-heading font-semibold">{{ side }}측 AI 변호사</h2>
            <p class="text-xs text-muted-foreground">안내 질문을 통해 진술을 정리해드려요</p>
          </div>
        </div>

        <div ref="chatLog" class="flex max-h-[27rem] flex-1 flex-col gap-4 overflow-y-auto pr-1" aria-live="polite">
          <div
            v-for="message in party.messages"
            :key="message.id"
            class="flex items-end gap-2"
            :class="message.role === 'USER' ? 'justify-end' : 'justify-start'"
          >
            <span v-if="message.role === 'ASSISTANT'" class="grid size-8 shrink-0 place-items-center rounded-full bg-muted text-primary">
              <Bot class="size-4" />
            </span>
            <p
              class="max-w-[82%] rounded-xl px-4 py-3 text-sm leading-6"
              :class="message.role === 'USER' ? 'rounded-br-sm bg-primary text-primary-foreground' : 'rounded-bl-sm bg-muted'"
            >
              {{ message.content }}
            </p>
            <span v-if="message.role === 'USER'" class="grid size-8 shrink-0 place-items-center rounded-full bg-primary/10 text-primary">
              <UserRound class="size-4" />
            </span>
          </div>

          <div v-if="loadingAction === 'questions'" class="flex items-center gap-2 text-sm text-muted-foreground">
            <LoaderCircle class="size-4 animate-spin" /> 안내 질문을 준비하고 있습니다.
          </div>
        </div>

        <p v-if="errorMessage" class="mt-3 rounded-lg bg-destructive/10 px-3 py-2 text-sm text-destructive" role="alert">
          {{ errorMessage }}
        </p>
        <button
          v-if="errorMessage && !party.questionsLoaded"
          class="mt-2 self-start text-sm font-semibold text-primary"
          type="button"
          :disabled="isBusy"
          @click="loadGuideQuestions"
        >
          안내 질문 다시 불러오기
        </button>

        <form class="mt-4 flex gap-2 border-t border-border pt-4" @submit.prevent="sendMessage">
          <input
            v-model="chatInput"
            class="min-w-0 flex-1 rounded-lg border border-input bg-muted px-4 py-2.5 outline-none focus:ring-2 focus:ring-ring disabled:cursor-not-allowed"
            :disabled="!currentQuestion || isBusy || party.confirmed"
            :placeholder="conversationComplete ? '답변이 완료되었습니다.' : '답변을 입력해주세요...'"
          />
          <button
            class="rounded-lg bg-primary px-5 text-primary-foreground disabled:cursor-not-allowed disabled:opacity-40"
            :disabled="!chatInput.trim() || !currentQuestion || isBusy || party.confirmed"
            type="submit"
            aria-label="답변 전송"
          >
            <Send class="size-4" />
          </button>
        </form>

        <button
          class="mt-3 flex w-full items-center justify-center gap-2 rounded-lg border border-primary px-4 py-2.5 font-semibold text-primary disabled:cursor-not-allowed disabled:opacity-40"
          :disabled="!conversationComplete || isBusy || party.confirmed"
          type="button"
          @click="createDraft"
        >
          <LoaderCircle v-if="loadingAction === 'draft'" class="size-4 animate-spin" />
          <Sparkles v-else class="size-4" />
          {{ loadingAction === 'draft' ? '변론문 생성 중...' : party.draftGenerated ? '진술서 다시 작성' : '진술서 작성 완료' }}
        </button>
      </div>
    </section>

    <section class="rounded-xl border border-border bg-card p-5 shadow-interactive">
      <h2 class="border-b border-border pb-3 font-heading text-xl font-semibold">변론문 초안 미리보기</h2>
      <div v-if="party.draftGenerated" class="mt-4 min-h-[31rem] rounded-lg bg-[var(--ds-color-primary-fixed)] p-5">
        <h3 class="mb-2 font-heading font-semibold">사건 개요 및 쟁점 파악</h3>
        <textarea
          :value="party.factSummary"
          class="mb-6 min-h-28 w-full resize-none rounded-lg border border-primary/20 bg-card/60 p-3 text-sm leading-6 outline-none focus:ring-2 focus:ring-ring disabled:opacity-70"
          :disabled="party.confirmed"
          aria-label="사실관계 요약 수정"
          @input="updateParty({ factSummary: $event.target.value })"
        />

        <h3 class="mb-2 font-heading font-semibold">{{ side }}측 완성 변론문</h3>
        <textarea
          :value="party.argumentText"
          class="min-h-64 w-full resize-none rounded-lg border border-primary/20 bg-card/60 p-3 text-sm leading-6 outline-none focus:ring-2 focus:ring-ring disabled:opacity-70"
          :disabled="party.confirmed"
          aria-label="변론문 수정"
          @input="updateParty({ argumentText: $event.target.value })"
        />
      </div>
      <div v-else class="mt-4 grid min-h-[31rem] place-items-center rounded-lg border border-dashed border-border bg-muted p-8 text-center text-sm text-muted-foreground">
        <div>
          <Sparkles class="mx-auto mb-3 size-7 text-primary" />
          모든 안내 질문에 답한 뒤<br />‘진술서 작성 완료’를 눌러주세요.
        </div>
      </div>
      <p class="mt-3 text-center text-xs text-muted-foreground">모든 답변이 저장된 후 Mock AI가 사실관계와 변론문을 생성합니다.</p>
    </section>

    <div class="flex justify-between border-t border-border pt-4 lg:col-span-2">
      <button class="rounded-lg border border-border bg-card px-5 py-2.5" type="button" :disabled="isBusy" @click="$emit('back')">이전</button>
      <button
        class="flex items-center gap-2 rounded-lg bg-primary px-5 py-2.5 font-semibold text-primary-foreground disabled:cursor-not-allowed disabled:opacity-40"
        :disabled="!party.draftGenerated || !party.factSummary.trim() || !party.argumentText.trim() || isBusy || party.confirmed"
        type="button"
        @click="confirmDraft"
      >
        <LoaderCircle v-if="loadingAction === 'confirm'" class="size-4 animate-spin" />
        <CheckCircle2 v-else class="size-4" />
        {{ party.confirmed ? '진술 확정 완료' : loadingAction === 'confirm' ? '저장 및 확정 중...' : '진술 확정' }}
      </button>
    </div>
  </div>
</template>
