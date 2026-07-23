import API from './api'
import type { JwtResponse, LoginRequest, RegisterRequest, UserDto } from '../types'

export interface TokenRefreshRequest {
  refreshToken: string
}

export interface ForgotPasswordRequest {
  email: string
}

export interface ResetPasswordRequest {
  token: string
  newPassword: string
}

const authService = {
  /** Authenticate and get JWT tokens */
  login: (data: LoginRequest) =>
    API.post<JwtResponse>('/auth/login', data).then((r) => r.data),

  /** Register a new user */
  register: (data: RegisterRequest) =>
    API.post<UserDto>('/auth/register', data).then((r) => r.data),

  /** Refresh access token using refresh token */
  refreshToken: (data: TokenRefreshRequest) =>
    API.post<JwtResponse>('/auth/refresh-token', data).then((r) => r.data),

  /** Logout current user (invalidates refresh token server-side) */
  logout: () =>
    API.post('/auth/logout').then((r) => r.data),

  /** Request a password reset email */
  forgotPassword: (data: ForgotPasswordRequest) =>
    API.post('/auth/forgot-password', data).then((r) => r.data),

  /** Reset password using token received via email */
  resetPassword: (data: ResetPasswordRequest) =>
    API.post('/auth/reset-password', data).then((r) => r.data),

  /** Verify email address using token */
  verifyEmail: (token: string) =>
    API.get(`/auth/verify-email?token=${token}`).then((r) => r.data),

  /** Get current authenticated user profile */
  getMe: () =>
    API.get<UserDto>('/users/me').then((r) => r.data),
}

export default authService
