import { useAuthStore } from '@/stores/auth.store'

export function landingFor(authStore: ReturnType<typeof useAuthStore>): string {
  if (authStore.isWorker) return '/my-report'
  if (authStore.isOwner) return '/dashboard'
  return '/workers'
}
