<template>
  <v-dialog v-model="model" max-width="480">
    <v-card>
      <v-card-title>New Team Member</v-card-title>
      <v-card-text>
        <v-alert v-if="errorMessage" type="error" density="compact" class="mb-4" closable @click:close="errorMessage = null">
          {{ errorMessage }}
        </v-alert>

        <v-form ref="formRef" @submit.prevent="handleSubmit">
          <v-text-field v-model="form.name" label="Full Name" :rules="[required]" class="mb-2" />
          <v-text-field v-model="form.email" label="Email" type="email" :rules="[required]" class="mb-2" />
          <v-text-field
            v-model="form.password"
            label="Password"
            type="password"
            :rules="[required, minLength]"
            hint="At least 6 characters"
            class="mb-2"
          />
          <v-select
            v-model="form.role"
            :items="roleOptions"
            item-title="label"
            item-value="value"
            label="Role"
            :rules="[required]"
            class="mb-2"
          />
          <v-text-field v-model="form.phone" label="Phone (optional)" class="mb-2" />
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn @click="model = false">Cancel</v-btn>
        <v-btn color="primary" :loading="submitting" @click="handleSubmit">Create</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useUserStore } from '@/stores/user.store'
import type { UserCreatePayload } from '@/types/user.types'

const props = defineProps<{ modelValue: boolean }>()
const emit = defineEmits<{ 'update:modelValue': [value: boolean] }>()

const model = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v)
})

const userStore = useUserStore()
const formRef = ref()
const submitting = ref(false)
const errorMessage = ref<string | null>(null)

const roleOptions = [
  { label: 'Manager', value: 'manager' },
  { label: 'Crew Leader', value: 'crew_leader' }
]

const form = ref<UserCreatePayload>({
  name: '',
  email: '',
  password: '',
  role: 'crew_leader',
  phone: ''
})

const required = (v: string) => !!v || 'Required'
const minLength = (v: string) => (v?.length ?? 0) >= 6 || 'At least 6 characters'

async function handleSubmit() {
  const { valid } = await formRef.value.validate()
  if (!valid) return

  submitting.value = true
  errorMessage.value = null
  try {
    await userStore.createUser(form.value)
    form.value = { name: '', email: '', password: '', role: 'crew_leader', phone: '' }
    model.value = false
  } catch (err: any) {
    // Surface the real backend message (e.g. "email already exists") instead
    // of failing silently with an uncaught promise rejection.
    errorMessage.value = err?.response?.data?.message ?? 'Could not create user. Please try again.'
  } finally {
    submitting.value = false
  }
}
</script>
