import { normalizeApiError } from '@/api/apiError.js'

export function extractResponseBody(response) {
  return response.data
}

export function rejectWithApiError(error) {
  return Promise.reject(normalizeApiError(error))
}
