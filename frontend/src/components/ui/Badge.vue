<script setup>
defineProps({
  variant: { type: String, default: 'secondary' },
})
</script>

<template>
  <span
    class="inline-flex items-center rounded-md px-2 py-1 text-xs font-semibold"
    :class="variant === 'live' ? 'bg-red-50 text-red-600' : variant === 'primary' ? 'bg-blue-50 text-blue-700' : 'bg-muted text-muted-foreground'"
  >
    <slot />
  </span>
</template>
