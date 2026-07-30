export interface DelayedProject {
  id: string
  name: string
  deadline: string | null
  progressPercent: number | null
}

export interface NotificationItem {
  id: string
  type: string
  message: string
  isRead: boolean
  createdAt: string
}

export interface DashboardSummary {
  activeProjects: number
  activeCrews: number
  activeWorkers: number
  todayProduction: number
  weeklyProduction: number
  monthlyProduction: number
  revenue: number
  payrollPending: number
  delayedProjects: DelayedProject[]
  notifications: NotificationItem[]
}
