<template>
  <v-container fluid>
    <h1 class="text-h5 mb-4">Daily Production</h1>

    <v-alert type="info" density="compact" variant="tonal" class="mb-4">
      Kjo listë tregon raportet e dërguara nga crew leader-i për ekipin.
      Për totalet e kombinuara (përfshi raportet individuale të punëtorëve), shiko faqen Reports.
    </v-alert>

    <v-alert v-if="dailyReportStore.error" type="error" density="compact" class="mb-4">
      {{ dailyReportStore.error }}
    </v-alert>

    <v-data-table
      :items="dailyReportStore.items"
      :loading="dailyReportStore.loading"
      :headers="headers"
      item-value="id"
    >
      <template #item.status="{ item }">
        <v-chip :color="statusColor(item.status)" size="small">
          {{ item.completionPercent ?? '-' }}%
        </v-chip>
      </template>

      <template #item.completedM2="{ item }">
        {{ item.completedM2 }} / {{ item.targetM2 ?? '—' }} m²
      </template>
    </v-data-table>
  </v-container>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useDailyReportStore } from '@/stores/dailyreport.store'

const dailyReportStore = useDailyReportStore()

const headers = [
  { title: 'Date', key: 'reportDate' },
  { title: 'Project', key: 'projectName' },
  { title: 'Crew', key: 'crewName' },
  { title: 'Production', key: 'completedM2' },
  { title: 'Hours', key: 'workedHours' },
  { title: 'Target Status', key: 'status' }
]

onMounted(() => {
  dailyReportStore.fetchReports()
})

function statusColor(status: string) {
  switch (status) {
    case 'green': return 'success'
    case 'yellow': return 'warning'
    case 'red': return 'error'
    default: return 'grey'
  }
}
</script>
