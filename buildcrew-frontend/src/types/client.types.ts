export interface Client {
  id: string
  companyName: string
  contactPerson: string | null
  phone: string | null
  email: string | null
  address: string | null
}

export interface ClientCreatePayload {
  companyName: string
  contactPerson?: string
  phone?: string
  email?: string
  address?: string
}
