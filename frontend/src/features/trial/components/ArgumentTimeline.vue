<script setup>
import { FileText, Gavel, Hourglass, Scale } from '@lucide/vue'

defineProps({
  phase: { type: String, required: true },
  notice: { type: String, required: true },
  argument: { type: Object, required: true },
  summary: { type: Object, required: true },
})
</script>

<template>
  <section class="argument-panel" aria-labelledby="argument-title">
    <header class="panel-header">
      <h2 id="argument-title"><FileText :size="20" /> AI 변론 진행 내역</h2>
      <span class="phase-badge">진행 단계: {{ phase }}</span>
    </header>

    <div class="argument-body">
      <div class="judge-notice">
        <p><Scale :size="17" /> “{{ notice }}”</p>
        <small>AI 판사 · 12:11</small>
      </div>

      <article class="timeline-item current">
        <div class="timeline-icon"><Gavel :size="19" /></div>
        <div class="speech-wrap">
          <strong>{{ argument.speaker }}</strong>
          <span class="current-badge">현재 변론 내용</span>
          <p>“{{ argument.content }}”</p>
        </div>
      </article>

      <article class="summary-card">
        <h3><FileText :size="16" /> {{ summary.title }}</h3>
        <p>“{{ summary.content }}”</p>
        <div class="progress-track" aria-label="B측 주장 요약 준비 진행률">
          <span :style="{ width: `${summary.progress}%` }"></span>
        </div>
      </article>

      <p class="waiting"><Hourglass :size="16" /> AI 변호사 B가 반론을 준비 중입니다...</p>
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
  font-size: 1rem;
}

.phase-badge,
.current-badge {
  padding: 5px 10px;
  border-radius: var(--ds-radius-full);
  background: #dbeafe;
  color: #245284;
  font-size: 0.69rem;
  font-weight: 600;
  white-space: nowrap;
}

.argument-body {
  min-height: 456px;
  padding: 24px 28px 26px 78px;
}

.judge-notice {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 28px;
}

.judge-notice p {
  margin: 0;
  padding: 10px 18px;
  display: inline-flex;
  align-items: center;
  gap: 7px;
  border: 1px solid #d2deef;
  border-radius: var(--ds-radius-full);
  background: #edf4ff;
  color: var(--ds-color-on-surface-variant);
  font-size: 0.8rem;
}

.judge-notice small {
  margin-top: 6px;
  color: #8b95a5;
  font-size: 0.65rem;
}

.timeline-item {
  position: relative;
  margin-bottom: 30px;
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

.speech-wrap strong {
  display: block;
  margin-bottom: -1px;
  color: var(--ds-color-primary);
  font-size: 0.76rem;
}

.speech-wrap p {
  margin: 0;
  padding: 20px;
  border: 1px solid var(--ds-color-outline-variant);
  border-radius: 0 var(--ds-radius-md) var(--ds-radius-md) var(--ds-radius-md);
  color: var(--ds-color-on-surface);
  font-size: 0.88rem;
  line-height: 1.75;
}

.current-badge {
  position: absolute;
  right: 12px;
  top: 15px;
}

.summary-card {
  padding: 15px 17px 16px;
  border: 1px solid var(--ds-color-outline-variant);
  border-left: 4px solid var(--ds-color-justice-blue);
  border-radius: var(--ds-radius-default);
}

.summary-card h3 {
  margin: 0 0 9px;
  display: flex;
  align-items: center;
  gap: 7px;
  color: var(--ds-color-justice-blue);
  font-family: var(--ds-font-body);
  font-size: 0.76rem;
}

.summary-card p {
  margin: 0 0 14px;
  color: var(--ds-color-on-surface-variant);
  font-size: 0.75rem;
}

.progress-track {
  height: 5px;
  border-radius: var(--ds-radius-full);
  background: #cfe0f8;
  overflow: hidden;
}

.progress-track span {
  display: block;
  height: 100%;
  border-radius: inherit;
  background: var(--ds-color-justice-blue);
}

.waiting {
  margin: 36px 0 0;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #7f8998;
  font-size: 0.78rem;
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
}
</style>
