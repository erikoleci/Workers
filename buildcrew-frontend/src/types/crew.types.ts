export interface CrewMember {
  workerId: string
  workerName: string | null
}

export interface Crew {
  id: string
  name: string
  leaderId: string | null
  leaderName: string | null
  currentProjectId: string | null
  status: 'active' | 'inactive'
  members: CrewMember[]
}

export interface CrewCreatePayload {
  name: string
  leaderId?: string
  currentProjectId?: string
  memberWorkerIds?: string[]
}
