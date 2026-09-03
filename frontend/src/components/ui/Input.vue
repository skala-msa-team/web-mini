<script setup>
defineOptions({ inheritAttrs: false })
</script>

<template>
  <input v-bind="$attrs" class="flex h-10 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm outline-none transition placeholder:text-muted-foreground focus-visible:ring-2 focus-visible:ring-ring disabled:cursor-not-allowed disabled:opacity-50" />
</template>
