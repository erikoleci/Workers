export type ProjectStatus = 'active' | 'delayed' | 'completed' | 'cancelled'

export interface Project {
  id: string
  clientId: string
  clientName: string | null
  name: string
  address: string | null
  startDate: string | null
  deadline: string | null
  contractValue: number | null
  totalM2: number | null
  assignedCrewId: string | null
  assignedCrewName: string | null
  status: ProjectStatus
  completedM2: number
  progressPercent: number
}

export interface ProjectCreatePayload {
  clientId: string
  name: string
  address?: string
  startDate?: string
  deadline?: string
  contractValue?: number
  totalM2?: number
  assignedCrewId?: string
}
