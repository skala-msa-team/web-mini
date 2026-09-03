<template>
  <div class="app-shell">
    <AppHeader />
    <RouterView />
    <AppFooter />
  </div>
</template>

<script setup>
import AppFooter from '@/components/layout/AppFooter.vue'
import AppHeader from '@/components/layout/AppHeader.vue'
</script>
