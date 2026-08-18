<template>
  <v-container fluid class="pa-4" style="max-width: 480px">
    <h1 class="text-h5 mb-4">Raporti Ditor</h1>

    <v-alert v-if="store.error" type="error" density="compact" class="mb-4">
      {{ store.error }}
    </v-alert>
    <v-alert v-if="store.success" type="success" density="compact" class="mb-4">
      Raporti u dërgua me sukses!
    </v-alert>

    <!-- Daily-pay workers don't log m2 - nothing for them to do here -->
    <v-card v-if="store.context?.payType === 'daily'" class="pa-4 mb-4" color="info" variant="tonal">
      Ti paguhesh me ditë pune - s'ke nevojë të shkruash m². Kjo faqe është vetëm për punëtorët që paguhen me metër katror.
    </v-card>

    <template v-else>
      <v-card v-if="store.context?.todayTarget" class="pa-4 mb-4" color="primary" variant="tonal">
        Target-i yt sot: <strong>{{ store.context.todayTarget.targetM2 }} m²</strong>
      </v-card>

      <v-card v-if="!store.loading && store.projects.length === 0" class="pa-4 mb-4" color="warning" variant="tonal">
        S'je i caktuar në asnjë projekt aktiv sot. Kontakto menaxherin.
      </v-card>

      <v-form v-else @submit.prevent="handleSubmit">
        <v-select
          v-model="form.projectId"
          :items="store.projects"
          item-title="projectName"
          item-value="projectId"
          label="Projekti"
          variant="outlined"
          density="comfortable"
          class="mb-2"
          required
        />
        <v-text-field
          v-model="form.reportDate"
          label="Data"
          type="date"
          variant="outlined"
          density="comfortable"
          class="mb-2"
          required
        />
        <v-text-field
          v-model.number="form.completedM2"
          label="m² të kompletuara sot"
          type="number"
          min="0"
          step="0.01"
          variant="outlined"
          density="comfortable"
          class="mb-2"
          required
        />
        <v-textarea
          v-model="form.comments"
          label="Shënime (opsionale)"
          variant="outlined"
          density="comfortable"
          rows="2"
          class="mb-4"
        />

        <v-btn type="submit" color="primary" block size="large" :loading="store.submitting">
          Dërgo Raportin
        </v-btn>
      </v-form>
    </template>

    <v-divider class="my-6" />

    <h2 class="text-subtitle-1 mb-2">Raportet e mia të fundit</h2>
    <v-list density="compact">
      <v-list-item v-for="r in store.myReports" :key="r.id">
        <v-list-item-title>{{ r.reportDate }} — {{ r.projectName }}</v-list-item-title>
        <v-list-item-subtitle>{{ r.completedM2 }} m²</v-list-item-subtitle>
      </v-list-item>
      <v-list-item v-if="store.myReports.length === 0">
        <span class="text-medium-emphasis">Ende s'ke dërguar asnjë raport.</span>
      </v-list-item>
    </v-list>
  </v-container>
</template>

<script setup lang="ts">
import { onMounted, reactive } from 'vue'
import { useWorkerReportStore } from '@/stores/workerreport.store'

const store = useWorkerReportStore()

const form = reactive({
  projectId: '',
  reportDate: new Date().toISOString().slice(0, 10),
  completedM2: undefined as number | undefined,
  comments: ''
})

onMounted(async () => {
  await store.fetchContext()
  if (store.projects.length === 1) {
    form.projectId = store.projects[0].projectId
  }
  await store.fetchMyReports()
})

async function handleSubmit() {
  if (!form.projectId || form.completedM2 === undefined) return
  await store.submit({
    projectId: form.projectId,
    reportDate: form.reportDate,
    completedM2: form.completedM2,
    comments: form.comments || undefined
  })
  form.completedM2 = undefined
  form.comments = ''
}
</script>
