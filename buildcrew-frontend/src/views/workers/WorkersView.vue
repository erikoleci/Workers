<template>
  <v-container fluid>
    <div class="d-flex justify-space-between align-center mb-4">
      <h1 class="text-h5">Workers</h1>
      <v-btn color="primary" prepend-icon="mdi-plus" @click="openCreate">Add Worker</v-btn>
    </div>

    <v-text-field
      v-model="workerStore.query"
      label="Search workers..."
      prepend-inner-icon="mdi-magnify"
      density="comfortable"
      variant="outlined"
      class="mb-4"
      @update:model-value="onSearchInput"
    />

    <v-data-table
      :items="workerStore.items"
      :loading="workerStore.loading"
      :headers="headers"
      :items-length="workerStore.total"
      :items-per-page="workerStore.size"
      :page="workerStore.page + 1"
      item-value="id"
      @update:page="onPageChange"
      @update:items-per-page="onSizeChange"
    >
      <template #item.payType="{ item }">
        {{ item.payType === 'daily' ? 'Per Day' : 'Per m²' }}
      </template>

      <template #item.status="{ item }">
        <v-switch
          :model-value="item.status === 'active'"
          color="success"
          density="compact"
          hide-details
          :label="item.status === 'active' ? 'Active' : 'Inactive'"
          @update:model-value="workerStore.toggleWorkerStatus(item.id)"
        />
      </template>

      <template #item.actions="{ item }">
        <v-btn icon="mdi-key" variant="text" size="small" @click="openCredentials(item)" title="Set login" />
        <v-btn icon="mdi-pencil" variant="text" size="small" @click="openEdit(item)" />
        <v-btn icon="mdi-delete" variant="text" size="small" color="error" @click="confirmDelete(item)" />
      </template>
    </v-data-table>

    <WorkerFormDialog
      v-model="dialogOpen"
      :worker="selectedWorker"
      @save="handleSave"
    />

    <WorkerCredentialsDialog
      v-model="credentialsDialogOpen"
      :worker="selectedWorker"
    />
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useWorkerStore } from '@/stores/worker.store'
import type { Worker, WorkerCreatePayload } from '@/types/worker.types'
import WorkerFormDialog from './WorkerFormDialog.vue'
import WorkerCredentialsDialog from './WorkerCredentialsDialog.vue'

const workerStore = useWorkerStore()
const dialogOpen = ref(false)
const credentialsDialogOpen = ref(false)
const selectedWorker = ref<Worker | null>(null)

const headers = [
  { title: 'Name', key: 'fullName' },
  { title: 'Phone', key: 'phone' },
  { title: 'Position', key: 'position' },
  { title: 'Pay Type', key: 'payType' },
  { title: 'Status', key: 'status' },
  { title: 'Actions', key: 'actions', sortable: false }
]

onMounted(() => {
  workerStore.fetchWorkers()
})

let searchTimeout: ReturnType<typeof setTimeout> | undefined
function onSearchInput() {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    workerStore.page = 0
    workerStore.fetchWorkers()
  }, 350)
}

function onPageChange(page: number) {
  workerStore.page = page - 1 // Vuetify is 1-indexed, backend is 0-indexed
  workerStore.fetchWorkers()
}

function onSizeChange(size: number) {
  workerStore.size = size
  workerStore.page = 0
  workerStore.fetchWorkers()
}

function openCreate() {
  selectedWorker.value = null
  dialogOpen.value = true
}

function openEdit(worker: Worker) {
  selectedWorker.value = worker
  dialogOpen.value = true
}

function openCredentials(worker: Worker) {
  selectedWorker.value = worker
  credentialsDialogOpen.value = true
}

async function handleSave(payload: WorkerCreatePayload) {
  if (selectedWorker.value) {
    await workerStore.updateWorker(selectedWorker.value.id, payload)
  } else {
    await workerStore.createWorker(payload)
  }
}

function confirmDelete(worker: Worker) {
  if (confirm(`Delete ${worker.fullName}? This cannot be undone.`)) {
    workerStore.deleteWorker(worker.id)
  }
}
</script>
