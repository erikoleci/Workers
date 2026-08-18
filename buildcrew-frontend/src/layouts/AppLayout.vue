<template>
  <v-app>
    <v-navigation-drawer v-model="drawer" app>
      <v-list-item
        title="BuildCrew Manager"
        :subtitle="authStore.name ?? ''"
        prepend-icon="mdi-domain"
      />
      <v-divider />
      <v-list nav density="comfortable">
        <v-list-item v-if="authStore.isOwner" to="/dashboard" prepend-icon="mdi-view-dashboard" title="Dashboard" />
        <v-list-item to="/workers" prepend-icon="mdi-account-hard-hat" title="Workers" />
        <v-list-item to="/crews" prepend-icon="mdi-account-group" title="Crews" />
        <v-list-item v-if="authStore.isOwner || authStore.isManager" to="/clients" prepend-icon="mdi-briefcase-account" title="Clients" />
        <v-list-item v-if="authStore.isOwner" to="/users" prepend-icon="mdi-account-plus" title="Team Members" />
        <v-list-item v-if="authStore.isOwner || authStore.isManager" to="/projects" prepend-icon="mdi-office-building" title="Projects" />
        <v-list-item to="/daily-report" prepend-icon="mdi-clipboard-text" title="Daily Report" />
        <v-list-item v-if="authStore.isOwner || authStore.isManager" to="/daily-production" prepend-icon="mdi-chart-timeline-variant" title="Daily Production" />
        <v-list-item v-if="authStore.isOwner || authStore.isManager" to="/daily-targets" prepend-icon="mdi-target" title="Daily Targets" />
        <v-list-item v-if="authStore.isOwner || authStore.isManager" to="/expenses" prepend-icon="mdi-receipt" title="Expenses" />
        <v-list-item v-if="authStore.isOwner" to="/payroll" prepend-icon="mdi-cash-multiple" title="Payroll" />
        <v-list-item v-if="authStore.isOwner" to="/reports" prepend-icon="mdi-chart-bar" title="Reports" />
      </v-list>
      <template #append>
        <div class="pa-2">
          <v-btn block variant="tonal" color="error" prepend-icon="mdi-logout" @click="handleLogout">
            Log out
          </v-btn>
        </div>
      </template>
    </v-navigation-drawer>

    <v-app-bar app flat border>
      <v-app-bar-nav-icon @click="drawer = !drawer" />
      <v-toolbar-title>{{ pageTitle }}</v-toolbar-title>
      <v-spacer />

      <v-menu location="bottom end" @update:model-value="onMenuToggle">
        <template #activator="{ props }">
          <v-btn v-bind="props" icon>
            <v-badge :content="notificationStore.unreadCount" :model-value="notificationStore.unreadCount > 0" color="error">
              <v-icon>mdi-bell</v-icon>
            </v-badge>
          </v-btn>
        </template>
        <v-card min-width="320" max-width="400">
          <v-card-title class="d-flex justify-space-between align-center text-subtitle-1">
            Notifications
            <v-btn v-if="notificationStore.unreadCount > 0" size="small" variant="text" @click="notificationStore.markAllRead()">
              Mark all read
            </v-btn>
          </v-card-title>
          <v-divider />
          <v-list max-height="400" style="overflow-y: auto">
            <v-list-item v-if="!notificationStore.items.length">
              <span class="text-medium-emphasis">No notifications</span>
            </v-list-item>
            <v-list-item
              v-for="n in notificationStore.items"
              :key="n.id"
              :class="{ 'bg-blue-lighten-5': !n.isRead }"
              @click="!n.isRead && notificationStore.markRead(n.id)"
            >
              <v-list-item-title class="text-wrap">{{ n.message }}</v-list-item-title>
              <v-list-item-subtitle>{{ formatDate(n.createdAt) }}</v-list-item-subtitle>
            </v-list-item>
          </v-list>
        </v-card>
      </v-menu>

      <v-btn icon="mdi-theme-light-dark" @click="toggleTheme" />
    </v-app-bar>

    <v-main>
      <router-view />
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTheme } from 'vuetify'
import { useAuthStore } from '@/stores/auth.store'
import { useNotificationStore } from '@/stores/notification.store'

const drawer = ref(true)
const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const notificationStore = useNotificationStore()
const theme = useTheme()

const pageTitle = computed(() => (route.meta.title as string) ?? 'BuildCrew Manager')

onMounted(() => {
  notificationStore.fetchNotifications()
})

function onMenuToggle(open: boolean) {
  if (open) notificationStore.fetchNotifications()
}

function formatDate(iso: string) {
  return new Date(iso).toLocaleString()
}

function toggleTheme() {
  theme.global.name.value = theme.global.name.value === 'light' ? 'dark' : 'light'
}

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>
