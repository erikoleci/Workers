<template>
  <v-card elevation="2" class="pa-4" rounded="lg">
    <div class="d-flex align-center justify-space-between">
      <div>
        <div class="text-caption text-medium-emphasis">{{ label }}</div>
        <div class="text-h5 font-weight-bold mt-1">{{ value }}</div>
      </div>
      <v-icon :icon="icon" size="32" :color="color" />
    </div>
  </v-card>
</template>

<script setup lang="ts">
defineProps<{
  label: string
  value: string | number
  icon: string
  color?: string
}>()
</script>
