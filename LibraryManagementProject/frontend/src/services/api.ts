import axios from 'axios'
import { globalShowToast } from '../context/ToastContext'

const API = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
})

// Request Interceptor
API.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// ─── Error message helpers ─────────────────────────────────────────────────────
const HTTP_TITLES: Record<number, string> = {
  400: 'Bad Request',
  401: 'Unauthorised',
  403: 'Forbidden',
  404: 'Not Found',
  409: 'Conflict',
  422: 'Validation Error',
  429: 'Too Many Requests',
  500: 'Server Error',
  502: 'Bad Gateway',
  503: 'Service Unavailable',
}

function extractMessage(error: unknown): string {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data
    if (data?.message) return data.message
    if (typeof data === 'string') return data
  }
  return 'An unexpected error occurred.'
}

// Response Interceptor
API.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config
    const status: number | undefined = error.response?.status

    // ── 401: attempt token refresh first ─────────────────────────────────────
    if (status === 401 && !originalRequest._retry) {
      originalRequest._retry = true

      const refreshToken = localStorage.getItem('refreshToken')
      if (refreshToken) {
        try {
          const res = await axios.post('/api/auth/refresh-token', { refreshToken })
          const { token, refreshToken: newRefreshToken } = res.data.data

          localStorage.setItem('token', token)
          localStorage.setItem('refreshToken', newRefreshToken)

          originalRequest.headers.Authorization = `Bearer ${token}`
          return API(originalRequest)
        } catch {
          localStorage.clear()
          window.location.href = '/login'
          return Promise.reject(error)
        }
      } else {
        localStorage.clear()
        window.location.href = '/login'
        return Promise.reject(error)
      }
    }

    // ── All other errors: show toast ──────────────────────────────────────────
    if (status !== 401) {
      const title = HTTP_TITLES[status ?? 0] ?? 'Request Failed'
      const message = extractMessage(error)
      globalShowToast?.('error', title, message)
    }

    return Promise.reject(error)
  }
)

export default API

