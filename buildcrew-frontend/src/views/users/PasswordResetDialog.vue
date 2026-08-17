<template>
  <v-dialog v-model="model" max-width="400">
    <v-card>
      <v-card-title>Reset Password</v-card-title>
      <v-card-text>
        <div class="text-caption mb-2">{{ user?.name }} ({{ user?.email }})</div>
        <v-text-field
          v-model="newPassword"
          label="New Password"
          :type="showPassword ? 'text' : 'password'"
          :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
          @click:append-inner="showPassword = !showPassword"
        />
        <v-alert v-if="success" type="success" density="compact" class="mt-2">Password updated.</v-alert>
        <v-alert v-if="error" type="error" density="compact" class="mt-2">{{ error }}</v-alert>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn @click="model = false">Close</v-btn>
        <v-btn color="primary" @click="handleReset" :loading="loading">Reset</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useUserStore } from '@/stores/user.store'
import type { AppUser } from '@/types/user.types'

const props = defineProps<{ modelValue: boolean; user: AppUser | null }>()
const emit = defineEmits<{ 'update:modelValue': [value: boolean] }>()

const model = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v)
})

const userStore = useUserStore()
const newPassword = ref('')
const showPassword = ref(false)
const loading = ref(false)
const success = ref(false)
const error = ref('')

watch(() => props.modelValue, (open) => {
  if (open) {
    newPassword.value = ''
    success.value = false
    error.value = ''
  }
})

async function handleReset() {
  if (!props.user) return
  if (newPassword.value.length < 6) {
    error.value = 'Password must be at least 6 characters'
    return
  }
  error.value = ''
  loading.value = true
  try {
    await userStore.resetPassword(props.user.id, newPassword.value)
    success.value = true
  } catch (e: any) {
    error.value = e.response?.data?.message ?? 'Failed to reset password'
  } finally {
    loading.value = false
  }
}
</script>
