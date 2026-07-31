export interface AppUser {
  id: string
  name: string
  email: string
  role: 'owner' | 'manager' | 'crew_leader'
  phone: string | null
  status: 'active' | 'inactive'
}

export interface UserCreatePayload {
  name: string
  email: string
  password: string
  role: 'manager' | 'crew_leader'
  phone?: string
}
