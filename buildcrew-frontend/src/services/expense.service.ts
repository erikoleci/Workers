import api from './api'
import type { Expense, ExpenseCreatePayload } from '@/types/expense.types'
import type { PageResponse } from '@/types/worker.types'

export const expenseService = {
  search(params: { projectId?: string; page?: number; size?: number }) {
    return api.get<PageResponse<Expense>>('/api/expenses', { params })
  },
  create(payload: ExpenseCreatePayload) {
    return api.post<Expense>('/api/expenses', payload)
  },
  remove(id: string) {
    return api.delete(`/api/expenses/${id}`)
  }
}
