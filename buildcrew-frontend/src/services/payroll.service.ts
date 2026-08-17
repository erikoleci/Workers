import api from './api'
import type { Payroll, PayrollGeneratePayload, PayrollAdjustPayload } from '@/types/payroll.types'
import type { PageResponse } from '@/types/worker.types'

export const payrollService = {
  search(params: { status?: string; workerId?: string; page?: number; size?: number }) {
    return api.get<PageResponse<Payroll>>('/api/payroll', { params })
  },
  generate(payload: PayrollGeneratePayload) {
    return api.post<Payroll[]>('/api/payroll/generate', payload)
  },
  adjust(id: string, payload: PayrollAdjustPayload) {
    return api.patch<Payroll>(`/api/payroll/${id}/adjust`, payload)
  },
  markPaid(id: string) {
    return api.patch<Payroll>(`/api/payroll/${id}/mark-paid`)
  }
}
