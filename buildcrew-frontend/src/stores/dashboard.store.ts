import { defineStore } from 'pinia'
import { dashboardService } from '@/services/dashboard.service'
import type { DashboardSummary } from '@/types/dashboard.types'

export const useDashboardStore = defineStore('dashboard', {
  state: () => ({
    summary: null as DashboardSummary | null,
    loading: false
  }),

  actions: {
    async fetchSummary() {
      this.loading = true
      try {
        const { data } = await dashboardService.getSummary()
        this.summary = data
      } finally {
        this.loading = false
      }
    }
  }
})
