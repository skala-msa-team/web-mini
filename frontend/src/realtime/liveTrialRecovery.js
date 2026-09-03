const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || window.location.origin

async function getJson(path) {
  const response = await fetch(`${API_BASE_URL}${path}`)
  if (!response.ok) throw new Error(`재판 복구 요청에 실패했습니다. (${response.status})`)
  return response.json()
}

export async function recoverLiveTrial({ trialId, onSnapshot, onEvent, onMessage }) {
  const snapshotResponse = await getJson(`/api/v1/trials/${trialId}/snapshot`)
  const snapshot = snapshotResponse.data ?? snapshotResponse
  onSnapshot?.(snapshot)

  const [eventsResponse, messagesResponse] = await Promise.all([
    getJson(`/api/v1/trials/${trialId}/events?afterSequence=${snapshot.latestEventSequence ?? 0}`),
    getJson(`/api/v1/trials/${trialId}/messages?afterSequence=${snapshot.latestMessageSequence ?? 0}`),
  ])

  const events = eventsResponse.data ?? eventsResponse
  const messages = messagesResponse.data?.items ?? messagesResponse.items ?? messagesResponse
  events.forEach((event) => onEvent?.(event))
  messages.forEach((message) => onMessage?.(message))

  return { snapshot, events, messages }
}
