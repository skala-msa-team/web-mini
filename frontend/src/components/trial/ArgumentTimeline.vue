<script setup>
import { FileText, Gavel, Hourglass } from '@lucide/vue'

const props = defineProps({
  phase: { type: String, required: true },
  events: { type: Array, default: () => [] },
  waitingMessage: { type: String, required: true },
})

function formatEventTime(value) {
  if (!value) return ''
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return ''

  return new Intl.DateTimeFormat('ko-KR', {
    hour: '2-digit',
    minute: '2-digit',
    hour12: false,
  }).format(date)
}

function isCurrentEvent(index) {
  return index === props.events.length - 1
}
</script>

<template>
  <section class="argument-panel" aria-labelledby="argument-title">
    <header class="panel-header">
      <h2 id="argument-title"><FileText :size="20" /> AI 변론 진행 내역</h2>
      <span class="phase-badge">진행 단계: {{ phase }}</span>
    </header>

    <div class="argument-body">
      <p v-if="!events.length" class="empty-history">
        저장된 AI 발언을 불러오는 중입니다.
      </p>

      <article
        v-for="(event, index) in events"
        v-else
        :key="event.id"
        class="timeline-item"
        :class="{ current: isCurrentEvent(index) }"
      >
        <div class="timeline-icon"><Gavel :size="19" /></div>
        <div class="speech-wrap">
          <div class="speech-meta">
            <strong>{{ event.speaker }}</strong>
            <time v-if="event.occurredAt" :datetime="event.occurredAt">
              {{ formatEventTime(event.occurredAt) }}
            </time>
          </div>
          <span v-if="isCurrentEvent(index)" class="current-badge">{{ event.label }}</span>
          <p>“{{ event.content }}”</p>
        </div>
      </article>

      <p class="waiting"><Hourglass :size="16" /> {{ waitingMessage }}</p>
    </div>
  </section>
</template>

<style scoped>
.argument-panel {
  border: 1px solid var(--ds-color-outline-variant);
  border-radius: var(--ds-radius-md);
  background: white;
  overflow: hidden;
}

.panel-header {
  min-height: 58px;
  padding: 0 24px;
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

.phase-badge,
.current-badge {
  padding: 5px 10px;
  border-radius: var(--ds-radius-full);
  background: #dbeafe;
  color: #245284;
  font-size: 0.9rem;
  font-weight: 600;
  white-space: nowrap;
}

.argument-body {
  min-height: 456px;
  padding: 24px 28px 26px 78px;
}

.timeline-item {
  position: relative;
  margin-bottom: 30px;
}

.timeline-item:not(:last-of-type)::after {
  content: '';
  position: absolute;
  top: 40px;
  bottom: -28px;
  left: -40px;
  width: 1px;
  background: var(--ds-color-outline-variant);
}

.timeline-icon {
  position: absolute;
  left: -58px;
  top: 2px;
  display: grid;
  place-items: center;
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: #1f466f;
  color: white;
}

.speech-wrap {
  position: relative;
}

.speech-meta {
  min-height: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.speech-meta strong {
  color: var(--ds-color-primary);
  font-size: 1.05rem;
}

.speech-meta time {
  color: #8b95a5;
  font-size: 0.85rem;
}

.speech-wrap p {
  margin: 0;
  padding: 20px;
  border: 1px solid var(--ds-color-outline-variant);
  border-radius: 0 var(--ds-radius-md) var(--ds-radius-md) var(--ds-radius-md);
  color: var(--ds-color-on-surface);
  font-size: 1.25rem;
  line-height: 1.8;
}

.current-badge {
  position: absolute;
  right: 12px;
  top: 15px;
}

.waiting {
  margin: 26px 0 0;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #7f8998;
  font-size: 1rem;
}

.empty-history {
  margin: 0;
  padding: 42px 0;
  color: #7f8998;
  font-size: 1rem;
  text-align: center;
}

@media (max-width: 680px) {
  .panel-header {
    padding: 0 16px;
  }

  .argument-body {
    min-height: auto;
    padding: 22px 16px 24px 54px;
  }

  .timeline-icon {
    left: -44px;
    width: 32px;
    height: 32px;
  }

  .current-badge {
    position: static;
    display: inline-flex;
    margin-bottom: 6px;
  }

  .speech-wrap p {
    padding: 16px;
    font-size: 1.05rem;
  }
}
</style>
