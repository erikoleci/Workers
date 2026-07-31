import { defineStore } from 'pinia'
import { userService } from '@/services/user.service'
import type { AppUser, UserCreatePayload } from '@/types/user.types'

function extractErrorMessage(err: unknown): string {
  const anyErr = err as any
  return anyErr?.response?.data?.message ?? anyErr?.message ?? 'Something went wrong. Please try again.'
}

export const useUserStore = defineStore('user', {
  state: () => ({
    items: [] as AppUser[],
    loading: false,
    error: null as string | null
  }),

  actions: {
    async fetchUsers(role?: string) {
      this.loading = true
      this.error = null
      try {
        const { data } = await userService.list(role)
        this.items = data
      } catch (err) {
        this.error = extractErrorMessage(err)
        throw err
      } finally {
        this.loading = false
      }
    },

    async createUser(payload: UserCreatePayload) {
      this.error = null
      try {
        await userService.create(payload)
        await this.fetchUsers()
      } catch (err) {
        this.error = extractErrorMessage(err)
        throw err
      }
    },

    async deactivateUser(id: string) {
      this.error = null
      try {
        await userService.deactivate(id)
        await this.fetchUsers()
      } catch (err) {
        this.error = extractErrorMessage(err)
        throw err
      }
    }
  }
})
