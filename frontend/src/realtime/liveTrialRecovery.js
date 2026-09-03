import { trialApi } from '@/api/trialApi.js'

export async function recoverLiveTrial({
  trialId,
  afterEventSequence = 0,
  onSnapshot,
  onEvent,
}) {
  const snapshot = await trialApi.getSnapshot(trialId)
  onSnapshot?.(snapshot)

  const events = (await trialApi.getEvents(trialId, afterEventSequence)) || []

  events.forEach((event) => onEvent?.(event))

  return { snapshot, events }
}
