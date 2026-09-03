<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Eye } from '@lucide/vue'
import { CONNECTION_STATUS, VOTE_STATUS } from '@/constants/liveTrialUiStatus.js'
import { TRIAL_STATUS } from '@/constants/trialStatus.js'
import { useLiveTrialSession } from '@/composables/useLiveTrialSession.js'
import { useTrialCountdown } from '@/composables/useTrialCountdown.js'
import TrialChatPanel from '@/features/chat/components/TrialChatPanel.vue'
import TrialConnectionStatus from '@/features/trial/components/TrialConnectionStatus.vue'
import FinalVerdictVote from '@/features/vote/components/FinalVerdictVote.vue'
import { finalVoteMock } from '@/features/vote/finalVoteMock.js'

const route = useRoute()
const router = useRouter()
const trialId = computed(() => route.params.trialId)
const session = useLiveTrialSession(trialId)

const trialEndsAt = computed(() => session.currentSnapshot.value?.phaseEndsAt)
const { formattedRemainingTime } = useTrialCountdown(trialEndsAt)
const trialEnded = computed(
  () => session.status.value === TRIAL_STATUS.ENDED || session.currentSnapshot.value?.ended,
)
const interactionsDisabled = computed(
  () => session.connection.value.status !== CONNECTION_STATUS.CONNECTED || trialEnded.value,
)
const interactionDisabledMessage = computed(() => {
  if (trialEnded.value) return '재판이 종료되었습니다.'
  if (session.connection.value.status !== CONNECTION_STATUS.CONNECTED) {
    return '재판 연결을 복구한 뒤 다시 시도해 주세요.'
  }
  return ''
})
const submittedVote = ref(null)
const selectedChoice = ref(null)
const voteStatus = computed(() =>
  submittedVote.value
    ? VOTE_STATUS.SUBMITTED
    : session.currentSnapshot.value?.voteOpen
      ? VOTE_STATUS.OPEN
      : VOTE_STATUS.WAITING,
)
const voteDisabled = computed(
  () => interactionsDisabled.value || voteStatus.value !== VOTE_STATUS.OPEN,
)
const voteDisabledMessage = computed(() => {
  if (interactionsDisabled.value) return interactionDisabledMessage.value
  if (voteStatus.value === VOTE_STATUS.WAITING) return '최종 투표가 아직 시작되지 않았습니다.'
  if (voteStatus.value === VOTE_STATUS.SUBMITTED) return '투표 제출이 완료되었습니다.'
  return ''
})

watch(
  [() => session.status.value, () => session.restoring.value],
  ([status, restoring]) => {
    if (restoring) return

    if (status === TRIAL_STATUS.ENDED) {
      router.replace({ name: 'trial-result', params: { trialId: route.params.trialId } })
      return
    }

    if (status && ![TRIAL_STATUS.VOTING, TRIAL_STATUS.VERDICT].includes(status)) {
      router.replace({ name: 'live-trial', params: { trialId: route.params.trialId } })
    }
  },
  { immediate: true },
)

function selectChoice(choiceId) {
  if (voteDisabled.value) return
  selectedChoice.value = choiceId
}

function submitVote() {
  if (voteDisabled.value || !selectedChoice.value) return

  submittedVote.value = Object.freeze({
    selectedSide: selectedChoice.value.replace('SIDE_', ''),
    votedAt: new Date().toISOString(),
  })
}
</script>

<template>
  <div class="voting-page">
    <main class="voting-shell">
      <TrialConnectionStatus
        :connection="session.connection.value"
        :ended="trialEnded"
        @retry="session.reconnect"
      />

      <p v-if="session.chatError.value" class="realtime-error" role="alert">
        {{ session.chatError.value?.message || '실시간 요청을 처리하지 못했습니다.' }}
      </p>

      <div class="voting-layout">
        <div class="voting-main">
          <section class="courtroom-stream" aria-labelledby="stream-title">
            <img src="/images/final-vote-courtroom.png" alt="최종 투표를 진행 중인 AI 재판장" />
            <div class="live-indicator" :class="{ ended: trialEnded }">
              <i aria-hidden="true"></i>
              <span>{{ trialEnded ? 'ENDED' : 'LIVE' }}</span>
              <Eye :size="14" />
              <strong>{{ finalVoteMock.viewerCount.toLocaleString('ko-KR') }}</strong>
            </div>
            <div class="stream-caption">
              <span>FINAL VOTE · 사랑과 전쟁터</span>
              <h1 id="stream-title">사건 #{{ finalVoteMock.caseNumber }}: {{ finalVoteMock.title }}</h1>
              <p>{{ finalVoteMock.subtitle }}</p>
            </div>
          </section>

          <FinalVerdictVote
            :choices="finalVoteMock.choices"
            :remaining-time="formattedRemainingTime"
            :selected-choice="selectedChoice"
            :status="voteStatus"
            :disabled="interactionsDisabled"
            :disabled-message="voteDisabledMessage"
            @select="selectChoice"
            @submit="submitVote"
          />
        </div>

        <TrialChatPanel
          :messages="session.messages.value"
          :audience-count="finalVoteMock.viewerCount"
          :header-label="trialEnded ? '종료' : '실시간'"
          :disabled="interactionsDisabled"
          :loading="session.chatRestoring.value"
          :sending="session.chatSending.value"
          :disabled-message="interactionDisabledMessage"
          :on-send="session.sendChat"
        />
      </div>
    </main>

  </div>
</template>

<style scoped>
.voting-page {
  min-height: 100vh;
  background: var(--ds-color-page-background);
}

.voting-shell {
  width: min(calc(100% - 32px), var(--ds-container-max));
  min-height: calc(100vh - 148px);
  margin: 0 auto;
  padding: 24px 0 34px;
}

.voting-layout {
  display: grid;
  grid-template-columns: minmax(0, 2fr) minmax(300px, 1fr);
  align-items: stretch;
  gap: 20px;
}

.voting-main {
  display: grid;
  align-content: start;
  gap: 18px;
}

.courtroom-stream {
  position: relative;
  aspect-ratio: 16 / 9;
  border: 1px solid var(--ds-color-outline-variant);
  border-radius: var(--ds-radius-md);
  background: #dce7f3;
  box-shadow: var(--ds-shadow-interactive);
  overflow: hidden;
}

.courtroom-stream::after {
  content: '';
  position: absolute;
  inset: 45% 0 0;
  background: linear-gradient(transparent, rgb(4 18 38 / 86%));
  pointer-events: none;
}

.courtroom-stream img {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
}

.live-indicator {
  position: absolute;
  z-index: 2;
  top: 12px;
  left: 12px;
  min-height: 28px;
  padding: 0 13px;
  display: inline-flex;
  align-items: center;
  gap: 7px;
  border-radius: var(--ds-radius-full);
  background: #c92532;
  color: white;
  font-size: 0.68rem;
}

.live-indicator i {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: white;
}

.live-indicator.ended {
  background: #687486;
}

.live-indicator span,
.live-indicator strong {
  font-weight: 700;
}

.stream-caption {
  position: absolute;
  z-index: 2;
  right: 0;
  bottom: 0;
  left: 0;
  padding: 22px 18px 16px;
  color: white;
}

.stream-caption span {
  font-size: 0.67rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  opacity: 0.76;
}

.stream-caption h1 {
  margin: 5px 0 3px;
  color: white;
  font-size: clamp(1.16rem, 2.3vw, 1.62rem);
  text-shadow: 0 2px 12px rgb(0 0 0 / 36%);
}

.stream-caption p {
  margin: 0;
  color: rgb(255 255 255 / 80%);
  font-size: 0.75rem;
}

.voting-layout > :deep(.chat-panel) {
  min-height: 100%;
}

@media (max-width: 960px) {
  .voting-layout {
    grid-template-columns: 1fr;
  }

  .voting-layout > :deep(.chat-panel) {
    min-height: 430px;
  }
}

@media (max-width: 620px) {
  .voting-shell {
    width: min(calc(100% - 24px), var(--ds-container-max));
    padding-top: 14px;
  }

  .courtroom-stream {
    aspect-ratio: 4 / 3;
  }

  .stream-caption p {
    display: none;
  }
}
</style>
