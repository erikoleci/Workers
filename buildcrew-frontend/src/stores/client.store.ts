import { defineStore } from 'pinia'
import { clientService } from '@/services/client.service'
import type { Client, ClientCreatePayload } from '@/types/client.types'

export const useClientStore = defineStore('client', {
  state: () => ({
    items: [] as Client[],
    total: 0,
    page: 0,
    size: 20,
    query: '',
    loading: false
  }),

  actions: {
    async fetchClients() {
      this.loading = true
      try {
        const { data } = await clientService.search({
          query: this.query,
          page: this.page,
          size: this.size
        })
        this.items = data.items
        this.total = data.total
      } finally {
        this.loading = false
      }
    },

    async createClient(payload: ClientCreatePayload) {
      await clientService.create(payload)
      await this.fetchClients()
    },

    async updateClient(id: string, payload: ClientCreatePayload) {
      await clientService.update(id, payload)
      await this.fetchClients()
    },

    async removeClient(id: string) {
      await clientService.remove(id)
      await this.fetchClients()
    }
  }
})
