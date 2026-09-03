<script setup>
import Badge from '@/components/ui/Badge.vue'
import Button from '@/components/ui/Button.vue'

defineProps({
  trial: {
    type: Object,
    required: true,
  },
});
</script>

<template>
  <article class="rounded-xl border border-border bg-card p-5 shadow-sm">
    <Badge variant="live">● LIVE</Badge>
    <h3 class="mt-3 min-h-12 text-base font-bold leading-6">{{ trial.title }}</h3>
    <p class="mt-2 text-sm text-muted-foreground">{{ trial.statusLabel || '공개 재판 진행 중' }}</p>
    <RouterLink
      class="mt-5 block"
      :to="{ name: 'live-trial', params: { trialId: trial.id } }"
    >
      <Button as="span" class="w-full">참여하기</Button>
    </RouterLink>
  </article>
</template>
