export type PayType = 'daily' | 'per_m2'
export type WorkerStatus = 'active' | 'inactive'

export interface Worker {
  id: string
  fullName: string
  phone: string | null
  position: string | null
  payType: PayType
  dailySalary: number | null
  pricePerM2: number | null
  employmentDate: string | null
  status: WorkerStatus
}

export interface WorkerCreatePayload {
  fullName: string
  phone?: string
  position?: string
  payType: PayType
  dailySalary?: number
  pricePerM2?: number
  employmentDate?: string
}

export interface PageResponse<T> {
  items: T[]
  page: number
  size: number
  total: number
  totalPages: number
}
