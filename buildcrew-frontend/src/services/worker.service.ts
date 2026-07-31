import api from './api'
import type { Worker, WorkerCreatePayload, PageResponse } from '@/types/worker.types'

export const workerService = {
  search(params: { query?: string; status?: string; page?: number; size?: number }) {
    return api.get<PageResponse<Worker>>('/api/workers', { params })
  },
  getById(id: string) {
    return api.get<Worker>(`/api/workers/${id}`)
  },
  create(payload: WorkerCreatePayload) {
    return api.post<Worker>('/api/workers', payload)
  },
  update(id: string, payload: WorkerCreatePayload) {
    return api.put<Worker>(`/api/workers/${id}`, payload)
  },
  toggleStatus(id: string) {
    return api.patch(`/api/workers/${id}/status`)
  },
  delete(id: string) {
    return api.delete(`/api/workers/${id}`)
  }
}
