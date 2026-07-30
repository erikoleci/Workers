import api from './api'
import type { Crew, CrewCreatePayload } from '@/types/crew.types'
import type { PageResponse } from '@/types/worker.types'

export const crewService = {
  search(params: { query?: string; page?: number; size?: number }) {
    return api.get<PageResponse<Crew>>('/api/crews', { params })
  },
  getById(id: string) {
    return api.get<Crew>(`/api/crews/${id}`)
  },
  create(payload: CrewCreatePayload) {
    return api.post<Crew>('/api/crews', payload)
  },
  update(id: string, payload: CrewCreatePayload) {
    return api.put<Crew>(`/api/crews/${id}`, payload)
  },
  deactivate(id: string) {
    return api.delete(`/api/crews/${id}`)
  }
}
