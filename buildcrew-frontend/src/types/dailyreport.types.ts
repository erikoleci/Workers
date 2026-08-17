export interface DailyReport {
  id: string
  projectId: string
  projectName: string | null
  crewId: string
  crewName: string | null
  reportDate: string
  completedM2: number
  workedHours: number | null
  comments: string | null
  createdByName: string | null
  targetM2: number | null
  remainingM2: number | null
  completionPercent: number | null
  status: 'green' | 'yellow' | 'red'
}

export interface DailyReportCreatePayload {
  projectId: string
  crewId: string
  reportDate: string
  completedM2: number
  workedHours?: number
  comments?: string
}
