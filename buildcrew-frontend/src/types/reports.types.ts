export interface ProductionByWorker {
  workerId: string
  workerName: string
  payType: 'daily' | 'per_m2'
  totalM2: number
  daysWorked: number
  todayM2: number
  estimatedPayment: number
}

export interface ProductionByCrew {
  crewId: string
  crewName: string
  totalM2: number
  reportsCount: number
}

export interface ProductionByProject {
  projectId: string
  projectName: string
  totalM2: number
  targetM2: number
  progressPercent: number
}

export interface MonthlyProduction {
  month: string
  totalM2: number
}

export interface FinancialSummary {
  revenue: number
  expenses: number
  payroll: number
  profit: number
}
