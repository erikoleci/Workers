import api from './api'
import type { DashboardSummary } from '@/types/dashboard.types'

export const dashboardService = {
  getSummary() {
    return api.get<DashboardSummary>('/api/dashboard/summary')
  }
}
