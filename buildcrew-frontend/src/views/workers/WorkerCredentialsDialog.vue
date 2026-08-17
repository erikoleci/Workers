<template>
  <v-dialog v-model="model" max-width="400">
    <v-card>
      <v-card-title>Set Login for Worker</v-card-title>
      <v-card-text>
        <div class="text-caption mb-2">{{ worker?.fullName }}</div>
        <v-text-field v-model="username" label="Username" class="mb-2" />
        <v-text-field
          v-model="password"
          label="Password"
          :type="showPassword ? 'text' : 'password'"
          :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
          @click:append-inner="showPassword = !showPassword"
        />
        <v-alert v-if="success" type="success" density="compact" class="mt-2">Login saved.</v-alert>
        <v-alert v-if="error" type="error" density="compact" class="mt-2">{{ error }}</v-alert>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn @click="model = false">Close</v-btn>
        <v-btn color="primary" @click="handleSave" :loading="loading">Save</v-btn>
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
const username = ref('')
const password = ref('')
const showPassword = ref(false)
const loading = ref(false)
const success = ref(false)
const error = ref('')

watch(() => props.modelValue, (open) => {
  if (open) {
    username.value = ''
    password.value = ''
    success.value = false
    error.value = ''
  }
})

async function handleSave() {
  if (!props.worker) return
  if (!username.value.trim()) {
    error.value = 'Username is required'
    return
  }
  if (password.value.length < 6) {
    error.value = 'Password must be at least 6 characters'
    return
  }
  error.value = ''
  loading.value = true
  try {
    await workerStore.setWorkerCredentials(props.worker.id, username.value.trim(), password.value)
    success.value = true
  } catch (e: any) {
    error.value = e.response?.data?.message ?? 'Failed to save login'
  } finally {
    loading.value = false
  }
}
</script>
