export type NotificationType = 'missing_report' | 'deadline_close' | 'payroll_ready'

export interface AppNotification {
  id: string
  type: NotificationType
  message: string
  isRead: boolean
  createdAt: string
}
