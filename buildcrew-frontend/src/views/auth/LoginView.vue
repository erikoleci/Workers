<template>
  <v-container class="fill-height" fluid>
    <v-row justify="center" align="center" class="fill-height">
      <v-col cols="12" sm="8" md="4">
        <v-card elevation="4" class="pa-6">
          <v-card-title class="text-h5 mb-4">BuildCrew Manager</v-card-title>

          <v-form @submit.prevent="handleLogin">
            <v-text-field
              v-model="identifier"
              label="Email or Username"
              variant="outlined"
              density="comfortable"
              class="mb-2"
              required
            />
            <v-text-field
              v-model="password"
              label="Password"
              type="password"
              variant="outlined"
              density="comfortable"
              class="mb-4"
              required
            />

            <v-alert v-if="error" type="error" density="compact" class="mb-4">
              {{ error }}
            </v-alert>

            <v-btn
              type="submit"
              color="primary"
              block
              size="large"
              :loading="loading"
            >
              Sign In
            </v-btn>
          </v-form>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth.store'

const identifier = ref('')
const password = ref('')
const loading = ref(false)
const error = ref('')

const authStore = useAuthStore()
const router = useRouter()

async function handleLogin() {
  error.value = ''
  loading.value = true
  try {
    await authStore.login({ identifier: identifier.value, password: password.value })
    router.push('/dashboard')
  } catch (e) {
    error.value = 'Invalid credentials'
  } finally {
    loading.value = false
  }
}
</script>
