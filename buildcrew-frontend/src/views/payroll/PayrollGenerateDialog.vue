<template>
  <v-dialog v-model="model" max-width="450">
    <v-card>
      <v-card-title>Generate Payroll</v-card-title>
      <v-card-text>
        <v-row>
          <v-col cols="6">
            <v-text-field v-model="periodStart" label="Period Start" type="date" />
          </v-col>
          <v-col cols="6">
            <v-text-field v-model="periodEnd" label="Period End" type="date" />
          </v-col>
        </v-row>
        <div class="text-caption text-medium-emphasis">
          Generates for all active workers automatically.
        </div>
        <v-alert v-if="error" type="error" density="compact" class="mt-3">{{ error }}</v-alert>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn @click="model = false">Cancel</v-btn>
        <v-btn color="primary" @click="handleGenerate" :loading="loading">Generate</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { usePayrollStore } from '@/stores/payroll.store'

const props = defineProps<{ modelValue: boolean }>()
const emit = defineEmits<{ 'update:modelValue': [value: boolean] }>()

const model = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v)
})

const payrollStore = usePayrollStore()
const periodStart = ref('')
const periodEnd = ref('')
const loading = ref(false)
const error = ref('')

async function handleGenerate() {
  error.value = ''
  if (!periodStart.value || !periodEnd.value) {
    error.value = 'Both dates are required'
    return
  }
  loading.value = true
  try {
    await payrollStore.generatePayroll({ periodStart: periodStart.value, periodEnd: periodEnd.value })
    model.value = false
  } catch (e: any) {
    error.value = e.response?.data?.message ?? 'Failed to generate payroll'
  } finally {
    loading.value = false
  }
}
</script>
