import { httpClient } from '@/api/httpClient.js'
import { requestData } from '@/api/apiResponse.js'

export const postApi = Object.freeze({
  createPost(payload) {
    return requestData(httpClient.post('/posts', payload))
  },

  createTrial(postId, payload) {
    return requestData(httpClient.post(`/posts/${postId}/trials`, payload))
  },
})
