<template>
  <v-container fluid>
    <div class="d-flex justify-space-between align-center mb-4">
      <h1 class="text-h5">Payroll</h1>
      <v-btn color="primary" prepend-icon="mdi-calculator" @click="generateOpen = true">Generate Payroll</v-btn>
    </div>

    <v-alert v-if="payrollStore.error" type="error" density="compact" class="mb-4">
      {{ payrollStore.error }}
    </v-alert>

    <v-select
      v-model="payrollStore.statusFilter"
      :items="['pending', 'paid']"
      label="Filter by status"
      clearable
      density="comfortable"
      variant="outlined"
      class="mb-4"
      style="max-width: 250px"
      @update:model-value="payrollStore.fetchPayroll()"
    />

    <v-data-table
      :items="payrollStore.items"
      :loading="payrollStore.loading"
      :headers="headers"
      item-value="id"
    >
      <template #item.period="{ item }">
        {{ item.periodStart }} → {{ item.periodEnd }}
      </template>

      <template #item.bonuses="{ item }">
        <v-text-field
          :model-value="item.bonuses"
          type="number"
          density="compact"
          variant="plain"
          hide-details
          style="max-width: 90px"
          @change="(e: any) => adjust(item.id, { bonuses: Number(e.target.value) })"
        />
      </template>

      <template #item.deductions="{ item }">
        <v-text-field
          :model-value="item.deductions"
          type="number"
          density="compact"
          variant="plain"
          hide-details
          style="max-width: 90px"
          @change="(e: any) => adjust(item.id, { deductions: Number(e.target.value) })"
        />
      </template>

      <template #item.finalAmount="{ item }">
        <strong>€{{ item.finalAmount }}</strong>
      </template>

      <template #item.status="{ item }">
        <v-chip :color="item.status === 'paid' ? 'success' : 'warning'" size="small">
          {{ item.status }}
        </v-chip>
      </template>

      <template #item.actions="{ item }">
        <v-btn
          v-if="item.status === 'pending'"
          size="small"
          color="primary"
          variant="tonal"
          @click="payrollStore.markPaid(item.id)"
        >
          Mark Paid
        </v-btn>
      </template>
    </v-data-table>

    <PayrollGenerateDialog v-model="generateOpen" />
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { usePayrollStore } from '@/stores/payroll.store'
import type { PayrollAdjustPayload } from '@/types/payroll.types'
import PayrollGenerateDialog from './PayrollGenerateDialog.vue'

const payrollStore = usePayrollStore()
const generateOpen = ref(false)

const headers = [
  { title: 'Worker', key: 'workerName' },
  { title: 'Period', key: 'period' },
  { title: 'Base', key: 'baseAmount' },
  { title: 'Bonuses', key: 'bonuses' },
  { title: 'Deductions', key: 'deductions' },
  { title: 'Final', key: 'finalAmount' },
  { title: 'Status', key: 'status' },
  { title: 'Actions', key: 'actions', sortable: false }
]

onMounted(() => {
  payrollStore.fetchPayroll()
})

function adjust(id: string, payload: PayrollAdjustPayload) {
  payrollStore.adjustPayroll(id, payload)
}
</script>
