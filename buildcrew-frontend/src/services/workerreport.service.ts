import api from './api'

export interface WorkerProjectOption {
  projectId: string
  projectName: string
}

export interface WorkerReport {
  id: string
  projectId: string
  projectName: string
  reportDate: string
  completedM2: number
  comments?: string
}

export interface WorkerReportSubmitPayload {
  projectId: string
  reportDate: string
  completedM2: number
  comments?: string
}

export const workerReportService = {
  myProjects() {
    return api.get<WorkerProjectOption[]>('/api/worker-reports/my-projects')
  },
  submit(payload: WorkerReportSubmitPayload) {
    return api.post<WorkerReport>('/api/worker-reports', payload)
  },
  myReports() {
    return api.get<WorkerReport[]>('/api/worker-reports/mine')
  }
}
