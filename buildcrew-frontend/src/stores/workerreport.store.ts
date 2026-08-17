import { defineStore } from 'pinia'
import {
  workerReportService,
  type WorkerProjectOption,
  type WorkerReport,
  type WorkerReportSubmitPayload
} from '@/services/workerreport.service'

function extractErrorMessage(err: unknown): string {
  const anyErr = err as any
  return anyErr?.response?.data?.message ?? anyErr?.message ?? 'Something went wrong. Please try again.'
}

export const useWorkerReportStore = defineStore('workerReport', {
  state: () => ({
    projects: [] as WorkerProjectOption[],
    myReports: [] as WorkerReport[],
    loading: false,
    submitting: false,
    error: null as string | null,
    success: false
  }),

  actions: {
    async fetchProjects() {
      this.loading = true
      this.error = null
      try {
        const { data } = await workerReportService.myProjects()
        this.projects = data
      } catch (err) {
        this.error = extractErrorMessage(err)
      } finally {
        this.loading = false
      }
    },

    async fetchMyReports() {
      try {
        const { data } = await workerReportService.myReports()
        this.myReports = data
      } catch (err) {
        this.error = extractErrorMessage(err)
      }
    },

    async submit(payload: WorkerReportSubmitPayload) {
      this.submitting = true
      this.error = null
      this.success = false
      try {
        await workerReportService.submit(payload)
        this.success = true
        await this.fetchMyReports()
      } catch (err) {
        this.error = extractErrorMessage(err)
        throw err
      } finally {
        this.submitting = false
      }
    }
  }
})
