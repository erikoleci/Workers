<template>
  <v-container fluid>
    <h1 class="text-h5 mb-4">Daily Targets</h1>

    <v-alert v-if="error" type="error" density="compact" class="mb-4">{{ error }}</v-alert>

    <v-select
      v-model="selectedProjectId"
      :items="projectOptions"
      item-title="name"
      item-value="id"
      label="Select Project"
      density="comfortable"
      variant="outlined"
      class="mb-4"
      style="max-width: 400px"
      @update:model-value="loadTargets"
    />

    <v-card v-if="selectedProjectId" elevation="2" rounded="lg" class="pa-4 mb-4">
      <div class="text-subtitle-1 mb-2">Set / Update Target</div>
      <v-row align="center">
        <v-col cols="12" sm="4">
          <v-text-field v-model="newDate" label="Date" type="date" density="compact" />
        </v-col>
        <v-col cols="12" sm="4">
          <v-text-field v-model.number="newTargetM2" label="Target m²" type="number" density="compact" />
        </v-col>
        <v-col cols="12" sm="4">
          <v-btn color="primary" @click="handleSave" :loading="saving">Save Target</v-btn>
        </v-col>
      </v-row>
    </v-card>

    <v-data-table
      v-if="selectedProjectId"
      :items="targets"
      :loading="loading"
      :headers="headers"
      item-value="id"
    />
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { dailyTargetService } from '@/services/dailytarget.service'
import { projectService } from '@/services/project.service'
import type { DailyTarget } from '@/types/dailytarget.types'
import type { Project } from '@/types/project.types'

const projectOptions = ref<Project[]>([])
const selectedProjectId = ref('')
const targets = ref<DailyTarget[]>([])
const loading = ref(false)
const saving = ref(false)
const error = ref('')

const newDate = ref(new Date().toISOString().split('T')[0])
const newTargetM2 = ref<number>(0)

const headers = [
  { title: 'Date', key: 'targetDate' },
  { title: 'Target m²', key: 'targetM2' }
]

onMounted(async () => {
  const { data } = await projectService.search({ status: 'active', size: 100 })
  projectOptions.value = data.items
})

async function loadTargets() {
  if (!selectedProjectId.value) return
  loading.value = true
  error.value = ''
  try {
    const { data } = await dailyTargetService.findByProject(selectedProjectId.value)
    targets.value = data
  } catch (e: any) {
    error.value = e.response?.data?.message ?? 'Failed to load targets'
  } finally {
    loading.value = false
  }
}

async function handleSave() {
  if (!selectedProjectId.value || !newDate.value || !newTargetM2.value) {
    error.value = 'Select a project, date, and target m²'
    return
  }
  saving.value = true
  error.value = ''
  try {
    await dailyTargetService.create({
      projectId: selectedProjectId.value,
      targetDate: newDate.value,
      targetM2: newTargetM2.value
    })
    await loadTargets()
  } catch (e: any) {
    error.value = e.response?.data?.message ?? 'Failed to save target'
  } finally {
    saving.value = false
  }
}
</script>
