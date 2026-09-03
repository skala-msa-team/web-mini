import axios from 'axios'
import { API_BASE_URL, API_TIMEOUT_MS } from './apiConfig.js'
import { normalizeApiError } from './apiError.js'
import { getDemoUserId } from '@/composables/useDemoUser.js'

export const http = axios.create({
  baseURL: API_BASE_URL,
  timeout: API_TIMEOUT_MS,
  headers: {
    Accept: 'application/json',
    'Content-Type': 'application/json',
  },
})

http.interceptors.request.use((config) => {
  const demoUserId = getDemoUserId()

  if (typeof config.headers?.set === 'function') {
    config.headers.set('X-Demo-User-Id', demoUserId)
  } else {
    config.headers = { ...config.headers, 'X-Demo-User-Id': demoUserId }
  }

  return config
})

http.interceptors.response.use(
  (response) => response,
  (error) => Promise.reject(normalizeApiError(error)),
)
