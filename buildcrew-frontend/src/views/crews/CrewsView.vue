<template>
  <v-container fluid>
    <div class="d-flex justify-space-between align-center mb-4">
      <h1 class="text-h5">Crews</h1>
      <v-btn color="primary" prepend-icon="mdi-plus" @click="openCreate">Add Crew</v-btn>
    </div>

    <v-alert v-if="errorMessage" type="error" density="compact" class="mb-4" closable @click:close="errorMessage = null">
      {{ errorMessage }}
    </v-alert>

    <v-text-field
      v-model="crewStore.query"
      label="Search crews..."
      prepend-inner-icon="mdi-magnify"
      density="comfortable"
      variant="outlined"
      class="mb-4"
      @update:model-value="onSearchInput"
    />

    <v-row v-if="crewStore.loading">
      <v-col cols="12" class="text-center pa-8">
        <v-progress-circular indeterminate color="primary" />
      </v-col>
    </v-row>

    <v-row v-else>
      <v-col v-for="crew in crewStore.items" :key="crew.id" cols="12" sm="6" md="4">
        <v-card elevation="2" rounded="lg">
          <v-card-title class="d-flex justify-space-between">
            {{ crew.name }}
            <v-chip :color="crew.status === 'active' ? 'success' : 'grey'" size="small">
              {{ crew.status }}
            </v-chip>
          </v-card-title>
          <v-card-subtitle>Leader: {{ crew.leaderName ?? 'Not assigned' }}</v-card-subtitle>
          <v-card-text>
            <div class="text-caption mb-1">Members ({{ crew.members.length }}):</div>
            <v-chip v-for="m in crew.members" :key="m.workerId" size="small" class="mr-1 mb-1">
              {{ m.workerName }}
            </v-chip>
          </v-card-text>
          <v-card-actions>
            <v-btn variant="text" prepend-icon="mdi-pencil" @click="openEdit(crew)">Edit</v-btn>
            <v-btn variant="text" color="error" prepend-icon="mdi-delete" @click="crewStore.deactivateCrew(crew.id)">
              Deactivate
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>

      <v-col v-if="!crewStore.items.length" cols="12" class="text-center text-medium-emphasis pa-8">
        No crews found.
      </v-col>
    </v-row>

    <div v-if="totalPages > 1" class="d-flex justify-center mt-4">
      <v-pagination
        :model-value="crewStore.page + 1"
        :length="totalPages"
        @update:model-value="onPageChange"
      />
    </div>

    <CrewFormDialog
      v-model="dialogOpen"
      :crew="selectedCrew"
      @save="handleSave"
    />
  </v-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useCrewStore } from '@/stores/crew.store'
import type { Crew, CrewCreatePayload } from '@/types/crew.types'
import CrewFormDialog from './CrewFormDialog.vue'

const crewStore = useCrewStore()
const dialogOpen = ref(false)
const selectedCrew = ref<Crew | null>(null)
const errorMessage = ref<string | null>(null)

const totalPages = computed(() => Math.max(1, Math.ceil(crewStore.total / crewStore.size)))

onMounted(() => {
  crewStore.fetchCrews()
})

let searchTimeout: ReturnType<typeof setTimeout> | undefined
function onSearchInput() {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    crewStore.page = 0
    crewStore.fetchCrews()
  }, 350)
}

function onPageChange(page: number) {
  crewStore.page = page - 1
  crewStore.fetchCrews()
}

function openCreate() {
  selectedCrew.value = null
  dialogOpen.value = true
}

function openEdit(crew: Crew) {
  selectedCrew.value = crew
  dialogOpen.value = true
}

async function handleSave(payload: CrewCreatePayload) {
  errorMessage.value = null
  try {
    if (selectedCrew.value) {
      await crewStore.updateCrew(selectedCrew.value.id, payload)
    } else {
      await crewStore.createCrew(payload)
    }
  } catch (err: any) {
    errorMessage.value = err?.response?.data?.message ?? 'Could not save crew. Please check the leader/members and try again.'
  }
}
</script>
