<template>
  <v-container fluid>
    <div class="d-flex justify-space-between align-center mb-4">
      <h1 class="text-h5">Crews</h1>
      <v-btn color="primary" prepend-icon="mdi-plus" @click="openCreate">Add Crew</v-btn>
    </div>

    <v-text-field
      v-model="crewStore.query"
      label="Search crews..."
      prepend-inner-icon="mdi-magnify"
      density="comfortable"
      variant="outlined"
      class="mb-4"
      @update:model-value="crewStore.fetchCrews()"
    />

    <v-row>
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
    </v-row>

    <CrewFormDialog
      v-model="dialogOpen"
      :crew="selectedCrew"
      @save="handleSave"
    />
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useCrewStore } from '@/stores/crew.store'
import type { Crew, CrewCreatePayload } from '@/types/crew.types'
import CrewFormDialog from './CrewFormDialog.vue'

const crewStore = useCrewStore()
const dialogOpen = ref(false)
const selectedCrew = ref<Crew | null>(null)

onMounted(() => {
  crewStore.fetchCrews()
})

function openCreate() {
  selectedCrew.value = null
  dialogOpen.value = true
}

function openEdit(crew: Crew) {
  selectedCrew.value = crew
  dialogOpen.value = true
}

async function handleSave(payload: CrewCreatePayload) {
  if (selectedCrew.value) {
    await crewStore.updateCrew(selectedCrew.value.id, payload)
  } else {
    await crewStore.createCrew(payload)
  }
}
</script>
