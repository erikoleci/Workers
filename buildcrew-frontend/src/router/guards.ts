import type { NavigationGuardWithThis } from 'vue-router'
import { useAuthStore } from '@/stores/auth.store'
import { landingFor } from './landing'

export const authGuard: NavigationGuardWithThis<undefined> = (to) => {
  const authStore = useAuthStore()

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return '/login'
  }

  if (to.path === '/login' && authStore.isAuthenticated) {
    return landingFor(authStore)
  }

  if (to.meta.roles && !(to.meta.roles as string[]).includes(authStore.role ?? '')) {
    return landingFor(authStore)
  }
}
