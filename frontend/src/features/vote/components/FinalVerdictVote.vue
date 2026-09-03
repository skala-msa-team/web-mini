<script setup>
import { computed } from 'vue'
import { Check, Clock3, Scale } from '@lucide/vue'
import { VOTE_STATUS } from '@/constants/liveTrialUiStatus.js'

const props = defineProps({
  choices: { type: Array, required: true },
  remainingTime: { type: String, required: true },
  selectedChoice: { type: String, default: null },
  status: { type: String, default: VOTE_STATUS.WAITING },
  disabled: { type: Boolean, default: false },
  disabledMessage: { type: String, default: '현재 투표할 수 없습니다.' },
})

defineEmits(['select', 'submit'])

const statusLabel = computed(() => {
  if (props.status === VOTE_STATUS.OPEN) return '투표 진행 중'
  if (props.status === VOTE_STATUS.SUBMITTED) return '제출 완료'
  return '투표 대기'
})
const choicesDisabled = computed(
  () => props.disabled || props.status !== VOTE_STATUS.OPEN,
)
const submitDisabled = computed(
  () => choicesDisabled.value || !props.selectedChoice,
)
const helpMessage = computed(() => {
  if (props.disabled) return props.disabledMessage
  if (props.status === VOTE_STATUS.WAITING) {
    return '현재 변론이 진행 중입니다. 투표 시작 알림을 기다려 주세요.'
  }
  if (props.status === VOTE_STATUS.SUBMITTED) {
    return '투표가 정상적으로 제출되었습니다. 재판 종료 후 최종 결과가 공개됩니다.'
  }
  if (props.selectedChoice) return '선택한 승소 측을 확인한 뒤 투표를 제출해 주세요.'
  return 'A측과 B측 중 승소해야 한다고 판단한 한 쪽을 선택해 주세요.'
})
</script>

<template>
  <section class="final-vote" aria-labelledby="final-vote-title">
    <header class="vote-heading">
      <div>
        <span class="eyebrow"><Scale :size="15" /> 배심원 최종 판단</span>
        <h1 id="final-vote-title">최종 판결 투표</h1>
      </div>
      <div class="vote-meta">
        <span class="status-badge" :class="`status-${status.toLowerCase()}`">
          {{ statusLabel }}
        </span>
        <div class="countdown" aria-label="투표 마감까지 남은 시간">
          <Clock3 :size="19" />
          <span>남은 시간</span>
          <strong>{{ remainingTime }}</strong>
        </div>
      </div>
    </header>

    <div class="choice-guide" aria-hidden="true">
      <span>A측 승소</span>
      <span>B측 승소</span>
    </div>
    <div class="vote-progress" aria-hidden="true"><span></span></div>

    <div class="choice-list">
      <button
        v-for="choice in choices"
        :key="choice.id"
        type="button"
        class="choice-card"
        :class="{ selected: selectedChoice === choice.id }"
        :aria-pressed="selectedChoice === choice.id"
        :disabled="choicesDisabled"
        @click="$emit('select', choice.id)"
      >
        <span class="choice-side">{{ choice.side }}</span>
        <strong>{{ choice.title }}</strong>
        <small>{{ choice.description }}</small>
        <span v-if="selectedChoice === choice.id" class="selected-mark">
          <Check :size="14" />
          {{ status === VOTE_STATUS.SUBMITTED ? '제출 완료' : '선택됨' }}
        </span>
      </button>
    </div>

    <p class="vote-help" aria-live="polite">{{ helpMessage }}</p>

    <button
      class="submit-vote"
      :class="{ submitted: status === VOTE_STATUS.SUBMITTED }"
      type="button"
      :disabled="submitDisabled"
      @click="$emit('submit')"
    >
      <Check v-if="status === VOTE_STATUS.SUBMITTED" :size="17" />
      <Clock3 v-else-if="status === VOTE_STATUS.WAITING" :size="17" />
      <Scale v-else :size="17" />
      {{
        status === VOTE_STATUS.SUBMITTED
          ? '투표 제출 완료'
          : status === VOTE_STATUS.WAITING
            ? '투표 대기 중'
            : '선택한 내용으로 투표하기'
      }}
    </button>
  </section>
</template>

<style scoped>
.final-vote {
  padding: 24px 26px 20px;
  border: 1px solid var(--ds-color-outline-variant);
  border-radius: var(--ds-radius-md);
  background: white;
  box-shadow: var(--ds-shadow-interactive);
}

.vote-heading {
  padding-bottom: 14px;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
  border-bottom: 1px solid var(--ds-color-card-border);
}

.eyebrow {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 5px;
  color: var(--ds-color-justice-blue);
  font-size: 0.95rem;
  font-weight: 700;
}

h1 {
  margin: 0;
  color: var(--ds-color-primary);
  font-size: 1.5rem;
}

.countdown {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--ds-color-justice-blue);
  font-size: 0.95rem;
  font-weight: 600;
}

.countdown span {
  color: var(--ds-color-on-surface-variant);
}

.countdown strong {
  font-size: 1.1rem;
  letter-spacing: 0.04em;
}

.vote-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.status-badge {
  min-height: 25px;
  padding: 0 10px;
  display: inline-flex;
  align-items: center;
  border-radius: var(--ds-radius-full);
  background: #eef2f7;
  color: #687486;
  font-size: 0.9rem;
  font-weight: 700;
}

.status-open {
  background: #e8f1ff;
  color: var(--ds-color-justice-blue);
}

.status-submitted {
  background: #e8f7ed;
  color: #207443;
}

.choice-guide {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  color: var(--ds-color-on-surface-variant);
  font-size: 0.95rem;
}

.vote-progress {
  height: 34px;
  margin-top: 7px;
  border: 1px solid #bbcae0;
  border-radius: var(--ds-radius-default);
  background: #e7effc;
  overflow: hidden;
}

.vote-progress span {
  display: block;
  width: 50%;
  height: 100%;
  background: linear-gradient(90deg, #cedfff, #dce8fa);
}

.choice-list {
  margin-top: 30px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.choice-card {
  position: relative;
  min-height: 104px;
  padding: 18px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 7px;
  border: 1px solid var(--ds-color-outline-variant);
  border-radius: var(--ds-radius-default);
  background: white;
  color: var(--ds-color-primary);
  cursor: pointer;
  transition: border-color 160ms ease, background 160ms ease, box-shadow 160ms ease, transform 160ms ease;
}

.choice-card:hover {
  border-color: var(--ds-color-justice-blue);
  transform: translateY(-1px);
  box-shadow: 0 8px 20px rgb(26 54 93 / 10%);
}

.choice-card:focus-visible {
  outline: 3px solid rgb(37 99 235 / 22%);
  outline-offset: 2px;
}

.choice-card:disabled {
  border-color: var(--ds-color-outline-variant);
  background: var(--ds-color-surface-container-low);
  box-shadow: none;
  cursor: not-allowed;
  opacity: 0.68;
  transform: none;
}

.choice-card.selected {
  border-color: var(--ds-color-justice-blue);
  background: #eef5ff;
  box-shadow: inset 0 0 0 1px var(--ds-color-justice-blue);
}

.choice-card strong {
  font-size: 1.2rem;
}

.choice-card small {
  color: var(--ds-color-on-surface-variant);
  font-size: 1rem;
}

.choice-side {
  position: absolute;
  top: 10px;
  left: 12px;
  color: #728096;
  font-size: 0.9rem;
  font-weight: 700;
}

.selected-mark {
  position: absolute;
  top: 9px;
  right: 10px;
  display: inline-flex;
  align-items: center;
  gap: 3px;
  color: var(--ds-color-justice-blue);
  font-size: 0.9rem;
  font-weight: 700;
}

.vote-help {
  margin: 14px 0 0;
  color: #7f8998;
  font-size: 0.95rem;
  text-align: center;
}

.submit-vote {
  width: 100%;
  min-height: 44px;
  margin-top: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 7px;
  border: 0;
  border-radius: var(--ds-radius-default);
  background: var(--ds-color-justice-blue);
  color: white;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
}

.submit-vote.submitted {
  background: #207443;
}

.submit-vote:disabled {
  background: #d9e0e9;
  color: #7d8795;
  cursor: not-allowed;
}

.submit-vote.submitted:disabled {
  background: #e8f7ed;
  color: #207443;
}

@media (max-width: 620px) {
  .final-vote {
    padding: 20px 16px;
  }

  .vote-heading {
    align-items: flex-start;
  }

  .vote-meta {
    flex-direction: column;
    align-items: flex-end;
    gap: 6px;
  }

  .countdown span {
    display: none;
  }

  .choice-list {
    grid-template-columns: 1fr;
    margin-top: 20px;
  }
}
</style>
