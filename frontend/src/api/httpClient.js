import axios from 'axios'
import { requestInterceptor } from './interceptors/requestInterceptor.js'
import { responseErrorInterceptor, responseInterceptor } from './interceptors/responseInterceptor.js'

const httpClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10_000,
  headers: {
    'Content-Type': 'application/json',
  },
})

httpClient.interceptors.request.use(requestInterceptor)
httpClient.interceptors.response.use(responseInterceptor, responseErrorInterceptor)

export default httpClient
