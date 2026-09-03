import { Client } from '@stomp/stompjs'
import { CONNECTION_STATUS } from '@/constants/liveTrialUiStatus.js'
import { STOMP_DESTINATION } from './stompDestinations.js'

const HEARTBEAT_MS = 10_000
const RECONNECT_DELAYS_MS = Object.freeze([1_000, 2_000, 5_000])

function defaultBrokerUrl() {
  const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || window.location.origin
  const apiUrl = new URL(apiBaseUrl, window.location.origin)
  const brokerUrl = new URL(STOMP_DESTINATION.endpoint, apiUrl.origin)
  brokerUrl.protocol = brokerUrl.protocol === 'https:' ? 'wss:' : 'ws:'
  return brokerUrl.toString()
}

function readMessage(frame) {
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

    const delay = RECONNECT_DELAYS_MS[
      Math.min(reconnectAttempt, RECONNECT_DELAYS_MS.length - 1)
    ]
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
    heartbeatIncoming: HEARTBEAT_MS,
    heartbeatOutgoing: HEARTBEAT_MS,
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
    activate,
    deactivate,
    subscribe,
    unsubscribe,
    publish,
    get connected() {
      return client.connected
    },
    get active() {
      return client.active
    },
    get status() {
      return status
    },
    get lastError() {
      return lastError
    },
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

export { readMessage }
