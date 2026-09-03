import { STOMP_DESTINATION } from './stompDestinations.js'

export function subscribeLiveTrial(client, trialId, handlers = {}) {
  const subscriptions = [
    [STOMP_DESTINATION.trialEvents(trialId), handlers.onEvent],
    [STOMP_DESTINATION.trialChat(trialId), handlers.onMessage],
    [STOMP_DESTINATION.errors, handlers.onError],
  ]
    .filter(([, handler]) => typeof handler === 'function')
    .map(([destination, handler]) => client.subscribe(destination, handler))

  return () => subscriptions.forEach((unsubscribe) => unsubscribe())
}
