import { API_ERROR_MESSAGES } from '@/consts/messages.js'

export const CLIENT_ERROR_CODE = Object.freeze({
  HTTP_ERROR: 'HTTP_ERROR',
  NETWORK_ERROR: 'NETWORK_ERROR',
  REQUEST_TIMEOUT: 'REQUEST_TIMEOUT',
  UNKNOWN_ERROR: 'UNKNOWN_ERROR',
})

export class ApiError extends Error {
  constructor({
    status = null,
    code = CLIENT_ERROR_CODE.UNKNOWN_ERROR,
    message = API_ERROR_MESSAGES[CLIENT_ERROR_CODE.UNKNOWN_ERROR],
    fieldErrors = [],
    timestamp = null,
    path = null,
    cause = null,
  } = {}) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.code = code
    this.fieldErrors = fieldErrors
    this.timestamp = timestamp
    this.path = path
    this.cause = cause
  }
}

export function normalizeApiError(error) {
  if (error instanceof ApiError) return error

  if (error?.code === 'ECONNABORTED' || error?.code === 'ETIMEDOUT') {
    return new ApiError({ code: CLIENT_ERROR_CODE.REQUEST_TIMEOUT, message: API_ERROR_MESSAGES[CLIENT_ERROR_CODE.REQUEST_TIMEOUT], cause: error })
  }

  if (error?.response) {
    const responseBody = error.response.data
    return new ApiError({
      status: error.response.status,
      code: responseBody?.code || CLIENT_ERROR_CODE.HTTP_ERROR,
      message: responseBody?.message || API_ERROR_MESSAGES[CLIENT_ERROR_CODE.HTTP_ERROR],
      fieldErrors: Array.isArray(responseBody?.fieldErrors) ? responseBody.fieldErrors : [],
      timestamp: responseBody?.timestamp || null,
      path: responseBody?.path || null,
      cause: error,
    })
  }

  if (error?.request) {
    return new ApiError({ code: CLIENT_ERROR_CODE.NETWORK_ERROR, message: API_ERROR_MESSAGES[CLIENT_ERROR_CODE.NETWORK_ERROR], cause: error })
  }

  return new ApiError({ message: error?.message || API_ERROR_MESSAGES[CLIENT_ERROR_CODE.UNKNOWN_ERROR], cause: error })
}
