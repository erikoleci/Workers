<template>
  <v-container fluid>
    <div class="d-flex justify-space-between align-center mb-4">
      <h1 class="text-h5">Clients</h1>
      <v-btn color="primary" prepend-icon="mdi-plus" @click="openCreate">Add Client</v-btn>
    </div>

    <v-text-field
      v-model="clientStore.query"
      label="Search clients..."
      prepend-inner-icon="mdi-magnify"
      density="comfortable"
      variant="outlined"
      class="mb-4"
      @update:model-value="clientStore.fetchClients()"
    />

    <v-data-table
      :items="clientStore.items"
      :loading="clientStore.loading"
      :headers="headers"
      item-value="id"
    >
      <template #item.actions="{ item }">
        <v-btn icon="mdi-pencil" variant="text" size="small" @click="openEdit(item)" />
        <v-btn icon="mdi-delete" variant="text" size="small" color="error" @click="clientStore.removeClient(item.id)" />
      </template>
    </v-data-table>

    <ClientFormDialog
      v-model="dialogOpen"
      :client="selectedClient"
      @save="handleSave"
    />
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useClientStore } from '@/stores/client.store'
import type { Client, ClientCreatePayload } from '@/types/client.types'
import ClientFormDialog from './ClientFormDialog.vue'

const clientStore = useClientStore()
const dialogOpen = ref(false)
const selectedClient = ref<Client | null>(null)

const headers = [
  { title: 'Company', key: 'companyName' },
  { title: 'Contact Person', key: 'contactPerson' },
  { title: 'Phone', key: 'phone' },
  { title: 'Email', key: 'email' },
  { title: 'Actions', key: 'actions', sortable: false }
]

onMounted(() => {
  clientStore.fetchClients()
})

function openCreate() {
  selectedClient.value = null
  dialogOpen.value = true
}

function openEdit(client: Client) {
  selectedClient.value = client
  dialogOpen.value = true
}

async function handleSave(payload: ClientCreatePayload) {
  if (selectedClient.value) {
    await clientStore.updateClient(selectedClient.value.id, payload)
  } else {
    await clientStore.createClient(payload)
  }
}
</script>
