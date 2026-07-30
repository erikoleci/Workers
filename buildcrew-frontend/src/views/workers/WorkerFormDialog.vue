<template>
  <v-dialog v-model="model" max-width="500">
    <v-card>
      <v-card-title>{{ isEdit ? 'Edit Worker' : 'New Worker' }}</v-card-title>
      <v-card-text>
        <v-form @submit.prevent="handleSubmit">
          <v-text-field v-model="form.fullName" label="Full Name" required class="mb-2" />
          <v-text-field v-model="form.phone" label="Phone" class="mb-2" />
          <v-text-field v-model="form.position" label="Position" class="mb-2" />

          <v-select
            v-model="form.payType"
            :items="[{ title: 'Per Day', value: 'daily' }, { title: 'Per m²', value: 'per_m2' }]"
            label="Pay Type"
            class="mb-2"
          />

          <v-text-field
            v-if="form.payType === 'daily'"
            v-model.number="form.dailySalary"
            label="Daily Salary"
            type="number"
            class="mb-2"
          />
          <v-text-field
            v-else
            v-model.number="form.pricePerM2"
            label="Price per m²"
            type="number"
            class="mb-2"
          />

          <v-text-field v-model="form.employmentDate" label="Employment Date" type="date" class="mb-2" />
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn @click="model = false">Cancel</v-btn>
        <v-btn color="primary" @click="handleSubmit">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import type { Worker, WorkerCreatePayload } from '@/types/worker.types'

const props = defineProps<{
  modelValue: boolean
  worker?: Worker | null
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  save: [payload: WorkerCreatePayload]
}>()

const model = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v)
})

const isEdit = computed(() => !!props.worker)

const form = ref<WorkerCreatePayload>({
  fullName: '',
  phone: '',
  position: '',
  payType: 'daily',
  dailySalary: undefined,
  pricePerM2: undefined,
  employmentDate: ''
})

watch(
  () => props.worker,
  (worker) => {
    if (worker) {
      form.value = {
        fullName: worker.fullName,
        phone: worker.phone ?? '',
        position: worker.position ?? '',
        payType: worker.payType,
        dailySalary: worker.dailySalary ?? undefined,
        pricePerM2: worker.pricePerM2 ?? undefined,
        employmentDate: worker.employmentDate ?? ''
      }
    }
  },
  { immediate: true }
)

function handleSubmit() {
  emit('save', form.value)
  model.value = false
}
</script>
