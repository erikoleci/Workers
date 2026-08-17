import { defineStore } from 'pinia'
import { payrollService } from '@/services/payroll.service'
import type { Payroll, PayrollGeneratePayload, PayrollAdjustPayload } from '@/types/payroll.types'

function extractErrorMessage(err: unknown): string {
  const anyErr = err as any
  return anyErr?.response?.data?.message ?? anyErr?.message ?? 'Something went wrong. Please try again.'
}

export const usePayrollStore = defineStore('payroll', {
  state: () => ({
    items: [] as Payroll[],
    total: 0,
    page: 0,
    size: 20,
    statusFilter: '',
    loading: false,
    error: null as string | null
  }),

  actions: {
    async fetchPayroll() {
      this.loading = true
      this.error = null
      try {
        const { data } = await payrollService.search({
          status: this.statusFilter,
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

    async generatePayroll(payload: PayrollGeneratePayload) {
      this.error = null
      try {
        await payrollService.generate(payload)
        await this.fetchPayroll()
      } catch (err) {
        this.error = extractErrorMessage(err)
        throw err
      }
    },

    async adjustPayroll(id: string, payload: PayrollAdjustPayload) {
      this.error = null
      try {
        await payrollService.adjust(id, payload)
        await this.fetchPayroll()
      } catch (err) {
        this.error = extractErrorMessage(err)
        throw err
      }
    },

    async markPaid(id: string) {
      this.error = null
      try {
        await payrollService.markPaid(id)
        await this.fetchPayroll()
      } catch (err) {
        this.error = extractErrorMessage(err)
        throw err
      }
    }
  }
})
