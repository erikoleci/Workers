export interface Expense {
  id: string
  projectId: string | null
  category: string
  amount: number
  expenseDate: string
  description: string | null
}

export interface ExpenseCreatePayload {
  projectId?: string
  category: string
  amount: number
  expenseDate: string
  description?: string
}
