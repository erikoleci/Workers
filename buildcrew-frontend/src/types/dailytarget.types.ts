export interface DailyTarget {
  id: string
  projectId: string
  targetDate: string
  targetM2: number
}

export interface DailyTargetCreatePayload {
  projectId: string
  targetDate: string
  targetM2: number
}
