import api from './api'
import type { DailyReport, DailyReportCreatePayload } from '@/types/dailyreport.types'
import type { PageResponse } from '@/types/worker.types'

export const dailyReportService = {
  search(params: { projectId?: string; crewId?: string; from?: string; to?: string; page?: number; size?: number }) {
    return api.get<PageResponse<DailyReport>>('/api/daily-reports', { params })
  },
  create(payload: DailyReportCreatePayload) {
    return api.post<DailyReport>('/api/daily-reports', payload)
  },
  update(id: string, payload: DailyReportCreatePayload) {
    return api.put<DailyReport>(`/api/daily-reports/${id}`, payload)
  }
}
