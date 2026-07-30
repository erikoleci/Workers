<template>
  <v-container fluid>
    <h1 class="text-h5 mb-4">Dashboard</h1>

    <v-row v-if="summary">
      <v-col cols="12" sm="6" md="3">
        <ProductionCard label="Active Projects" :value="summary.activeProjects" icon="mdi-office-building" color="primary" />
      </v-col>
      <v-col cols="12" sm="6" md="3">
        <ProductionCard label="Active Crews" :value="summary.activeCrews" icon="mdi-account-group" color="primary" />
      </v-col>
      <v-col cols="12" sm="6" md="3">
        <ProductionCard label="Active Workers" :value="summary.activeWorkers" icon="mdi-account-hard-hat" color="primary" />
      </v-col>
      <v-col cols="12" sm="6" md="3">
        <ProductionCard label="Payroll Pending" :value="formatCurrency(summary.payrollPending)" icon="mdi-cash" color="warning" />
      </v-col>

      <v-col cols="12" sm="6" md="4">
        <ProductionCard label="Today's Production (m²)" :value="summary.todayProduction" icon="mdi-calendar-today" color="success" />
      </v-col>
      <v-col cols="12" sm="6" md="4">
        <ProductionCard label="Weekly Production (m²)" :value="summary.weeklyProduction" icon="mdi-calendar-week" color="success" />
      </v-col>
      <v-col cols="12" sm="6" md="4">
        <ProductionCard label="Monthly Production (m²)" :value="summary.monthlyProduction" icon="mdi-calendar-month" color="success" />
      </v-col>

      <v-col cols="12" md="6">
        <v-card elevation="2" rounded="lg">
          <v-card-title>Delayed Projects</v-card-title>
          <v-list v-if="summary.delayedProjects.length" density="comfortable">
            <v-list-item
              v-for="p in summary.delayedProjects"
              :key="p.id"
              :title="p.name"
              :subtitle="`Deadline: ${p.deadline ?? 'N/A'}`"
            >
              <template #prepend>
                <v-icon color="error" icon="mdi-alert-circle" />
              </template>
            </v-list-item>
          </v-list>
          <v-card-text v-else class="text-medium-emphasis">No delayed projects.</v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="6">
        <v-card elevation="2" rounded="lg">
          <v-card-title>Notifications</v-card-title>
          <v-list v-if="summary.notifications.length" density="comfortable">
            <v-list-item
              v-for="n in summary.notifications"
              :key="n.id"
              :title="n.message"
              :subtitle="n.type"
            >
              <template #prepend>
                <v-icon :icon="n.isRead ? 'mdi-bell-outline' : 'mdi-bell-ring'" :color="n.isRead ? 'grey' : 'primary'" />
              </template>
            </v-list-item>
          </v-list>
          <v-card-text v-else class="text-medium-emphasis">No notifications.</v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-row v-else-if="loading">
      <v-col cols="12" class="text-center pa-8">
        <v-progress-circular indeterminate color="primary" />
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useDashboardStore } from '@/stores/dashboard.store'
import { storeToRefs } from 'pinia'
import ProductionCard from '@/components/dashboard/ProductionCard.vue'

const dashboardStore = useDashboardStore()
const { summary, loading } = storeToRefs(dashboardStore)

onMounted(() => {
  dashboardStore.fetchSummary()
})

function formatCurrency(value: number) {
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'EUR' }).format(value)
}
</script>
