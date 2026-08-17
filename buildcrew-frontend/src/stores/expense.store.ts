import { defineStore } from 'pinia'
import { expenseService } from '@/services/expense.service'
import type { Expense, ExpenseCreatePayload } from '@/types/expense.types'

function extractErrorMessage(err: unknown): string {
  const anyErr = err as any
  return anyErr?.response?.data?.message ?? anyErr?.message ?? 'Something went wrong. Please try again.'
}

export const useExpenseStore = defineStore('expense', {
  state: () => ({
    items: [] as Expense[],
    total: 0,
    page: 0,
    size: 20,
    loading: false,
    error: null as string | null
  }),

  actions: {
    async fetchExpenses(projectId?: string) {
      this.loading = true
      this.error = null
      try {
        const { data } = await expenseService.search({ projectId, page: this.page, size: this.size })
        this.items = data.items
        this.total = data.total
      } catch (err) {
        this.error = extractErrorMessage(err)
        throw err
      } finally {
        this.loading = false
      }
    },

    async createExpense(payload: ExpenseCreatePayload) {
      this.error = null
      try {
        await expenseService.create(payload)
        await this.fetchExpenses()
      } catch (err) {
        this.error = extractErrorMessage(err)
        throw err
      }
    },

    async removeExpense(id: string) {
      this.error = null
      try {
        await expenseService.remove(id)
        await this.fetchExpenses()
      } catch (err) {
        this.error = extractErrorMessage(err)
        throw err
      }
    }
  }
})
