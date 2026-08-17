export interface Payroll {
  id: string
  workerId: string
  workerName: string | null
  periodStart: string
  periodEnd: string
  baseAmount: number
  bonuses: number
  deductions: number
  finalAmount: number
  status: 'pending' | 'paid'
}

export interface PayrollGeneratePayload {
  periodStart: string
  periodEnd: string
  workerIds?: string[]
}

export interface PayrollAdjustPayload {
  bonuses?: number
  deductions?: number
}
