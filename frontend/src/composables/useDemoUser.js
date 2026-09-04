const DEMO_USER_ID_STORAGE_KEY = 'demoUserId'

function createDemoUserId() {
  if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') {
    return crypto.randomUUID()
  }

  const timestamp = Date.now().toString(16).padStart(11, '0')
  const randomPart = (() => {
    if (typeof crypto !== 'undefined' && typeof crypto.getRandomValues === 'function') {
      const values = new Uint8Array(16)
      crypto.getRandomValues(values)
      return Array.from(values)
        .slice(0, 16)
        .map((value) => value.toString(16).padStart(2, '0'))
        .join('')
    }

    return Math.floor(Math.random() * 1e16).toString(16).padStart(16, '0')
  })()

  return `00000000-0000-4000-8000-${timestamp}${randomPart}`.slice(0, 36)
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
