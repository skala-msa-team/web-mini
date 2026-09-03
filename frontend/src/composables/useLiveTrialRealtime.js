import { onBeforeUnmount, onMounted, ref, unref } from 'vue'
import { CONNECTION_STATUS } from '@/consts/liveTrialUiStatus.js'
import { createStompClient, readMessage, STOMP_DESTINATION, subscribeLiveTrial } from '@/lib/realtime.js'
import { useDemoUser } from './useDemoUser.js'

export function useLiveTrialRealtime(trialId, options = {}) {
  const connection = ref({ status: CONNECTION_STATUS.CONNECTING, error: null })
  const lastError = ref(null)
  const userErrors = ref([])
  const { getDemoUserId } = useDemoUser()
  const client = options.client || createStompClient({
    userId: getDemoUserId(),
    brokerURL: options.brokerURL,
  })
  let unsubscribeSubscriptions = () => {}
  let removeStatusListener = () => {}
  let removeConnectedListener = () => {}

  function currentTrialId() {
    return unref(trialId)
  }

  function onConnected({ reconnected }) {
    const callback = reconnected ? options.onReconnect : options.onConnected
    Promise.resolve(callback?.({ client, trialId: currentTrialId() })).catch((error) => {
      reportConnectionError(error)
    })
  }

  function onError(frame) {
    reportUserError(readMessage(frame))
  }

  function reportUserError(error) {
    lastError.value = error
    userErrors.value.push(error)
    options.onError?.(error)
  }

  function reportConnectionError(error) {
    reportUserError(error)
    connection.value = { status: CONNECTION_STATUS.ERROR, error }
  }

  function connect() {
    if (client.active) return

    unsubscribeSubscriptions = subscribeLiveTrial(client, currentTrialId(), {
      onEvent: (frame) => options.onEvent?.(readMessage(frame), frame),
      onMessage: (frame) => options.onMessage?.(readMessage(frame), frame),
      onError,
    })
    removeStatusListener = client.onStatusChange((nextStatus, error) => {
      connection.value = { status: nextStatus, error }
      if (error) lastError.value = error
    })
    removeConnectedListener = client.onConnected(onConnected)
    client.activate()
  }

  function sendChat(content) {
    return client.publish(STOMP_DESTINATION.sendChat(currentTrialId()), { content })
  }

  async function reconnect() {
    unsubscribeSubscriptions()
    removeStatusListener()
    removeConnectedListener()
    await client.deactivate()
    connect()
  }

  onMounted(connect)
  onBeforeUnmount(async () => {
    unsubscribeSubscriptions()
    removeStatusListener()
    removeConnectedListener()
    await client.deactivate()
  })

  return {
    connection,
    lastError,
    userErrors,
    sendChat,
    reconnect,
    client,
  }
}
