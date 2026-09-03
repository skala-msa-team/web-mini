<script setup>
import { Check, Clock3, Scale } from '@lucide/vue'

defineProps({
  choices: { type: Array, required: true },
  remainingTime: { type: String, required: true },
  selectedChoice: { type: String, default: null },
})

defineEmits(['select'])
</script>

<template>
  <section class="final-vote" aria-labelledby="final-vote-title">
    <header class="vote-heading">
      <div>
        <span class="eyebrow"><Scale :size="15" /> 배심원 최종 판단</span>
        <h1 id="final-vote-title">최종 판결 투표</h1>
      </div>
      <div class="countdown" aria-label="투표 마감까지 남은 시간">
        <Clock3 :size="19" />
        <span>남은 시간</span>
        <strong>{{ remainingTime }}</strong>
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
        @click="$emit('select', choice.id)"
      >
        <span class="choice-side">{{ choice.side }}</span>
        <strong>{{ choice.title }}</strong>
        <small>{{ choice.description }}</small>
        <span v-if="selectedChoice === choice.id" class="selected-mark">
          <Check :size="14" /> 선택됨
        </span>
      </button>
    </div>

    <p class="vote-help">
      {{ selectedChoice ? '선택이 완료되었습니다. 투표 종료 전까지 변경할 수 있습니다.' : '두 입장을 검토한 뒤 한 쪽을 선택해 주세요.' }}
    </p>
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
  font-size: 0.69rem;
  font-weight: 700;
}

h1 {
  margin: 0;
  color: var(--ds-color-primary);
  font-size: 1.22rem;
}

.countdown {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--ds-color-justice-blue);
  font-size: 0.72rem;
  font-weight: 600;
}

.countdown span {
  color: var(--ds-color-on-surface-variant);
}

.countdown strong {
  font-size: 0.84rem;
  letter-spacing: 0.04em;
}

.choice-guide {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  color: var(--ds-color-on-surface-variant);
  font-size: 0.68rem;
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

.choice-card.selected {
  border-color: var(--ds-color-justice-blue);
  background: #eef5ff;
  box-shadow: inset 0 0 0 1px var(--ds-color-justice-blue);
}

.choice-card strong {
  font-size: 0.98rem;
}

.choice-card small {
  color: var(--ds-color-on-surface-variant);
  font-size: 0.76rem;
}

.choice-side {
  position: absolute;
  top: 10px;
  left: 12px;
  color: #728096;
  font-size: 0.65rem;
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
  font-size: 0.65rem;
  font-weight: 700;
}

.vote-help {
  margin: 14px 0 0;
  color: #7f8998;
  font-size: 0.7rem;
  text-align: center;
}

@media (max-width: 620px) {
  .final-vote {
    padding: 20px 16px;
  }

  .vote-heading {
    align-items: flex-start;
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
