const FALLBACK_DEMO_USER_ID = '00000000-0000-4000-8000-000000000001'
const DEMO_USER_ID_STORAGE_KEY = 'demoUserId'

function createDemoUserId() {
  if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') {
    return crypto.randomUUID()
  }

  return FALLBACK_DEMO_USER_ID
}

function isValidDemoUserId(value) {
  if (!value) return false

  return /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i.test(value)
}

export function getDemoUserId() {
  const savedUserId = window.localStorage.getItem(DEMO_USER_ID_STORAGE_KEY)
  if (isValidDemoUserId(savedUserId)) return savedUserId

  const userId = createDemoUserId()
  window.localStorage.setItem(DEMO_USER_ID_STORAGE_KEY, userId)
  return userId
}

export function useDemoUser() {
  return {
    getDemoUserId,
  }
}
