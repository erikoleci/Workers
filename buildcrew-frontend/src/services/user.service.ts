import api from './api'
import type { AppUser, UserCreatePayload } from '@/types/user.types'

export const userService = {
  list(role?: string) {
    return api.get<AppUser[]>('/api/users', { params: role ? { role } : {} })
  },
  create(payload: UserCreatePayload) {
    return api.post<AppUser>('/api/users', payload)
  },
  deactivate(id: string) {
    return api.delete(`/api/users/${id}`)
  },
  resetPassword(id: string, newPassword: string) {
    return api.patch(`/api/users/${id}/reset-password`, { newPassword })
  }
}
