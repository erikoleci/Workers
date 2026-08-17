<template>
  <v-dialog v-model="model" max-width="600">
    <v-card>
      <v-card-title>{{ isEdit ? 'Edit Project' : 'New Project' }}</v-card-title>
      <v-card-text>
        <v-alert v-if="error" type="error" density="compact" class="mb-3">{{ error }}</v-alert>
        <v-form @submit.prevent="handleSubmit">
          <v-select
            v-model="form.clientId"
            :items="clientOptions"
            item-title="companyName"
            item-value="id"
            label="Client"
            required
            class="mb-2"
          />
          <v-text-field v-model="form.name" label="Project Name" required class="mb-2" />
          <v-text-field v-model="form.address" label="Address" class="mb-2" />

          <v-row>
            <v-col cols="6">
              <v-text-field v-model="form.startDate" label="Start Date" type="date" />
            </v-col>
            <v-col cols="6">
              <v-text-field v-model="form.deadline" label="Deadline" type="date" />
            </v-col>
          </v-row>

          <v-row>
            <v-col cols="6">
              <v-text-field v-model.number="form.contractValue" label="Contract Value (€)" type="number" />
            </v-col>
            <v-col cols="6">
              <v-text-field v-model.number="form.totalM2" label="Total m²" type="number" />
            </v-col>
          </v-row>

          <v-select
            v-model="form.assignedCrewId"
            :items="crewOptions"
            item-title="name"
            item-value="id"
            label="Assigned Crew"
            clearable
            class="mb-2"
          />
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn @click="model = false">Cancel</v-btn>
        <v-btn color="primary" @click="handleSubmit" :loading="loading">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, watch, computed, onMounted } from 'vue'
import type { Project, ProjectCreatePayload } from '@/types/project.types'
import { clientService } from '@/services/client.service'
import { crewService } from '@/services/crew.service'
import type { Client } from '@/types/client.types'
import type { Crew } from '@/types/crew.types'

const props = defineProps<{
  modelValue: boolean
  project?: Project | null
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  save: [payload: ProjectCreatePayload]
}>()

const model = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v)
})

const isEdit = computed(() => !!props.project)
const clientOptions = ref<Client[]>([])
const crewOptions = ref<Crew[]>([])
const loading = ref(false)
const error = ref('')

const form = ref<ProjectCreatePayload>({
  clientId: '',
  name: '',
  address: '',
  startDate: '',
  deadline: '',
  contractValue: undefined,
  totalM2: undefined,
  assignedCrewId: undefined
})

onMounted(async () => {
  const [clientsRes, crewsRes] = await Promise.all([
    clientService.search({ size: 100 }),
    crewService.search({ size: 100 })
  ])
  clientOptions.value = clientsRes.data.items
  crewOptions.value = crewsRes.data.items
})

watch(
  () => props.project,
  (project) => {
    if (project) {
      form.value = {
        clientId: project.clientId,
        name: project.name,
        address: project.address ?? '',
        startDate: project.startDate ?? '',
        deadline: project.deadline ?? '',
        contractValue: project.contractValue ?? undefined,
        totalM2: project.totalM2 ?? undefined,
        assignedCrewId: project.assignedCrewId ?? undefined
      }
    } else {
      form.value = {
        clientId: '',
        name: '',
        address: '',
        startDate: '',
        deadline: '',
        contractValue: undefined,
        totalM2: undefined,
        assignedCrewId: undefined
      }
    }
  },
  { immediate: true }
)

async function handleSubmit() {
  error.value = ''
  if (!form.value.clientId || !form.value.name) {
    error.value = 'Client and project name are required'
    return
  }
  loading.value = true
  try {
    emit('save', form.value)
    model.value = false
  } finally {
    loading.value = false
  }
}
</script>
