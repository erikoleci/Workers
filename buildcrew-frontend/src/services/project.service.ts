import api from './api'
import type { Project, ProjectCreatePayload, ProjectStatus } from '@/types/project.types'
import type { PageResponse } from '@/types/worker.types'

export const projectService = {
  search(params: { query?: string; status?: string; page?: number; size?: number }) {
    return api.get<PageResponse<Project>>('/api/projects', { params })
  },
  getById(id: string) {
    return api.get<Project>(`/api/projects/${id}`)
  },
  create(payload: ProjectCreatePayload) {
    return api.post<Project>('/api/projects', payload)
  },
  update(id: string, payload: ProjectCreatePayload) {
    return api.put<Project>(`/api/projects/${id}`, payload)
  },
  updateStatus(id: string, status: ProjectStatus) {
    return api.patch<Project>(`/api/projects/${id}/status`, { status })
  },
  remove(id: string) {
    return api.delete(`/api/projects/${id}`)
  }
}
