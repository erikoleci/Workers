import { defineStore } from 'pinia'
import { crewService } from '@/services/crew.service'
import type { Crew, CrewCreatePayload } from '@/types/crew.types'

export const useCrewStore = defineStore('crew', {
  state: () => ({
    items: [] as Crew[],
    total: 0,
    page: 0,
    size: 20,
    query: '',
    loading: false
  }),

  actions: {
    async fetchCrews() {
      this.loading = true
      try {
        const { data } = await crewService.search({
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

    async createCrew(payload: CrewCreatePayload) {
      await crewService.create(payload)
      await this.fetchCrews()
    },

    async updateCrew(id: string, payload: CrewCreatePayload) {
      await crewService.update(id, payload)
      await this.fetchCrews()
    },

    async deactivateCrew(id: string) {
      await crewService.deactivate(id)
      await this.fetchCrews()
    }
  }
})
