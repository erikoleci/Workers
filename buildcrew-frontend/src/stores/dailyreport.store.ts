import { defineStore } from 'pinia'
import { dailyReportService } from '@/services/dailyreport.service'
import type { DailyReport, DailyReportCreatePayload } from '@/types/dailyreport.types'

function extractErrorMessage(err: unknown): string {
  const anyErr = err as any
  return anyErr?.response?.data?.message ?? anyErr?.message ?? 'Something went wrong. Please try again.'
}

export const useDailyReportStore = defineStore('dailyReport', {
  state: () => ({
    items: [] as DailyReport[],
    total: 0,
    page: 0,
    size: 20,
    loading: false,
    error: null as string | null
  }),

  actions: {
    async fetchReports(filters: { projectId?: string; crewId?: string; from?: string; to?: string } = {}) {
      this.loading = true
      this.error = null
      try {
        const { data } = await dailyReportService.search({
          ...filters,
          page: this.page,
          size: this.size
        })
        this.items = data.items
        this.total = data.total
      } catch (err) {
        this.error = extractErrorMessage(err)
        throw err
      } finally {
        this.loading = false
      }
    },

    async submitReport(payload: DailyReportCreatePayload) {
      this.error = null
      try {
        await dailyReportService.create(payload)
      } catch (err) {
        this.error = extractErrorMessage(err)
        throw err
      }
    }
  }
})
