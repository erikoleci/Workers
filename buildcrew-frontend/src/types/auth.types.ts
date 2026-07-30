export interface LoginRequest {
  email: string
  password: string
}

export interface LoginResponse {
  token: string
  name: string
  email: string
  role: 'owner' | 'manager' | 'crew_leader'
  companyId: string
}
