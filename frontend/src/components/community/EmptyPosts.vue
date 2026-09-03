<script setup>
import { SearchX } from "@lucide/vue";
import Button from '@/components/ui/Button.vue'

defineEmits(["reset"]);
</script>

<template>
  <section class="rounded-xl border border-dashed border-border bg-card px-5 py-16 text-center text-muted-foreground">
    <SearchX class="mx-auto" :size="36" aria-hidden="true" />
    <h2 class="mt-4 text-lg font-bold text-foreground">검색 결과가 없어요</h2>
    <p class="mt-2 text-sm">다른 검색어나 필터를 선택해 보세요.</p>
    <button
      class="mt-5"
      type="button"
      @click="$emit('reset')"
    >
      <Button variant="outline">필터 초기화</Button>
    </button>
  </section>
</template>
