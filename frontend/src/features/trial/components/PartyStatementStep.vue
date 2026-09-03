<script setup>
import { computed, nextTick, ref, useTemplateRef } from 'vue'
import { Bot, CheckCircle2, FileText, Send, Sparkles, UserRound } from '@lucide/vue'

const props = defineProps({
  side: { type: String, required: true, validator: (value) => ['A', 'B'].includes(value) },
  party: { type: Object, required: true },
  otherParty: { type: Object, default: null },
})

const emit = defineEmits(['update:party', 'back', 'confirm'])
const chatInput = ref('')
const chatLog = useTemplateRef('chatLog')
const guideQuestions = [
  '그 상황에서 가장 서운하거나 힘들었던 점은 무엇이었나요?',
  '상대방에게 바라는 변화나 원하는 해결 방향을 알려주세요.',
]

const userMessages = computed(() => props.party.messages.filter((message) => message.role === 'USER'))
const conversationComplete = computed(() => userMessages.value.length >= guideQuestions.length + 1)

function updateParty(patch) {
  emit('update:party', { ...props.party, ...patch })
}

async function scrollChatToBottom() {
  await nextTick()
  if (chatLog.value) chatLog.value.scrollTop = chatLog.value.scrollHeight
}

function sendMessage() {
  const content = chatInput.value.trim()
  if (!content || conversationComplete.value) return

  const messages = [...props.party.messages, { id: crypto.randomUUID(), role: 'USER', content }]
  const answeredCount = messages.filter((message) => message.role === 'USER').length
  const nextQuestion = guideQuestions[answeredCount - 1]

  messages.push({
    id: crypto.randomUUID(),
    role: 'ASSISTANT',
    content:
      nextQuestion ??
      '필요한 내용을 모두 확인했습니다. 아래의 ‘진술서 작성 완료’를 누르면 대화를 바탕으로 초안을 정리해드릴게요.',
  })

  chatInput.value = ''
  updateParty({ messages, draftGenerated: false })
  scrollChatToBottom()
}

// #221에서 Mock AI API 응답으로 교체할 임시 초안 생성 동작입니다.
function createMockDraft() {
  if (!conversationComplete.value) return

  const answers = userMessages.value.map((message) => message.content)
  updateParty({
    draftGenerated: true,
    caseOverview: `${props.side}측은 ${answers[0]} 이 과정에서 양측의 기대와 상황 인식이 달라 갈등이 발생한 것으로 파악됩니다.`,
    keyPoints: [answers[1], answers[2]],
    argumentText: `${props.side}측은 ${answers[1]}고 설명합니다. 이를 해결하기 위해 ${answers[2]}는 방향을 제안합니다.`,
  })
}
</script>

<template>
  <div class="grid gap-5 lg:grid-cols-[minmax(0,1fr)_minmax(0,1fr)]">
    <section class="grid gap-4">
      <div v-if="otherParty" class="rounded-xl border border-border bg-card p-4">
        <h2 class="mb-2 flex items-center gap-2 font-heading font-semibold">
          <FileText class="size-4 text-primary" /> A측 주요 진술 요약
        </h2>
        <p class="rounded-lg bg-muted p-3 text-sm text-muted-foreground">{{ otherParty.argumentText }}</p>
      </div>

      <div class="flex min-h-[36rem] flex-col rounded-xl border border-border bg-card p-5 shadow-interactive">
        <div class="mb-4 flex items-center gap-3 border-b border-border pb-4">
          <span class="grid size-10 place-items-center rounded-full bg-[var(--ds-color-primary-fixed)] text-primary"><Bot class="size-5" /></span>
          <div>
            <h2 class="font-heading font-semibold">{{ side }}측 AI 변호사</h2>
            <p class="text-xs text-muted-foreground">대화를 통해 진술을 정리해드려요</p>
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
        </div>

        <form class="mt-4 flex gap-2 border-t border-border pt-4" @submit.prevent="sendMessage">
          <input
            v-model="chatInput"
            class="min-w-0 flex-1 rounded-lg border border-input bg-muted px-4 py-2.5 outline-none focus:ring-2 focus:ring-ring disabled:cursor-not-allowed"
            :disabled="conversationComplete"
            :placeholder="conversationComplete ? '대화가 완료되었습니다.' : '상황을 설명해주세요...'"
          />
          <button
            class="rounded-lg bg-primary px-5 text-primary-foreground disabled:cursor-not-allowed disabled:opacity-40"
            :disabled="!chatInput.trim() || conversationComplete"
            type="submit"
            aria-label="메시지 전송"
          >
            <Send class="size-4" />
          </button>
        </form>

        <button
          class="mt-3 flex w-full items-center justify-center gap-2 rounded-lg border border-primary px-4 py-2.5 font-semibold text-primary disabled:cursor-not-allowed disabled:opacity-40"
          :disabled="!conversationComplete"
          type="button"
          @click="createMockDraft"
        >
          <Sparkles class="size-4" /> {{ party.draftGenerated ? '진술서 다시 작성' : '진술서 작성 완료' }}
        </button>
      </div>
    </section>

    <section class="rounded-xl border border-border bg-card p-5 shadow-interactive">
      <h2 class="border-b border-border pb-3 font-heading text-xl font-semibold">변론문 초안 미리보기</h2>
      <div v-if="party.draftGenerated" class="mt-4 min-h-[31rem] rounded-lg bg-[var(--ds-color-primary-fixed)] p-5">
        <h3 class="mb-2 font-heading font-semibold">사건 개요 및 쟁점 파악</h3>
        <p class="mb-6 text-sm leading-6 text-muted-foreground">{{ party.caseOverview }}</p>

        <h3 class="mb-2 font-heading font-semibold">핵심 진술 요지</h3>
        <ul class="mb-6 list-disc space-y-2 pl-5 text-sm leading-6 text-muted-foreground">
          <li v-for="point in party.keyPoints" :key="point">{{ point }}</li>
        </ul>

        <h3 class="mb-2 font-heading font-semibold">{{ side }}측 변론문</h3>
        <textarea
          :value="party.argumentText"
          class="min-h-40 w-full resize-none rounded-lg border border-primary/20 bg-card/60 p-3 text-sm leading-6 outline-none focus:ring-2 focus:ring-ring"
          aria-label="변론문 수정"
          @input="updateParty({ argumentText: $event.target.value })"
        />
      </div>
      <div v-else class="mt-4 grid min-h-[31rem] place-items-center rounded-lg border border-dashed border-border bg-muted p-8 text-center text-sm text-muted-foreground">
        <div>
          <Sparkles class="mx-auto mb-3 size-7 text-primary" />
          AI 변호사와 대화를 마친 뒤<br />‘진술서 작성 완료’를 눌러주세요.
        </div>
      </div>
      <p class="mt-3 text-center text-xs text-muted-foreground">대화 완료 후 Mock AI가 사건 개요와 핵심 진술을 요약합니다.</p>
    </section>

    <div class="flex justify-between border-t border-border pt-4 lg:col-span-2">
      <button class="rounded-lg border border-border bg-card px-5 py-2.5" type="button" @click="$emit('back')">이전</button>
      <button
        class="flex items-center gap-2 rounded-lg bg-primary px-5 py-2.5 font-semibold text-primary-foreground disabled:cursor-not-allowed disabled:opacity-40"
        :disabled="!party.draftGenerated || !party.argumentText.trim()"
        type="button"
        @click="$emit('confirm')"
      >
        <CheckCircle2 class="size-4" /> 진술 확정
      </button>
    </div>
  </div>
</template>
