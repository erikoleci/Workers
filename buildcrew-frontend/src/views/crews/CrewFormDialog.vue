<template>
  <v-dialog v-model="model" max-width="550">
    <v-card>
      <v-card-title>{{ isEdit ? 'Edit Crew' : 'New Crew' }}</v-card-title>
      <v-card-text>
        <v-form @submit.prevent="handleSubmit">
          <v-text-field v-model="form.name" label="Crew Name" required class="mb-2" />

          <v-select
            v-model="form.leaderId"
            :items="workerOptions"
            item-title="fullName"
            item-value="id"
            label="Crew Leader"
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
import type { Worker } from '@/types/worker.types'

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

const form = ref<CrewCreatePayload>({
  name: '',
  leaderId: undefined,
  memberWorkerIds: []
})

onMounted(async () => {
  const { data } = await workerService.search({ status: 'active', size: 100 })
  workerOptions.value = data.items
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
