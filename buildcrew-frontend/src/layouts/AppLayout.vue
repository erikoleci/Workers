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
        <v-list-item to="/dashboard" prepend-icon="mdi-view-dashboard" title="Dashboard" />
        <v-list-item to="/workers" prepend-icon="mdi-account-hard-hat" title="Workers" />
        <v-list-item to="/crews" prepend-icon="mdi-account-group" title="Crews" />
        <v-list-item to="/clients" prepend-icon="mdi-briefcase-account" title="Clients" />
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
      <v-btn icon="mdi-theme-light-dark" @click="toggleTheme" />
    </v-app-bar>

    <v-main>
      <router-view />
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTheme } from 'vuetify'
import { useAuthStore } from '@/stores/auth.store'

const drawer = ref(true)
const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const theme = useTheme()

const pageTitle = computed(() => (route.meta.title as string) ?? 'BuildCrew Manager')

function toggleTheme() {
  theme.global.name.value = theme.global.name.value === 'light' ? 'dark' : 'light'
}

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>
