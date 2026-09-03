const FALLBACK_DEMO_USER_ID = '00000000-0000-4000-8000-000000000001'

function createDemoUserId() {
  if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') {
    return crypto.randomUUID()
  }

  return FALLBACK_DEMO_USER_ID
}

export function useDemoUser() {
  function getDemoUserId() {
    const savedUserId = window.localStorage.getItem('demoUserId')
    if (savedUserId) return savedUserId

    const userId = createDemoUserId()
    window.localStorage.setItem('demoUserId', userId)
    return userId
  }

  return {
    getDemoUserId,
  }
}
