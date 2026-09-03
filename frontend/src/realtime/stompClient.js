import { Client } from '@stomp/stompjs'
import { CONNECTION_STATUS } from '@/constants/liveTrialUiStatus.js'
import { STOMP_DESTINATION } from './stompDestinations.js'

const HEARTBEAT_MS = 10_000
const RECONNECT_DELAY_MS = 5_000

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

  const client = new Client({
    brokerURL,
    connectHeaders: { 'X-Demo-User-Id': userId },
    heartbeatIncoming: HEARTBEAT_MS,
    heartbeatOutgoing: HEARTBEAT_MS,
    reconnectDelay: RECONNECT_DELAY_MS,
    onConnect: () => {
      const reconnected = hasConnected
      hasConnected = true
      lastError = null
      subscriptions.forEach((entry) => {
        entry.subscription = client.subscribe(entry.destination, entry.handler)
      })
      updateStatus(CONNECTION_STATUS.CONNECTED)
      connectedListeners.forEach((listener) => listener({ reconnected }))
    },
    onWebSocketClose: () => {
      if (client.active) updateStatus(CONNECTION_STATUS.RECONNECTING)
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
    updateStatus(hasConnected ? CONNECTION_STATUS.RECONNECTING : CONNECTION_STATUS.CONNECTING)
    client.activate()
  }

  async function deactivate() {
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
