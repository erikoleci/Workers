import { defineStore } from 'pinia'
import { authService } from '@/services/auth.service'
import type { LoginRequest } from '@/types/auth.types'

interface AuthState {
  token: string | null
  name: string | null
  email: string | null
  role: string | null
  companyId: string | null
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: localStorage.getItem('token'),
    name: localStorage.getItem('name'),
    email: localStorage.getItem('email'),
    role: localStorage.getItem('role'),
    companyId: localStorage.getItem('companyId')
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
    isOwner: (state) => state.role === 'owner',
    isManager: (state) => state.role === 'manager',
    isCrewLeader: (state) => state.role === 'crew_leader'
  },

  actions: {
    async login(payload: LoginRequest) {
      const { data } = await authService.login(payload)

      this.token = data.token
      this.name = data.name
      this.email = data.email
      this.role = data.role
      this.companyId = data.companyId

      localStorage.setItem('token', data.token)
      localStorage.setItem('name', data.name)
      localStorage.setItem('email', data.email)
      localStorage.setItem('role', data.role)
      localStorage.setItem('companyId', data.companyId)
    },

    logout() {
      this.token = null
      this.name = null
      this.email = null
      this.role = null
      this.companyId = null
      localStorage.clear()
    }
  }
})
