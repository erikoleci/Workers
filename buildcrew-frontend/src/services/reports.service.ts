import api from './api'
import type {
  ProductionByWorker,
  ProductionByCrew,
  ProductionByProject,
  MonthlyProduction,
  FinancialSummary
} from '@/types/reports.types'

export const reportsService = {
  byWorker(from: string, to: string) {
    return api.get<ProductionByWorker[]>('/api/reports/production/by-worker', { params: { from, to } })
  },
  byCrew(from: string, to: string) {
    return api.get<ProductionByCrew[]>('/api/reports/production/by-crew', { params: { from, to } })
  },
  byProject() {
    return api.get<ProductionByProject[]>('/api/reports/production/by-project')
  },
  monthly(months = 6) {
    return api.get<MonthlyProduction[]>('/api/reports/production/monthly', { params: { months } })
  },
  financialSummary(from: string, to: string) {
    return api.get<FinancialSummary>('/api/reports/financial-summary', { params: { from, to } })
  }
}
