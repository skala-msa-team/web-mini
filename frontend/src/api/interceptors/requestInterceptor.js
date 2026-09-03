const DEMO_USER_ID = 'demo-user-a'

export function requestInterceptor(config) {
  config.headers = config.headers || {}
  config.headers['X-Demo-User-Id'] = DEMO_USER_ID

  return config
}
