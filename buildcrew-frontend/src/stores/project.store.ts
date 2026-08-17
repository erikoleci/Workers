import { defineStore } from 'pinia'
import { projectService } from '@/services/project.service'
import type { Project, ProjectCreatePayload, ProjectStatus } from '@/types/project.types'

function extractErrorMessage(err: unknown): string {
  const anyErr = err as any
  return anyErr?.response?.data?.message ?? anyErr?.message ?? 'Something went wrong. Please try again.'
}

export const useProjectStore = defineStore('project', {
  state: () => ({
    items: [] as Project[],
    total: 0,
    page: 0,
    size: 20,
    query: '',
    statusFilter: '',
    loading: false,
    error: null as string | null
  }),

  actions: {
    async fetchProjects() {
      this.loading = true
      this.error = null
      try {
        const { data } = await projectService.search({
          query: this.query,
          status: this.statusFilter,
          page: this.page,
          size: this.size
        })
        this.items = data.items
        this.total = data.total
      } catch (err) {
        this.error = extractErrorMessage(err)
        throw err
      } finally {
        this.loading = false
      }
    },

    async createProject(payload: ProjectCreatePayload) {
      this.error = null
      try {
        await projectService.create(payload)
        await this.fetchProjects()
      } catch (err) {
        this.error = extractErrorMessage(err)
        throw err
      }
    },

    async updateProject(id: string, payload: ProjectCreatePayload) {
      this.error = null
      try {
        await projectService.update(id, payload)
        await this.fetchProjects()
      } catch (err) {
        this.error = extractErrorMessage(err)
        throw err
      }
    },

    async updateStatus(id: string, status: ProjectStatus) {
      this.error = null
      try {
        await projectService.updateStatus(id, status)
        await this.fetchProjects()
      } catch (err) {
        this.error = extractErrorMessage(err)
        throw err
      }
    },

    async removeProject(id: string) {
      this.error = null
      try {
        await projectService.remove(id)
        await this.fetchProjects()
      } catch (err) {
        this.error = extractErrorMessage(err)
        throw err
      }
    }
  }
})
