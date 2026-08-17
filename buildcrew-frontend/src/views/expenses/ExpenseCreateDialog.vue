<template>
  <v-dialog v-model="model" max-width="450">
    <v-card>
      <v-card-title>New Expense</v-card-title>
      <v-card-text>
        <v-alert v-if="error" type="error" density="compact" class="mb-3">{{ error }}</v-alert>
        <v-form @submit.prevent="handleSubmit">
          <v-select
            v-model="form.projectId"
            :items="projectOptions"
            item-title="name"
            item-value="id"
            label="Project (optional)"
            clearable
            class="mb-2"
          />
          <v-text-field v-model="form.category" label="Category" placeholder="Materiale, Transport, etj." required class="mb-2" />
          <v-text-field v-model.number="form.amount" label="Amount (€)" type="number" required class="mb-2" />
          <v-text-field v-model="form.expenseDate" label="Date" type="date" required class="mb-2" />
          <v-textarea v-model="form.description" label="Description" rows="2" class="mb-2" />
        </v-form>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn @click="model = false">Cancel</v-btn>
        <v-btn color="primary" @click="handleSubmit" :loading="loading">Save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useExpenseStore } from '@/stores/expense.store'
import type { ExpenseCreatePayload } from '@/types/expense.types'
import { projectService } from '@/services/project.service'
import type { Project } from '@/types/project.types'

const props = defineProps<{ modelValue: boolean }>()
const emit = defineEmits<{ 'update:modelValue': [value: boolean] }>()

const model = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v)
})

const expenseStore = useExpenseStore()
const projectOptions = ref<Project[]>([])
const loading = ref(false)
const error = ref('')

const form = ref<ExpenseCreatePayload>({
  projectId: undefined,
  category: '',
  amount: 0,
  expenseDate: new Date().toISOString().split('T')[0],
  description: ''
})

onMounted(async () => {
  const { data } = await projectService.search({ size: 100 })
  projectOptions.value = data.items
})

async function handleSubmit() {
  error.value = ''
  if (!form.value.category || !form.value.amount || !form.value.expenseDate) {
    error.value = 'Category, amount and date are required'
    return
  }
  loading.value = true
  try {
    await expenseStore.createExpense(form.value)
    model.value = false
    form.value = { projectId: undefined, category: '', amount: 0, expenseDate: new Date().toISOString().split('T')[0], description: '' }
  } catch (e: any) {
    error.value = e.response?.data?.message ?? 'Failed to create expense'
  } finally {
    loading.value = false
  }
}
</script>
