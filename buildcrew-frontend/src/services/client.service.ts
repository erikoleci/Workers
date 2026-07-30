import api from './api'
import type { Client, ClientCreatePayload } from '@/types/client.types'
import type { PageResponse } from '@/types/worker.types'

export const clientService = {
  search(params: { query?: string; page?: number; size?: number }) {
    return api.get<PageResponse<Client>>('/api/clients', { params })
  },
  getById(id: string) {
    return api.get<Client>(`/api/clients/${id}`)
  },
  create(payload: ClientCreatePayload) {
    return api.post<Client>('/api/clients', payload)
  },
  update(id: string, payload: ClientCreatePayload) {
    return api.put<Client>(`/api/clients/${id}`, payload)
  },
  remove(id: string) {
    return api.delete(`/api/clients/${id}`)
  }
}
