export function responseInterceptor(response) {
  return response
}

export function responseErrorInterceptor(error) {
  const responseData = error.response?.data
  const responseMessage = typeof responseData === 'string' ? responseData : responseData?.message

  error.userMessage = responseMessage || error.message || '요청을 처리하지 못했습니다.'

  return Promise.reject(error)
}
