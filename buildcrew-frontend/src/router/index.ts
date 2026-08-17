import { createRouter, createWebHistory } from 'vue-router'
import { authGuard } from './guards'
import { useAuthStore } from '@/stores/auth.store'
import { landingFor } from './landing'

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
      path: '/my-report',
      name: 'my-report',
      component: () => import('@/views/workerreport/WorkerReportView.vue'),
      meta: { title: 'Daily Report', requiresAuth: true, roles: ['worker'] }
    },
    {
      path: '/',
      component: () => import('@/layouts/AppLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        { path: '', redirect: () => landingFor(useAuthStore()) },
        {
          path: 'dashboard',
          name: 'dashboard',
          component: () => import('@/views/dashboard/DashboardView.vue'),
          meta: { title: 'Dashboard', roles: ['owner'] }
        },
        {
          path: 'workers',
          name: 'workers',
          component: () => import('@/views/workers/WorkersView.vue'),
          meta: { title: 'Workers', roles: ['owner', 'manager', 'crew_leader'] }
        },
        {
          path: 'crews',
          name: 'crews',
          component: () => import('@/views/crews/CrewsView.vue'),
          meta: { title: 'Crews', roles: ['owner', 'manager', 'crew_leader'] }
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
        },
        {
          path: 'projects',
          name: 'projects',
          component: () => import('@/views/projects/ProjectsView.vue'),
          meta: { title: 'Projects', roles: ['owner', 'manager'] }
        },
        {
          path: 'daily-report',
          name: 'daily-report-submit',
          component: () => import('@/views/dailyreports/DailyReportSubmitView.vue'),
          meta: { title: 'Daily Report', roles: ['owner', 'manager', 'crew_leader'] }
        },
        {
          path: 'daily-production',
          name: 'daily-production',
          component: () => import('@/views/dailyreports/DailyReportsListView.vue'),
          meta: { title: 'Daily Production', roles: ['owner', 'manager'] }
        },
        {
          path: 'payroll',
          name: 'payroll',
          component: () => import('@/views/payroll/PayrollView.vue'),
          meta: { title: 'Payroll', roles: ['owner'] }
        },
        {
          path: 'expenses',
          name: 'expenses',
          component: () => import('@/views/expenses/ExpensesView.vue'),
          meta: { title: 'Expenses', roles: ['owner', 'manager'] }
        },
        {
          path: 'daily-targets',
          name: 'daily-targets',
          component: () => import('@/views/dailytargets/DailyTargetsView.vue'),
          meta: { title: 'Daily Targets', roles: ['owner', 'manager'] }
        },
        {
          path: 'reports',
          name: 'reports',
          component: () => import('@/views/reports/ReportsView.vue'),
          meta: { title: 'Reports', roles: ['owner'] }
        }
      ]
    },
    { path: '/:pathMatch(.*)*', redirect: '/dashboard' }
  ]
})

router.beforeEach(authGuard)

export default router
