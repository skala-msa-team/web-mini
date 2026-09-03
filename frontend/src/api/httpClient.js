import axios from 'axios'
import { API_BASE_URL, API_TIMEOUT_MS } from '@/api/apiConfig.js'
import { attachDemoUserId } from '@/api/interceptors/requestInterceptor.js'
import {
  extractResponseBody,
  rejectWithApiError,
} from '@/api/interceptors/responseInterceptor.js'

export const httpClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: API_TIMEOUT_MS,
  headers: {
    Accept: 'application/json',
    'Content-Type': 'application/json',
  },
})

httpClient.interceptors.request.use(attachDemoUserId)
httpClient.interceptors.response.use(extractResponseBody, rejectWithApiError)
