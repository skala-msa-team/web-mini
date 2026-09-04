<script setup>
import { nextTick, ref, watch } from 'vue'
import { Scale, Sparkles } from '@lucide/vue'

const props = defineProps({
  events: { type: Array, default: () => [] },
  remainingTime: { type: String, required: true },
})

const debateList = ref(null)
const shouldFollowLatest = ref(true)
const FOLLOW_LATEST_THRESHOLD = 48

function formatEventTime(value) {
  if (!value) return ''
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return ''

  return new Intl.DateTimeFormat('ko-KR', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false,
  }).format(date)
}

watch(
  () => props.events.at(-1)?.id,
  async () => {
    await nextTick()
    if (!shouldFollowLatest.value || !debateList.value) return

    debateList.value.scrollTo({
      top: debateList.value.scrollHeight,
      behavior: 'smooth',
    })
  },
  { immediate: true },
)

function updateFollowLatest() {
  if (!debateList.value) return

  const remainingScroll =
    debateList.value.scrollHeight - debateList.value.scrollTop - debateList.value.clientHeight
  shouldFollowLatest.value = remainingScroll <= FOLLOW_LATEST_THRESHOLD
}
</script>

<template>
  <section class="debate-panel" aria-labelledby="debate-title">
    <header class="panel-header">
      <div>
        <h2 id="debate-title"><Scale :size="20" /> AI 재판 진행 내역</h2>
        <p>사건 소개부터 양측 주장과 상호 변론까지 확인할 수 있습니다.</p>
      </div>
      <span class="timer-badge">남은 시간 {{ remainingTime }}</span>
    </header>

    <div
      ref="debateList"
      class="debate-list"
      aria-live="polite"
      tabindex="0"
      @scroll.passive="updateFollowLatest"
    >
      <p v-if="!events.length" class="debate-notice">
        <Sparkles :size="17" /> 첫 번째 변론을 준비하고 있습니다.
      </p>

      <article
        v-for="event in events"
        :key="event.id"
        class="debate-message"
        :class="`side-${event.side.toLowerCase()}`"
      >
        <div class="message-meta">
          <strong>{{ event.speaker }}</strong>
          <span class="event-label">{{ event.label }}</span>
          <time v-if="event.occurredAt" :datetime="event.occurredAt">
            {{ formatEventTime(event.occurredAt) }}
          </time>
        </div>
        <p>{{ event.content }}</p>
      </article>
    </div>
  </section>
</template>

<style scoped>
.debate-panel {
  height: 456px;
  min-height: 0;
  display: grid;
  grid-template-rows: auto minmax(0, 1fr);
  border: 1px solid var(--ds-color-outline-variant);
  border-radius: var(--ds-radius-md);
  background: white;
  overflow: hidden;
}

.panel-header {
  min-height: 74px;
  padding: 12px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  border-bottom: 1px solid var(--ds-color-outline-variant);
  background: var(--ds-color-surface-container-low);
}

h2 {
  margin: 0;
  display: inline-flex;
  align-items: center;
  gap: 9px;
  color: var(--ds-color-primary);
  font-size: 1.2rem;
}

.panel-header p {
  margin: 5px 0 0;
  color: var(--ds-color-on-surface-variant);
  font-size: 1rem;
}

.timer-badge {
  padding: 6px 10px;
  border-radius: var(--ds-radius-full);
  background: #dbeafe;
  color: #245284;
  font-size: 0.9rem;
  font-weight: 700;
  white-space: nowrap;
}

.debate-list {
  min-height: 0;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 18px;
  overflow-y: auto;
  overscroll-behavior-y: contain;
  scrollbar-gutter: stable;
  scrollbar-color: #9fb2cd #edf3fa;
  scrollbar-width: thin;
  background: linear-gradient(180deg, #f9fbff 0%, white 35%);
}

.debate-list::-webkit-scrollbar {
  width: 10px;
}

.debate-list::-webkit-scrollbar-track {
  background: #edf3fa;
}

.debate-list::-webkit-scrollbar-thumb {
  border: 2px solid #edf3fa;
  border-radius: var(--ds-radius-full);
  background: #9fb2cd;
}

.debate-list:focus-visible {
  outline: 2px solid var(--ds-color-justice-blue);
  outline-offset: -2px;
}

.debate-message {
  width: min(82%, 520px);
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.debate-message.side-a {
  align-self: flex-start;
}

.debate-message.side-b {
  align-self: flex-end;
  align-items: flex-end;
}

.debate-message.side-judge {
  width: min(90%, 620px);
  align-self: center;
  align-items: center;
}

.message-meta {
  display: flex;
  align-items: center;
  gap: 7px;
  color: var(--ds-color-on-surface-variant);
  font-size: 0.85rem;
}

.message-meta strong {
  color: var(--ds-color-primary);
  font-size: 1.05rem;
}

.side-b .message-meta strong {
  color: var(--ds-color-justice-blue);
}

.side-judge .message-meta strong {
  color: #6b4f1d;
}

.event-label {
  padding: 2px 7px;
  border-radius: var(--ds-radius-full);
  background: #e8eef8;
  color: #52627a;
  font-size: 0.78rem;
  font-weight: 700;
}

.debate-message p {
  margin: 0;
  padding: 14px 16px;
  border: 1px solid #c9d7ea;
  border-radius: 3px 14px 14px;
  background: #eff5ff;
  color: var(--ds-color-on-surface);
  font-size: 1.25rem;
  line-height: 1.8;
}

.side-b p {
  border-color: #bdd2ff;
  border-radius: 14px 3px 14px 14px;
  background: #e7f0ff;
}

.side-judge p {
  border-color: #ddd0ac;
  border-radius: 14px;
  background: #fffaf0;
  text-align: center;
}

.debate-notice {
  margin: auto;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: var(--ds-color-on-surface-variant);
  font-size: 1rem;
}

@media (max-width: 680px) {
  .panel-header {
    padding: 12px 16px;
    align-items: flex-start;
    flex-direction: column;
  }

  .debate-list {
    padding: 18px 16px;
  }

  .debate-message {
    width: 92%;
  }

  .debate-message p {
    font-size: 1.05rem;
  }
}
</style>
