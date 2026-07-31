import { defineStore } from 'pinia'
import { workerService } from '@/services/worker.service'
import type { Worker, WorkerCreatePayload } from '@/types/worker.types'

export const useWorkerStore = defineStore('worker', {
  state: () => ({
    items: [] as Worker[],
    total: 0,
    page: 0,
    size: 20,
    query: '',
    loading: false
  }),

  actions: {
    async fetchWorkers() {
      this.loading = true
      try {
        const { data } = await workerService.search({
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

    async createWorker(payload: WorkerCreatePayload) {
      await workerService.create(payload)
      await this.fetchWorkers()
    },

    async updateWorker(id: string, payload: WorkerCreatePayload) {
      await workerService.update(id, payload)
      await this.fetchWorkers()
    },

    async toggleWorkerStatus(id: string) {
      await workerService.toggleStatus(id)
      await this.fetchWorkers()
    },

    async deleteWorker(id: string) {
      await workerService.delete(id)
      await this.fetchWorkers()
    }
  }
})
