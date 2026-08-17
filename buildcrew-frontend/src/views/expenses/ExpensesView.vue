<template>
  <v-container fluid>
    <div class="d-flex justify-space-between align-center mb-4">
      <h1 class="text-h5">Expenses</h1>
      <v-btn color="primary" prepend-icon="mdi-plus" @click="createOpen = true">Add Expense</v-btn>
    </div>

    <v-alert v-if="expenseStore.error" type="error" density="compact" class="mb-4">
      {{ expenseStore.error }}
    </v-alert>

    <v-data-table
      :items="expenseStore.items"
      :loading="expenseStore.loading"
      :headers="headers"
      item-value="id"
    >
      <template #item.amount="{ item }">
        €{{ item.amount }}
      </template>

      <template #item.actions="{ item }">
        <v-btn icon="mdi-delete" variant="text" size="small" color="error" @click="handleDelete(item.id)" />
      </template>
    </v-data-table>

    <ExpenseCreateDialog v-model="createOpen" />
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useExpenseStore } from '@/stores/expense.store'
import ExpenseCreateDialog from './ExpenseCreateDialog.vue'

const expenseStore = useExpenseStore()
const createOpen = ref(false)

const headers = [
  { title: 'Date', key: 'expenseDate' },
  { title: 'Category', key: 'category' },
  { title: 'Amount', key: 'amount' },
  { title: 'Description', key: 'description' },
  { title: 'Actions', key: 'actions', sortable: false }
]

onMounted(() => {
  expenseStore.fetchExpenses()
})

async function handleDelete(id: string) {
  if (!confirm('Delete this expense?')) return
  await expenseStore.removeExpense(id)
}
</script>
