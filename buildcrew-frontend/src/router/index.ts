import { createRouter, createWebHistory } from 'vue-router'
import { authGuard } from './guards'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/auth/LoginView.vue'),
      meta: { title: 'Sign in' }
    },
    {
      path: '/',
      component: () => import('@/layouts/AppLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        { path: '', redirect: '/dashboard' },
        {
          path: 'dashboard',
          name: 'dashboard',
          component: () => import('@/views/dashboard/DashboardView.vue'),
          meta: { title: 'Dashboard' }
        },
        {
          path: 'workers',
          name: 'workers',
          component: () => import('@/views/workers/WorkersView.vue'),
          meta: { title: 'Workers', roles: ['owner', 'manager'] }
        },
        {
          path: 'crews',
          name: 'crews',
          component: () => import('@/views/crews/CrewsView.vue'),
          meta: { title: 'Crews', roles: ['owner', 'manager'] }
        },
        {
          path: 'clients',
          name: 'clients',
          component: () => import('@/views/clients/ClientsView.vue'),
          meta: { title: 'Clients', roles: ['owner', 'manager'] }
        },
        {
          path: 'users',
          name: 'users',
          component: () => import('@/views/users/UsersView.vue'),
          meta: { title: 'Team Members', roles: ['owner'] }
        }
      ]
    },
    { path: '/:pathMatch(.*)*', redirect: '/dashboard' }
  ]
})

router.beforeEach(authGuard)

export default router
