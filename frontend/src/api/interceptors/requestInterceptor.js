import { getDemoUserId } from '@/composables/useDemoUser.js'

const DEMO_USER_HEADER = 'X-Demo-User-Id'

export function attachDemoUserId(config) {
  const demoUserId = getDemoUserId()

  if (typeof config.headers?.set === 'function') {
    config.headers.set(DEMO_USER_HEADER, demoUserId)
  } else {
    config.headers = {
      ...config.headers,
      [DEMO_USER_HEADER]: demoUserId,
    }
  }

  return config
}
