export interface LoginRequest {
  identifier: string
  password: string
}

export interface LoginResponse {
  token: string
  name: string
  email: string | null
  role: 'owner' | 'manager' | 'crew_leader' | 'worker'
  companyId: string
}
