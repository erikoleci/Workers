import api from './api'
import type { LoginRequest, LoginResponse } from '@/types/auth.types'

export const authService = {
  login(payload: LoginRequest) {
    return api.post<LoginResponse>('/api/auth/login', payload)
  }
}
