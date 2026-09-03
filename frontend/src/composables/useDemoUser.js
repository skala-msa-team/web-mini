const DEFAULT_DEMO_USER_ID = 'demo-user-a'

export function useDemoUser() {
  function getDemoUserId() {
    return window.localStorage.getItem('demoUserId') || DEFAULT_DEMO_USER_ID
  }

  return {
    getDemoUserId,
  }
}
