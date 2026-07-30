<template>
  <v-dialog v-model="model" max-width="500">
    <v-card>
      <v-card-title>{{ isEdit ? 'Edit Client' : 'New Client' }}</v-card-title>
      <v-card-text>
        <v-form @submit.prevent="handleSubmit">
          <v-text-field v-model="form.companyName" label="Company Name" required class="mb-2" />
          <v-text-field v-model="form.contactPerson" label="Contact Person" class="mb-2" />
          <v-text-field v-model="form.phone" label="Phone" class="mb-2" />
          <v-text-field v-model="form.email" label="Email" type="email" class="mb-2" />
          <v-textarea v-model="form.address" label="Address" rows="2" class="mb-2" />
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
import type { Client, ClientCreatePayload } from '@/types/client.types'

const props = defineProps<{
  modelValue: boolean
  client?: Client | null
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  save: [payload: ClientCreatePayload]
}>()

const model = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v)
})

const isEdit = computed(() => !!props.client)

const form = ref<ClientCreatePayload>({
  companyName: '',
  contactPerson: '',
  phone: '',
  email: '',
  address: ''
})

watch(
  () => props.client,
  (client) => {
    if (client) {
      form.value = {
        companyName: client.companyName,
        contactPerson: client.contactPerson ?? '',
        phone: client.phone ?? '',
        email: client.email ?? '',
        address: client.address ?? ''
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
