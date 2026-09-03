import { http } from '@/lib/http.js'
import { unwrapApiResponse } from '@/utils/apiResponse.js'

export async function createPost(payload) {
  const response = await http.post('/posts', payload)
  return unwrapApiResponse(response)
}

export async function createTrial(postId, payload) {
  const response = await http.post(`/posts/${postId}/trials`, payload)
  return unwrapApiResponse(response)
}
