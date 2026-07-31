<template>
  <v-dialog v-model="model" max-width="550">
    <v-card>
      <v-card-title>{{ isEdit ? 'Edit Crew' : 'New Crew' }}</v-card-title>
      <v-card-text>
        <v-form @submit.prevent="handleSubmit">
          <v-text-field v-model="form.name" label="Crew Name" required class="mb-2" />

          <v-select
            v-model="form.leaderId"
            :items="leaderOptions"
            item-title="name"
            item-value="id"
            label="Crew Leader"
            hint="Only managers and crew leaders can lead a crew"
            persistent-hint
            clearable
            class="mb-2"
          />

          <v-select
            v-model="form.memberWorkerIds"
            :items="workerOptions"
            item-title="fullName"
            item-value="id"
            label="Members"
            multiple
            chips
            class="mb-2"
          />
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
import { ref, watch, computed, onMounted } from 'vue'
import type { Crew, CrewCreatePayload } from '@/types/crew.types'
import { workerService } from '@/services/worker.service'
import { userService } from '@/services/user.service'
import type { Worker } from '@/types/worker.types'
import type { AppUser } from '@/types/user.types'

const props = defineProps<{
  modelValue: boolean
  crew?: Crew | null
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  save: [payload: CrewCreatePayload]
}>()

const model = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v)
})

const isEdit = computed(() => !!props.crew)
const workerOptions = ref<Worker[]>([])
const leaderOptions = ref<AppUser[]>([])

const form = ref<CrewCreatePayload>({
  name: '',
  leaderId: undefined,
  memberWorkerIds: []
})

onMounted(async () => {
  const [workersRes, managersRes, leadersRes] = await Promise.all([
    workerService.search({ status: 'active', size: 100 }),
    userService.list('manager'),
    userService.list('crew_leader')
  ])
  workerOptions.value = workersRes.data.items
  leaderOptions.value = [...managersRes.data, ...leadersRes.data]
})

watch(
  () => props.crew,
  (crew) => {
    if (crew) {
      form.value = {
        name: crew.name,
        leaderId: crew.leaderId ?? undefined,
        currentProjectId: crew.currentProjectId ?? undefined,
        memberWorkerIds: crew.members.map((m) => m.workerId)
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
