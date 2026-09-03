// Keep destinations in one place so they can be changed with the approved contract.
export const STOMP_DESTINATION = Object.freeze({
  endpoint: '/ws',
  trialEvents: (trialId) => `/topic/trials/${trialId}/events`,
  trialChat: (trialId) => `/topic/trials/${trialId}/chat`,
  errors: '/user/queue/errors',
  sendChat: (trialId) => `/app/trials/${trialId}/chat`,
})
