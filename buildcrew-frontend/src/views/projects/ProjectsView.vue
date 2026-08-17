<template>
  <v-container fluid>
    <div class="d-flex justify-space-between align-center mb-4">
      <h1 class="text-h5">Projects</h1>
      <v-btn color="primary" prepend-icon="mdi-plus" @click="openCreate">Add Project</v-btn>
    </div>

    <v-alert v-if="projectStore.error" type="error" density="compact" class="mb-4">
      {{ projectStore.error }}
    </v-alert>

    <v-row class="mb-2">
      <v-col cols="12" sm="6">
        <v-text-field
          v-model="projectStore.query"
          label="Search projects..."
          prepend-inner-icon="mdi-magnify"
          density="comfortable"
          variant="outlined"
          @update:model-value="projectStore.fetchProjects()"
        />
      </v-col>
      <v-col cols="12" sm="6">
        <v-select
          v-model="projectStore.statusFilter"
          :items="statusOptions"
          label="Filter by status"
          clearable
          density="comfortable"
          variant="outlined"
          @update:model-value="projectStore.fetchProjects()"
        />
      </v-col>
    </v-row>

    <v-row v-if="projectStore.loading">
      <v-col cols="12" class="text-center pa-8">
        <v-progress-circular indeterminate color="primary" />
      </v-col>
    </v-row>

    <v-row v-else-if="!projectStore.items.length">
      <v-col cols="12">
        <span class="text-medium-emphasis">No projects yet. Add your first one.</span>
      </v-col>
    </v-row>

    <v-row v-else>
      <v-col v-for="project in projectStore.items" :key="project.id" cols="12" md="6">
        <v-card elevation="2" rounded="lg">
          <v-card-title class="d-flex justify-space-between">
            {{ project.name }}
            <v-chip :color="statusColor(project.status)" size="small">{{ project.status }}</v-chip>
          </v-card-title>
          <v-card-subtitle>{{ project.clientName }} · Crew: {{ project.assignedCrewName ?? 'Unassigned' }}</v-card-subtitle>

          <v-card-text>
            <div class="d-flex justify-space-between text-caption mb-1">
              <span>{{ project.completedM2 }} / {{ project.totalM2 ?? 0 }} m²</span>
              <span>{{ project.progressPercent }}%</span>
            </div>
            <v-progress-linear
              :model-value="project.progressPercent"
              :color="progressColor(project.progressPercent)"
              height="10"
              rounded
            />
            <div class="text-caption mt-2">Deadline: {{ project.deadline ?? 'N/A' }}</div>
          </v-card-text>

          <v-card-actions>
            <v-btn variant="text" prepend-icon="mdi-pencil" @click="openEdit(project)">Edit</v-btn>
            <v-select
              :model-value="project.status"
              :items="statusOptions"
              density="compact"
              variant="plain"
              hide-details
              style="max-width: 140px"
              @update:model-value="(val) => projectStore.updateStatus(project.id, val as any)"
            />
            <v-spacer />
            <v-btn variant="text" color="error" icon="mdi-delete" size="small" @click="handleDelete(project.id)" />
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>

    <ProjectFormDialog
      v-model="dialogOpen"
      :project="selectedProject"
      @save="handleSave"
    />
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useProjectStore } from '@/stores/project.store'
import type { Project, ProjectCreatePayload } from '@/types/project.types'
import ProjectFormDialog from './ProjectFormDialog.vue'

const projectStore = useProjectStore()
const dialogOpen = ref(false)
const selectedProject = ref<Project | null>(null)

const statusOptions = ['active', 'delayed', 'completed', 'cancelled']

onMounted(() => {
  projectStore.fetchProjects()
})

function statusColor(status: string) {
  switch (status) {
    case 'active': return 'success'
    case 'delayed': return 'error'
    case 'completed': return 'primary'
    default: return 'grey'
  }
}

function progressColor(percent: number) {
  if (percent >= 95) return 'success'
  if (percent >= 70) return 'warning'
  return 'error'
}

function openCreate() {
  selectedProject.value = null
  dialogOpen.value = true
}

function openEdit(project: Project) {
  selectedProject.value = project
  dialogOpen.value = true
}

async function handleSave(payload: ProjectCreatePayload) {
  try {
    if (selectedProject.value) {
      await projectStore.updateProject(selectedProject.value.id, payload)
    } else {
      await projectStore.createProject(payload)
    }
  } catch {
    // error already surfaced via projectStore.error
  }
}

async function handleDelete(id: string) {
  if (!confirm('Delete this project? This cannot be undone.')) return
  try {
    await projectStore.removeProject(id)
  } catch {
    // error already surfaced via projectStore.error
  }
}
</script>
