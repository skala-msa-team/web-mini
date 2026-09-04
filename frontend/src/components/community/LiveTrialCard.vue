<script setup>
import Badge from '@/components/ui/Badge.vue'
import Button from '@/components/ui/Button.vue'

const props = defineProps({
  trial: {
    type: Object,
    required: true,
  },
  isMock: {
    type: Boolean,
    default: false,
  },
});

const actionLabel = props.isMock ? '준비 중' : '참여하기'
</script>

<template>
  <article class="live-trial-card" :class="{ 'live-trial-card--mock': isMock }">
    <div class="mb-2 flex items-center justify-between">
      <Badge :variant="isMock ? 'primary' : 'live'">● LIVE</Badge>
      <span v-if="isMock" class="text-xs font-semibold text-muted-foreground">데모</span>
    </div>
    <h3 class="mt-3 min-h-12 text-base font-bold leading-6">{{ trial.title }}</h3>
    <p class="mt-2 text-sm text-muted-foreground">{{ trial.statusLabel || '공개 재판 진행 중' }}</p>
    <p v-if="trial.viewerCount" class="mt-2 text-xs text-muted-foreground">참여자 {{ trial.viewerCount }}명</p>
    <template v-if="isMock">
      <Button class="mt-5 w-full" disabled>
        {{ actionLabel }}
      </Button>
      <p class="mt-2 text-[11px] text-muted-foreground">데모 재판은 화면 표시 전용입니다.</p>
    </template>
    <RouterLink v-else class="mt-5 block" :to="{ name: 'live-trial', params: { trialId: trial.id } }">
      <Button as="span" class="w-full">{{ actionLabel }}</Button>
    </RouterLink>
  </article>
</template>

<style scoped>
.live-trial-card {
  border: 1px solid var(--ds-color-border, #d7e2f2);
  border-radius: var(--ds-radius-md);
  background: var(--ds-color-card, #fff);
  padding: 1.25rem;
  box-shadow: var(--ds-shadow, 0 8px 22px rgb(16 45 97 / 9%));
}

.live-trial-card--mock {
  opacity: 0.94;
}
</style>
