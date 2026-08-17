import { defineStore } from 'pinia'
import { notificationService } from '@/services/notification.service'
import type { AppNotification } from '@/types/notification.types'

export const useNotificationStore = defineStore('notification', {
  state: () => ({
    items: [] as AppNotification[],
    loading: false
  }),

  getters: {
    unreadCount: (state) => state.items.filter((n) => !n.isRead).length
  },

  actions: {
    async fetchNotifications() {
      this.loading = true
      try {
        const { data } = await notificationService.list()
        this.items = data
      } finally {
        this.loading = false
      }
    },

    async markRead(id: string) {
      await notificationService.markRead(id)
      const n = this.items.find((i) => i.id === id)
      if (n) n.isRead = true
    },

    async markAllRead() {
      await notificationService.markAllRead()
      this.items.forEach((n) => (n.isRead = true))
    }
  }
})
