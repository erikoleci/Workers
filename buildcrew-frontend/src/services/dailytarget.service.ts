import api from './api'
import type { DailyTarget, DailyTargetCreatePayload } from '@/types/dailytarget.types'

export const dailyTargetService = {
  findByProject(projectId: string) {
    return api.get<DailyTarget[]>(`/api/daily-targets/project/${projectId}`)
  },
  create(payload: DailyTargetCreatePayload) {
    return api.post<DailyTarget>('/api/daily-targets', payload)
  }
}
