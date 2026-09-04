import { Client } from '@stomp/stompjs'
import { getEvents, getMessages, getSnapshot } from '@/apis/trialApi.js'
import { DEFAULT_MESSAGE_PAGE_SIZE } from '@/consts/api.js'
import { CONNECTION_STATUS } from '@/consts/liveTrialUiStatus.js'
import { STOMP_HEARTBEAT_MS, STOMP_RECONNECT_DELAYS_MS } from '@/consts/realtime.js'
import { mergeMessages } from '@/utils/messageMerge.js'

export const STOMP_DESTINATION = Object.freeze({
  endpoint: '/ws',
  trialEvents: (trialId) => `/topic/trials/${trialId}/events`,
  trialPresence: (trialId) => `/topic/trials/${trialId}/presence`,
  trialChat: (trialId) => `/topic/trials/${trialId}/chat`,
  errors: '/user/queue/errors',
  sendChat: (trialId) => `/app/trials/${trialId}/chat`,
})

function defaultBrokerUrl() {
  const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || window.location.origin
  const apiUrl = new URL(apiBaseUrl, window.location.origin)
  const brokerUrl = new URL(STOMP_DESTINATION.endpoint, apiUrl.origin)
  brokerUrl.protocol = brokerUrl.protocol === 'https:' ? 'wss:' : 'ws:'
  return brokerUrl.toString()
}

export function readMessage(frame) {
  try {
    return JSON.parse(frame.body)
  } catch {
    return frame.body
  }
}

export function createStompClient({ userId, brokerURL = defaultBrokerUrl() } = {}) {
  const statusListeners = new Set()
  const connectedListeners = new Set()
  const subscriptions = new Map()
  let status = CONNECTION_STATUS.CONNECTING
  let hasConnected = false
  let lastError = null
  let shouldReconnect = false
  let reconnectAttempt = 0
  let reconnectTimer = null

  function clearReconnectTimer() {
    if (reconnectTimer === null) return
    window.clearTimeout(reconnectTimer)
    reconnectTimer = null
  }

  function scheduleReconnect() {
    if (!shouldReconnect || reconnectTimer !== null) return
    const delay = STOMP_RECONNECT_DELAYS_MS[Math.min(reconnectAttempt, STOMP_RECONNECT_DELAYS_MS.length - 1)]
    reconnectAttempt += 1
    reconnectTimer = window.setTimeout(async () => {
      reconnectTimer = null
      if (!shouldReconnect) return
      await client.deactivate({ force: true })
      if (shouldReconnect) client.activate()
    }, delay)
  }

  const client = new Client({
    brokerURL,
    connectHeaders: { 'X-Demo-User-Id': userId },
    heartbeatIncoming: STOMP_HEARTBEAT_MS,
    heartbeatOutgoing: STOMP_HEARTBEAT_MS,
    reconnectDelay: 0,
    onConnect: () => {
      const reconnected = hasConnected
      hasConnected = true
      reconnectAttempt = 0
      clearReconnectTimer()
      lastError = null
      subscriptions.forEach((entry) => {
        entry.subscription = client.subscribe(entry.destination, entry.handler)
      })
      updateStatus(CONNECTION_STATUS.CONNECTED)
      connectedListeners.forEach((listener) => listener({ reconnected }))
    },
    onWebSocketClose: () => {
      if (!shouldReconnect) return
      updateStatus(CONNECTION_STATUS.RECONNECTING)
      scheduleReconnect()
    },
    onStompError: (frame) => {
      lastError = readMessage(frame)
      updateStatus(CONNECTION_STATUS.ERROR)
    },
    onWebSocketError: (error) => {
      lastError = error
      updateStatus(CONNECTION_STATUS.ERROR)
    },
  })

  function updateStatus(nextStatus) {
    status = nextStatus
    statusListeners.forEach((listener) => listener(status, lastError))
  }

  function subscribe(destination, handler) {
    if (subscriptions.has(destination)) return () => unsubscribe(destination)
    const entry = { destination, handler, subscription: null }
    subscriptions.set(destination, entry)
    if (client.connected) entry.subscription = client.subscribe(destination, handler)
    return () => unsubscribe(destination)
  }

  function unsubscribe(destination) {
    const entry = subscriptions.get(destination)
    if (!entry) return
    entry.subscription?.unsubscribe()
    subscriptions.delete(destination)
  }

  function activate() {
    if (client.active) return
    shouldReconnect = true
    updateStatus(hasConnected ? CONNECTION_STATUS.RECONNECTING : CONNECTION_STATUS.CONNECTING)
    client.activate()
  }

  async function deactivate() {
    shouldReconnect = false
    reconnectAttempt = 0
    clearReconnectTimer()
    subscriptions.forEach((entry) => entry.subscription?.unsubscribe())
    subscriptions.clear()
    await client.deactivate()
  }

  function publish(destination, body) {
    if (!client.connected) return false
    client.publish({ destination, body: JSON.stringify(body) })
    return true
  }

  return {
    activate, deactivate, subscribe, unsubscribe, publish,
    get connected() { return client.connected },
    get active() { return client.active },
    get status() { return status },
    get lastError() { return lastError },
    onStatusChange(listener) {
      statusListeners.add(listener)
      return () => statusListeners.delete(listener)
    },
    onConnected(listener) {
      connectedListeners.add(listener)
      return () => connectedListeners.delete(listener)
    },
  }
}

export function subscribeLiveTrial(client, trialId, handlers = {}) {
  const subscriptions = [
    [STOMP_DESTINATION.trialEvents(trialId), handlers.onEvent],
    [STOMP_DESTINATION.trialChat(trialId), handlers.onMessage],
    [STOMP_DESTINATION.errors, handlers.onError],
  ].filter(([, handler]) => typeof handler === 'function')
    .map(([destination, handler]) => client.subscribe(destination, handler))
  return () => subscriptions.forEach((unsubscribe) => unsubscribe())
}

export async function recoverLiveTrial({ trialId, afterEventSequence = 0, onSnapshot, onEvent }) {
  const snapshot = await getSnapshot(trialId)
  onSnapshot?.(snapshot)
  const events = (await getEvents(trialId, afterEventSequence)) || []
  events.forEach((event) => onEvent?.(event))
  return { snapshot, events }
}

export async function recoverLiveTrialChat({ trialId, afterMessageSequence = 0, pageSize = DEFAULT_MESSAGE_PAGE_SIZE }) {
  let cursor = afterMessageSequence
  let messages = []
  let hasMore = true
  while (hasMore) {
    const response = await getMessages(trialId, cursor, pageSize)
    const items = Array.isArray(response?.items) ? response.items : []
    messages = mergeMessages(messages, items)
    hasMore = response?.hasMore === true
    const lastSequence = items.reduce((latest, message) => Math.max(latest, Number(message.messageSequence) || 0), cursor)
    if (lastSequence <= cursor) break
    cursor = lastSequence
  }
  return { messages, latestMessageSequence: cursor }
}
