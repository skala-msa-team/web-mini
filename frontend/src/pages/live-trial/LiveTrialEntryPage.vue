<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Clock3, Eye, UsersRound } from '@lucide/vue'
import AppFooter from '@/components/layout/AppFooter.vue'
import AppHeader from '@/components/layout/AppHeader.vue'
import { CONNECTION_STATUS } from '@/constants/liveTrialUiStatus.js'
import { useLiveTrialMockState } from '@/composables/useLiveTrialMockState.js'
import { useLiveTrialRealtime } from '@/composables/useLiveTrialRealtime.js'
import { useTrialCountdown } from '@/composables/useTrialCountdown.js'
import TrialChatPanel from '@/features/chat/components/TrialChatPanel.vue'
import ArgumentTimeline from '@/features/trial/components/ArgumentTimeline.vue'
import TrialConnectionStatus from '@/features/trial/components/TrialConnectionStatus.vue'
import TrialStage from '@/features/trial/components/TrialStage.vue'
import { recoverLiveTrial } from '@/realtime/liveTrialRecovery.js'
import { liveTrialMock } from '@/features/trial/liveTrialMock.js'
import { getLiveTrialTimelineMock } from '@/features/trial/liveTrialTimelineMock.js'
import {
  LIVE_TRIAL_MOCK_SCENARIO,
  LIVE_TRIAL_TIMING,
} from '@/features/trial/liveTrialStateMock.js'
import { mergeMessages } from '@/utils/messageMerge.js'

const route = useRoute()
const router = useRouter()
const trialId = computed(() => route.params.trialId ?? liveTrialMock.id)
const liveMessages = ref([...liveTrialMock.messages])
const realtimeSnapshot = ref(null)
const realtimeEvents = ref([])

const {
  state: trialState,
  interactionDisabledMessage: stateInteractionDisabledMessage,
} = useLiveTrialMockState(LIVE_TRIAL_MOCK_SCENARIO.ARGUMENT)

function toChatMessage(payload) {
  const sender = payload.sender || {}
  return {
    id: payload.messageId ?? payload.messageSequence,
    avatar: (sender.nickname || '배').slice(0, 1),
    nickname: sender.nickname || '관전자',
    message: payload.content || '',
    tone: 'blue',
  }
}

function appendMessage(payload) {
  liveMessages.value = mergeMessages(liveMessages.value, [toChatMessage(payload)])
}

function appendEvent(event) {
  if (!realtimeEvents.value.some((savedEvent) => savedEvent.sequence === event.sequence)) {
    realtimeEvents.value.push(event)
  }
}

async function recoverConnection({ trialId: currentTrialId }) {
  await recoverLiveTrial({
    trialId: currentTrialId,
    onSnapshot: (snapshot) => { realtimeSnapshot.value = snapshot },
    onEvent: appendEvent,
    onMessage: appendMessage,
  })
}

const realtime = useLiveTrialRealtime(trialId, {
  onConnected: recoverConnection,
  onReconnect: recoverConnection,
  onEvent: appendEvent,
  onMessage: appendMessage,
})
const realtimeConnection = realtime.connection
const realtimeErrors = realtime.userErrors
const sendChat = realtime.sendChat
const reconnect = realtime.reconnect

const currentSnapshot = computed(() => realtimeSnapshot.value || trialState.value.snapshot)
const trialEndsAt = computed(() => currentSnapshot.value?.scheduledEndAt)
const { remainingSeconds, formattedRemainingTime, isExpired } = useTrialCountdown(trialEndsAt)
const trialEnded = computed(() => Boolean(currentSnapshot.value?.ended || isExpired.value))
const timeline = computed(() => getLiveTrialTimelineMock(remainingSeconds.value))
const phaseLabel = computed(() => {
  if (trialEnded.value) return '재판 종료'
  return timeline.value.phase
})
const interactionsDisabled = computed(
  () => realtime.connection.value.status !== CONNECTION_STATUS.CONNECTED || isExpired.value,
)
const interactionDisabledMessage = computed(() =>
  isExpired.value ? '재판 시간이 종료되었습니다.' : stateInteractionDisabledMessage.value,
)

watch(
  remainingSeconds,
  (seconds) => {
    if (
      seconds === null ||
      realtime.connection.value.status !== CONNECTION_STATUS.CONNECTED ||
      currentSnapshot.value?.ended
    ) {
      return
    }

    if (seconds === 0) {
      router.replace({ name: 'trial-result', params: { trialId: route.params.trialId } })
      return
    }

    if (seconds <= LIVE_TRIAL_TIMING.VOTING_SECONDS) {
      router.replace({ name: 'trial-voting', params: { trialId: route.params.trialId } })
    }
  },
  { immediate: true },
)
</script>

<template>
  <div id="live-trial" class="live-trial-page">
    <AppHeader />

    <main class="page-shell">
      <TrialConnectionStatus
        :connection="realtimeConnection"
        :ended="trialEnded"
        @retry="reconnect"
      />

      <p v-if="realtimeErrors.length" class="realtime-error" role="alert">
        {{ realtimeErrors.at(-1)?.message || '실시간 요청을 처리하지 못했습니다.' }}
      </p>

      <section class="trial-summary" aria-labelledby="trial-title">
        <div class="summary-copy">
          <div class="summary-badges">
            <span class="live-badge" :class="{ ended: trialEnded }">
              <i aria-hidden="true"></i>{{ trialEnded ? '종료' : '실시간' }}
            </span>
            <span class="view-badge"><Eye :size="14" />{{ liveTrialMock.viewCount }}</span>
          </div>
          <h1 id="trial-title">“{{ liveTrialMock.title }}”</h1>
        </div>

        <div class="summary-stats">
          <div class="stat-card timer-card">
            <Clock3 :size="21" />
            <span>
              <small>{{ liveTrialMock.sessionTime }}</small>
              <strong>{{ formattedRemainingTime }}</strong>
            </span>
          </div>
          <div class="stat-card audience-card">
            <UsersRound :size="21" />
            <span>
              <small>참여 배심원</small>
              <strong>{{ liveTrialMock.audienceCount.toLocaleString('ko-KR') }}명</strong>
            </span>
          </div>
        </div>
      </section>

      <div class="trial-layout">
        <div class="trial-main-column">
          <TrialStage :participants="liveTrialMock.participants" />
          <ArgumentTimeline
            :phase="phaseLabel"
            :notice="timeline.notice"
            :argument="timeline.argument"
            :summary="timeline.summary"
            :current-label="timeline.currentLabel"
            :waiting-message="timeline.waitingMessage"
          />

        </div>

        <TrialChatPanel
          :initial-messages="liveMessages"
          :messages="liveMessages"
          :audience-count="liveTrialMock.audienceCount"
          :header-label="trialEnded ? '종료' : ''"
          :disabled="interactionsDisabled"
          :disabled-message="interactionDisabledMessage"
          :on-send="sendChat"
        />
      </div>
    </main>

    <AppFooter />
  </div>
</template>

<style scoped>
.live-trial-page {
  min-height: 100vh;
  background: var(--ds-color-page-background);
}

.page-shell {
  width: min(calc(100% - 32px), var(--ds-container-max));
  margin: 0 auto;
  padding: 18px 0 20px;
}

.trial-summary {
  min-height: 94px;
  padding: 18px 18px 18px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  border: 1px solid var(--ds-color-outline-variant);
  border-radius: var(--ds-radius-md);
  background: white;
}

.summary-badges {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.live-badge,
.view-badge {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  min-height: 24px;
  padding: 0 10px;
  border-radius: var(--ds-radius-full);
  font-size: 0.69rem;
  font-weight: 700;
}

.live-badge {
  background: #ca2330;
  color: white;
}

.live-badge.ended {
  background: #687486;
}

.live-badge i {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: white;
  box-shadow: 0 0 0 4px rgb(255 255 255 / 16%);
}

.view-badge {
  background: #edf2f9;
  color: var(--ds-color-on-surface-variant);
}

h1 {
  margin: 0;
  color: var(--ds-color-primary);
  font-size: clamp(1.2rem, 2vw, 1.55rem);
  line-height: 1.35;
}

.summary-stats {
  display: flex;
  align-items: stretch;
  gap: 12px;
}

.stat-card {
  min-width: 142px;
  min-height: 54px;
  padding: 9px 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border: 1px solid #dae3f0;
  border-radius: var(--ds-radius-default);
  background: #f3f7ff;
  box-shadow: 0 2px 5px rgb(26 54 93 / 7%);
  color: #6e7887;
}

.stat-card span {
  display: flex;
  flex-direction: column;
}

.stat-card small {
  color: var(--ds-color-on-surface-variant);
  font-size: 0.68rem;
}

.stat-card strong {
  color: var(--ds-color-primary);
  font-size: 0.83rem;
}

.timer-card strong {
  color: var(--ds-color-justice-blue);
  letter-spacing: 0.05em;
}

.trial-layout {
  margin-top: 14px;
  display: grid;
  grid-template-columns: minmax(0, 2fr) minmax(300px, 1fr);
  align-items: stretch;
}

.trial-main-column {
  display: grid;
  gap: 14px;
}

.trial-main-column > :deep(*) {
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}

.trial-layout > :deep(.chat-panel) {
  margin-left: -1px;
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}

@media (max-width: 960px) {
  .trial-summary {
    align-items: flex-start;
  }

  .trial-layout {
    grid-template-columns: 1fr;
    gap: 14px;
  }

  .trial-main-column > :deep(*) {
    border-radius: var(--ds-radius-md);
  }

  .trial-layout > :deep(.chat-panel) {
    margin-left: 0;
    border-radius: var(--ds-radius-md);
  }
}

@media (max-width: 680px) {
  .page-shell {
    width: min(calc(100% - 24px), var(--ds-container-max));
    padding-top: 12px;
  }

  .trial-summary {
    flex-direction: column;
    padding: 16px;
  }

  .summary-stats {
    width: 100%;
  }

  .stat-card {
    flex: 1;
    min-width: 0;
    padding: 8px;
  }

  .audience-card {
    display: none;
  }
}
</style>
