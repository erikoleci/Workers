<template>
  <v-container fluid>
    <div class="d-flex justify-space-between align-center mb-4">
      <h1 class="text-h5">Team Members</h1>
      <v-btn color="primary" prepend-icon="mdi-account-plus" @click="dialogOpen = true">
        Add Team Member
      </v-btn>
    </div>

    <v-alert v-if="userStore.error" type="error" density="compact" class="mb-4">
      {{ userStore.error }}
    </v-alert>

    <v-card>
      <v-list>
        <v-list-item v-if="userStore.loading">
          <v-progress-circular indeterminate color="primary" size="24" />
        </v-list-item>

        <v-list-item v-else-if="!userStore.items.length">
          <span class="text-medium-emphasis">No team members yet. Add your first manager or crew leader.</span>
        </v-list-item>

        <v-list-item
          v-for="u in userStore.items"
          :key="u.id"
          :title="u.name"
          :subtitle="u.email"
        >
          <template #prepend>
            <v-avatar color="primary" variant="tonal">
              <v-icon icon="mdi-account" />
            </v-avatar>
          </template>
          <template #append>
            <v-chip size="small" class="mr-2" :color="roleColor(u.role)">
              {{ roleLabel(u.role) }}
            </v-chip>
            <v-btn
              icon="mdi-key"
              variant="text"
              size="small"
              @click="openReset(u)"
            />
            <v-btn
              v-if="u.role !== 'owner'"
              icon="mdi-delete"
              variant="text"
              color="error"
              size="small"
              @click="handleDeactivate(u.id)"
            />
          </template>
        </v-list-item>
      </v-list>
    </v-card>

    <UserFormDialog v-model="dialogOpen" />
    <PasswordResetDialog v-model="resetOpen" :user="selectedUser" />
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user.store'
import type { AppUser } from '@/types/user.types'
import UserFormDialog from './UserFormDialog.vue'
import PasswordResetDialog from './PasswordResetDialog.vue'

const userStore = useUserStore()
const dialogOpen = ref(false)
const resetOpen = ref(false)
const selectedUser = ref<AppUser | null>(null)

onMounted(() => {
  userStore.fetchUsers()
})

function openReset(user: AppUser) {
  selectedUser.value = user
  resetOpen.value = true
}

function roleLabel(role: string) {
  if (role === 'owner') return 'Owner'
  if (role === 'manager') return 'Manager'
  return 'Crew Leader'
}

function roleColor(role: string) {
  if (role === 'owner') return 'primary'
  if (role === 'manager') return 'secondary'
  return 'default'
}

async function handleDeactivate(id: string) {
  if (!confirm('Deactivate this team member? They will no longer be able to log in.')) return
  try {
    await userStore.deactivateUser(id)
  } catch {
    // userStore.error is already set and shown in the template; nothing else to do here.
  }
}
</script>
