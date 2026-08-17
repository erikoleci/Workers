<template>
  <v-container fluid>
    <h1 class="text-h5 mb-4">Reports</h1>

    <v-alert v-if="error" type="error" density="compact" class="mb-4">{{ error }}</v-alert>

    <v-row class="mb-2">
      <v-col cols="6" sm="3">
        <v-text-field v-model="from" label="From" type="date" density="comfortable" variant="outlined" @change="loadAll" />
      </v-col>
      <v-col cols="6" sm="3">
        <v-text-field v-model="to" label="To" type="date" density="comfortable" variant="outlined" @change="loadAll" />
      </v-col>
    </v-row>

    <v-row class="mb-4">
      <v-col cols="12" sm="3">
        <v-card elevation="2" rounded="lg" class="pa-4">
          <div class="text-caption">Revenue</div>
          <div class="text-h6">€{{ summary?.revenue ?? 0 }}</div>
        </v-card>
      </v-col>
      <v-col cols="12" sm="3">
        <v-card elevation="2" rounded="lg" class="pa-4">
          <div class="text-caption">Expenses</div>
          <div class="text-h6">€{{ summary?.expenses ?? 0 }}</div>
        </v-card>
      </v-col>
      <v-col cols="12" sm="3">
        <v-card elevation="2" rounded="lg" class="pa-4">
          <div class="text-caption">Payroll</div>
          <div class="text-h6">€{{ summary?.payroll ?? 0 }}</div>
        </v-card>
      </v-col>
      <v-col cols="12" sm="3">
        <v-card elevation="2" rounded="lg" class="pa-4">
          <div class="text-caption">Profit</div>
          <div class="text-h6">€{{ summary?.profit ?? 0 }}</div>
        </v-card>
      </v-col>
    </v-row>

    <v-tabs v-model="tab" class="mb-4">
      <v-tab value="worker">By Worker</v-tab>
      <v-tab value="crew">By Crew</v-tab>
      <v-tab value="project">By Project</v-tab>
      <v-tab value="monthly">Monthly</v-tab>
    </v-tabs>

    <v-window v-model="tab">
      <v-window-item value="worker">
        <v-data-table :items="byWorker" :headers="workerHeaders" item-value="workerId">
          <template #item.estimatedPayment="{ item }">
            €{{ item.estimatedPayment }}
          </template>
        </v-data-table>
      </v-window-item>

      <v-window-item value="crew">
        <v-data-table :items="byCrew" :headers="crewHeaders" item-value="crewId" />
      </v-window-item>

      <v-window-item value="project">
        <v-data-table :items="byProject" :headers="projectHeaders" item-value="projectId">
          <template #item.progressPercent="{ item }">
            {{ item.progressPercent }}%
          </template>
        </v-data-table>
      </v-window-item>

      <v-window-item value="monthly">
        <v-data-table :items="monthly" :headers="monthlyHeaders" item-value="month" />
      </v-window-item>
    </v-window>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { reportsService } from '@/services/reports.service'
import type {
  ProductionByWorker,
  ProductionByCrew,
  ProductionByProject,
  MonthlyProduction,
  FinancialSummary
} from '@/types/reports.types'

const tab = ref('worker')
const today = new Date().toISOString().split('T')[0]
const firstOfMonth = new Date(new Date().getFullYear(), new Date().getMonth(), 1).toISOString().split('T')[0]

const from = ref(firstOfMonth)
const to = ref(today)
const error = ref('')

const byWorker = ref<ProductionByWorker[]>([])
const byCrew = ref<ProductionByCrew[]>([])
const byProject = ref<ProductionByProject[]>([])
const monthly = ref<MonthlyProduction[]>([])
const summary = ref<FinancialSummary | null>(null)

const workerHeaders = [
  { title: 'Worker', key: 'workerName' },
  { title: 'Today m²', key: 'todayM2' },
  { title: 'Period Total m²', key: 'totalM2' },
  { title: 'Days Worked', key: 'daysWorked' },
  { title: 'Estimated Payment', key: 'estimatedPayment' }
]
const crewHeaders = [
  { title: 'Crew', key: 'crewName' },
  { title: 'Total m²', key: 'totalM2' },
  { title: 'Reports', key: 'reportsCount' }
]
const projectHeaders = [
  { title: 'Project', key: 'projectName' },
  { title: 'Completed m²', key: 'totalM2' },
  { title: 'Target m²', key: 'targetM2' },
  { title: 'Progress', key: 'progressPercent' }
]
const monthlyHeaders = [
  { title: 'Month', key: 'month' },
  { title: 'Total m²', key: 'totalM2' }
]

async function loadAll() {
  error.value = ''
  try {
    const [workerRes, crewRes, projectRes, monthlyRes, summaryRes] = await Promise.all([
      reportsService.byWorker(from.value, to.value),
      reportsService.byCrew(from.value, to.value),
      reportsService.byProject(),
      reportsService.monthly(6),
      reportsService.financialSummary(from.value, to.value)
    ])
    byWorker.value = workerRes.data
    byCrew.value = crewRes.data
    byProject.value = projectRes.data
    monthly.value = monthlyRes.data
    summary.value = summaryRes.data
  } catch (e: any) {
    error.value = e.response?.data?.message ?? 'Failed to load reports'
  }
}

onMounted(loadAll)
</script>
