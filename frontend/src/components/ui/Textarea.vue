<script setup>
defineOptions({ inheritAttrs: false })

const model = defineModel()
</script>

<template>
  <textarea v-model="model" v-bind="$attrs" class="flex min-h-24 w-full resize-y rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none transition placeholder:text-muted-foreground focus-visible:ring-2 focus-visible:ring-ring disabled:cursor-not-allowed disabled:opacity-50" />
</template>
