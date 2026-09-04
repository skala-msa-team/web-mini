import { onBeforeUnmount, watch } from 'vue'
import { createStompClient, readMessage, STOMP_DESTINATION } from '@/lib/realtime.js'
import { getDemoUserId } from '@/composables/useDemoUser.js'

export function useLiveTrialPresenceList(trials, onPresenceUpdate) {
  const client = createStompClient({ userId: getDemoUserId() })
  let unsubscribePresence = []
  let removeConnectedListener = () => {}

  function trialIds() {
    return trials.value
      .filter((trial) => !trial.isMock && trial.id)
      .map((trial) => trial.id)
  }

  function clearSubscriptions() {
    unsubscribePresence.forEach((unsubscribe) => unsubscribe())
    unsubscribePresence = []
  }

  function subscribePresence() {
    if (!client.connected) return

    clearSubscriptions()
    unsubscribePresence = trialIds().map((trialId) =>
      client.subscribe(STOMP_DESTINATION.trialPresence(trialId), (frame) => {
        const payload = readMessage(frame)
        const audienceCount = Number(payload?.audienceCount)
        if (!Number.isFinite(audienceCount)) return

        onPresenceUpdate?.({
          trialId: payload?.trialId ?? trialId,
          audienceCount,
        })
      }),
    )
  }

  removeConnectedListener = client.onConnected(subscribePresence)

  watch(
    () => trialIds().join(','),
    () => {
      if (!trialIds().length) {
        clearSubscriptions()
        return
      }

      if (!client.active) {
        client.activate()
        return
      }

      subscribePresence()
    },
    { immediate: true },
  )

  onBeforeUnmount(async () => {
    clearSubscriptions()
    removeConnectedListener()
    await client.deactivate()
  })
}
