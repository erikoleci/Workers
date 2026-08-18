<template>
  <v-dialog v-model="model" max-width="400">
    <v-card>
      <v-card-title>Set Daily Target</v-card-title>
      <v-card-text>
        <div class="text-caption mb-2">{{ worker?.fullName }}</div>

        <v-alert v-if="worker && worker.payType !== 'per_m2'" type="info" density="compact" class="mb-2">
          Ky punëtor paguhet me ditë - target m² nuk aplikohet.
        </v-alert>

        <template v-else>
          <v-text-field v-model="targetDate" label="Data" type="date" class="mb-2" />
          <v-text-field v-model.number="targetM2" label="Target (m²)" type="number" min="0" step="0.01" />
          <v-alert v-if="success" type="success" density="compact" class="mt-2">Target u ruajt.</v-alert>
          <v-alert v-if="error" type="error" density="compact" class="mt-2">{{ error }}</v-alert>
        </template>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn @click="model = false">Close</v-btn>
        <v-btn
          v-if="worker && worker.payType === 'per_m2'"
          color="primary"
          @click="handleSave"
          :loading="loading"
        >
          Save
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useWorkerStore } from '@/stores/worker.store'
import type { Worker } from '@/types/worker.types'

const props = defineProps<{ modelValue: boolean; worker: Worker | null }>()
const emit = defineEmits<{ 'update:modelValue': [value: boolean] }>()

const model = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v)
})

const workerStore = useWorkerStore()
const targetDate = ref(new Date().toISOString().slice(0, 10))
const targetM2 = ref<number | undefined>(undefined)
const loading = ref(false)
const success = ref(false)
const error = ref('')

watch(() => props.modelValue, (open) => {
  if (open) {
    targetDate.value = new Date().toISOString().slice(0, 10)
    targetM2.value = undefined
    success.value = false
    error.value = ''
  }
})

async function handleSave() {
  if (!props.worker || targetM2.value === undefined) return
  error.value = ''
  loading.value = true
  try {
    await workerStore.setWorkerTarget(props.worker.id, targetDate.value, targetM2.value)
    success.value = true
  } catch (e: any) {
    error.value = e.response?.data?.message ?? 'Failed to save target'
  } finally {
    loading.value = false
  }
}
</script>
