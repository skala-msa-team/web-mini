<script setup>
import { ref } from 'vue'
import { ArrowLeft, Check, Share2 } from '@lucide/vue'
import AppFooter from '@/components/layout/AppFooter.vue'
import AppHeader from '@/components/layout/AppHeader.vue'
import FaultRatioCard from '@/features/verdict/components/FaultRatioCard.vue'
import JudgmentGrounds from '@/features/verdict/components/JudgmentGrounds.vue'
import VerdictComparison from '@/features/verdict/components/VerdictComparison.vue'
import { trialResultMock } from '@/features/verdict/trialResultMock.js'

const shareCompleted = ref(false)

async function shareResult() {
  const shareData = {
    title: `사랑과 전쟁터 판결 ${trialResultMock.caseNumber}`,
    text: trialResultMock.title,
    url: window.location.href,
  }

  try {
    if (navigator.share) {
      await navigator.share(shareData)
    } else {
      await navigator.clipboard.writeText(window.location.href)
    }
    shareCompleted.value = true
  } catch {
    shareCompleted.value = false
  }
}
</script>

<template>
  <div class="result-page">
    <AppHeader />

    <main class="result-shell">
      <section class="result-hero" aria-labelledby="result-title">
        <span class="final-badge"><i aria-hidden="true"></i>최종 판결</span>
        <p>AI 판사의 판결이 확정되었습니다</p>
        <h1 id="result-title">{{ trialResultMock.caseNumber }}</h1>
        <p class="case-title">“{{ trialResultMock.title }}”</p>
        <div class="winner-badge"><Check :size="16" /> {{ trialResultMock.winner }} 승소</div>
      </section>

      <div class="judgment-grid">
        <FaultRatioCard :ratio="trialResultMock.faultRatio" />
        <JudgmentGrounds :grounds="trialResultMock.grounds" :judgment="trialResultMock.judgment" />
      </div>

      <VerdictComparison
        :ai-result="trialResultMock.aiResult"
        :jury-result="trialResultMock.juryResult"
      />

      <div class="result-actions">
        <button class="share-button" type="button" @click="shareResult">
          <Check v-if="shareCompleted" :size="17" />
          <Share2 v-else :size="17" />
          {{ shareCompleted ? '링크가 복사되었습니다' : '판결문 공유하기' }}
        </button>
        <RouterLink
          class="list-button"
          :to="{ name: 'live-trial', params: { trialId: $route.params.trialId } }"
        >
          <ArrowLeft :size="17" /> Live 재판으로 돌아가기
        </RouterLink>
      </div>

      <p class="legal-notice">
        본 결과는 갈등 해결을 돕기 위한 AI 의견이며 실제 법률 판결이나 법률 상담이 아닙니다.
      </p>
    </main>

    <AppFooter />
  </div>
</template>

<style scoped>
.result-page {
  min-height: 100vh;
  background:
    radial-gradient(circle at 50% 10%, rgb(219 232 255 / 48%), transparent 28%),
    var(--ds-color-page-background);
}

.result-shell {
  width: min(calc(100% - 32px), var(--ds-container-max));
  margin: 0 auto;
  padding: 40px 0 52px;
}

.result-hero {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.final-badge,
.winner-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 7px;
  border-radius: var(--ds-radius-full);
  font-size: 0.7rem;
  font-weight: 700;
}

.final-badge {
  min-height: 25px;
  padding: 0 13px;
  background: #c92532;
  color: white;
}

.final-badge i {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: white;
}

.result-hero > p:first-of-type {
  margin: 14px 0 4px;
  color: #788395;
  font-size: 0.68rem;
}

.result-hero h1 {
  margin: 0;
  color: var(--ds-color-primary);
  font-size: clamp(1.9rem, 4vw, 2.5rem);
  letter-spacing: 0.02em;
}

.case-title {
  margin: 8px 0 0;
  color: var(--ds-color-on-surface-variant);
  font-size: 0.88rem;
}

.winner-badge {
  margin-top: 14px;
  min-height: 30px;
  padding: 0 14px;
  background: #e8f7ed;
  color: #207443;
}

.judgment-grid {
  margin-top: 38px;
  display: grid;
  grid-template-columns: 1fr 1.18fr;
  gap: 22px;
}

.result-shell > :deep(.comparison-card) {
  margin-top: 24px;
}

.result-actions {
  margin-top: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.share-button,
.list-button {
  min-width: 180px;
  min-height: 46px;
  padding: 0 18px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border-radius: var(--ds-radius-default);
  font-size: 0.76rem;
  font-weight: 700;
  text-decoration: none;
  cursor: pointer;
}

.share-button {
  border: 1px solid var(--ds-color-justice-blue);
  background: white;
  color: var(--ds-color-justice-blue);
}

.list-button {
  border: 1px solid var(--ds-color-justice-blue);
  background: var(--ds-color-justice-blue);
  color: white;
}

.share-button:hover,
.list-button:hover {
  box-shadow: 0 7px 18px rgb(26 54 93 / 14%);
  transform: translateY(-1px);
}

.share-button:focus-visible,
.list-button:focus-visible {
  outline: 3px solid rgb(37 99 235 / 22%);
  outline-offset: 2px;
}

.legal-notice {
  margin: 18px 0 0;
  color: #8b95a4;
  font-size: 0.65rem;
  text-align: center;
}

@media (max-width: 900px) {
  .judgment-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 620px) {
  .result-shell {
    width: min(calc(100% - 24px), var(--ds-container-max));
    padding-top: 28px;
  }

  .judgment-grid {
    margin-top: 28px;
  }

  .result-actions {
    flex-direction: column;
  }

  .share-button,
  .list-button {
    width: 100%;
  }
}
</style>
