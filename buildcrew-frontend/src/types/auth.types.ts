export interface LoginRequest {
  identifier: string
  password: string
}

export interface LoginResponse {
  token: string
  name: string
  email: string
  role: 'owner' | 'manager' | 'crew_leader'
  companyId: string
}
