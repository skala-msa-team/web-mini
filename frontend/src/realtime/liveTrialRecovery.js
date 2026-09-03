import { trialApi } from '@/api/trialApi.js'

export async function recoverLiveTrial({ trialId, onSnapshot, onEvent, onMessage }) {
  const snapshot = await trialApi.getSnapshot(trialId)
  onSnapshot?.(snapshot)

  const [events, messageHistory] = await Promise.all([
    trialApi.getEvents(trialId, snapshot.latestEventSequence ?? 0),
    trialApi.getMessages(trialId, snapshot.latestMessageSequence ?? 0),
  ])

  const messages = messageHistory.items
  events.forEach((event) => onEvent?.(event))
  messages.forEach((message) => onMessage?.(message))

  return { snapshot, events, messages }
}
