<template>
  <v-container fluid class="pa-4" style="max-width: 500px">
    <h1 class="text-h6 mb-4">Daily Report</h1>

    <v-form @submit.prevent="handleSubmit">
      <v-select
        v-model="form.projectId"
        :items="projectOptions"
        item-title="name"
        item-value="id"
        label="Project"
        required
        class="mb-3"
      />

      <v-select
        v-model="form.crewId"
        :items="crewOptions"
        item-title="name"
        item-value="id"
        label="Crew"
        required
        class="mb-3"
      />

      <v-text-field v-model="form.reportDate" label="Date" type="date" required class="mb-3" />

      <v-text-field
        v-model.number="form.completedM2"
        label="Completed m²"
        type="number"
        required
        class="mb-3"
      />

      <v-text-field
        v-model.number="form.workedHours"
        label="Worked Hours"
        type="number"
        class="mb-3"
      />

      <v-textarea v-model="form.comments" label="Comments" rows="3" class="mb-3" />

      <v-alert v-if="success" type="success" density="compact" class="mb-3">
        Report submitted successfully.
      </v-alert>
      <v-alert v-if="error" type="error" density="compact" class="mb-3">
        {{ error }}
      </v-alert>

      <v-btn type="submit" color="primary" block size="large" :loading="submitting">
        Submit Report
      </v-btn>
    </v-form>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useDailyReportStore } from '@/stores/dailyreport.store'
import type { DailyReportCreatePayload } from '@/types/dailyreport.types'
import { projectService } from '@/services/project.service'
import { crewService } from '@/services/crew.service'
import type { Project } from '@/types/project.types'
import type { Crew } from '@/types/crew.types'

const dailyReportStore = useDailyReportStore()
const projectOptions = ref<Project[]>([])
const crewOptions = ref<Crew[]>([])
const submitting = ref(false)
const success = ref(false)
const error = ref('')

const form = ref<DailyReportCreatePayload>({
  projectId: '',
  crewId: '',
  reportDate: new Date().toISOString().split('T')[0],
  completedM2: 0,
  workedHours: undefined,
  comments: ''
})

onMounted(async () => {
  try {
    const [projectsRes, crewsRes] = await Promise.all([
      projectService.search({ status: 'active', size: 100 }),
      crewService.search({ size: 100 })
    ])
    projectOptions.value = projectsRes.data.items
    crewOptions.value = crewsRes.data.items
  } catch {
    error.value = 'Failed to load projects/crews'
  }
})

async function handleSubmit() {
  success.value = false
  error.value = ''
  submitting.value = true
  try {
    await dailyReportStore.submitReport(form.value)
    success.value = true
    form.value.completedM2 = 0
    form.value.workedHours = undefined
    form.value.comments = ''
  } catch (e: any) {
    error.value = e.response?.data?.message ?? 'Failed to submit report'
  } finally {
    submitting.value = false
  }
}
</script>
