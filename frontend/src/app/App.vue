<script setup>
import AppFooter from '@/components/common/AppFooter.vue'
import AppHeader from '@/components/common/AppHeader.vue'
</script>

<template>
  <div class="app-shell">
    <AppHeader />
    <RouterView />
    <AppFooter />
  </div>
</template>
