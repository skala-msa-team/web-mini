<script setup>
import { SearchX } from '@lucide/vue'

defineEmits(['reset'])
</script>

<template>
  <section class="empty-posts">
    <SearchX :size="36" aria-hidden="true" />
    <h2>검색 결과가 없어요</h2>
    <p>다른 검색어나 필터를 선택해 보세요.</p>
    <button class="button button--outline" type="button" @click="$emit('reset')">필터 초기화</button>
  </section>
</template>
