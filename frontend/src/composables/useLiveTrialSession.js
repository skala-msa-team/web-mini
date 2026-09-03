import { computed, ref } from 'vue'
import { trialApi } from '@/api/trialApi.js'
import { useLiveTrialRealtime } from '@/composables/useLiveTrialRealtime.js'
import { CONNECTION_STATUS } from '@/constants/liveTrialUiStatus.js'
import { recoverLiveTrial } from '@/realtime/liveTrialRecovery.js'
import {
  applyEventsToSnapshot,
  getLastContiguousEventSequence,
  mergeTrialEvents,
} from '@/utils/trialEvent.js'

export function useLiveTrialSession(trialId, options = {}) {
  const detail = ref(null)
  const snapshot = ref(null)
  const events = ref([])
  const restoring = ref(true)
  const hasRecovered = ref(false)

  function appendEvent(event) {
    events.value = mergeTrialEvents(events.value, [event])
    options.onEvent?.(event)
  }

  async function recoverConnection({ trialId: currentTrialId }) {
    restoring.value = true
    const afterEventSequence = getLastContiguousEventSequence(events.value)
    const detailRequest = detail.value
      ? Promise.resolve(detail.value)
      : trialApi.getTrial(currentTrialId)

    try {
      const [nextDetail] = await Promise.all([
        detailRequest,
        recoverLiveTrial({
          trialId: currentTrialId,
          afterEventSequence,
          onSnapshot: (nextSnapshot) => {
            snapshot.value = nextSnapshot
          },
          onEvent: appendEvent,
        }),
      ])

      detail.value = nextDetail
      hasRecovered.value = true
    } finally {
      restoring.value = false
    }
  }

  const realtime = useLiveTrialRealtime(trialId, {
    ...options,
    onConnected: recoverConnection,
    onReconnect: recoverConnection,
    onEvent: appendEvent,
  })

  const currentSnapshot = computed(() => applyEventsToSnapshot(snapshot.value, events.value))
  const status = computed(() => currentSnapshot.value?.status ?? null)
  const connection = computed(() => {
    if (restoring.value && realtime.connection.value.status === CONNECTION_STATUS.CONNECTED) {
      return {
        status: hasRecovered.value
          ? CONNECTION_STATUS.RECONNECTING
          : CONNECTION_STATUS.CONNECTING,
        error: null,
      }
    }

    return realtime.connection.value
  })

  return {
    detail,
    snapshot,
    events,
    currentSnapshot,
    status,
    restoring,
    ...realtime,
    connection,
  }
}
