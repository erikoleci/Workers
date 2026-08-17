import api from './api'
import type { AppNotification } from '@/types/notification.types'

export const notificationService = {
  list(unreadOnly = false) {
    return api.get<AppNotification[]>('/api/notifications', { params: { unreadOnly } })
  },
  markRead(id: string) {
    return api.patch(`/api/notifications/${id}/read`)
  },
  markAllRead() {
    return api.patch('/api/notifications/read-all')
  }
}
