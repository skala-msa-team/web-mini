<script setup>
defineProps({
  trial: {
    type: Object,
    required: true,
  },
});
</script>

<template>
  <article class="live-trial-card">
    <span class="live-badge">● LIVE</span>
    <h3>{{ trial.title }}</h3>
    <p>{{ trial.statusLabel || '공개 재판 진행 중' }}</p>
    <RouterLink
      class="button button--primary"
      :to="{ name: 'live-trial', params: { trialId: trial.id } }"
    >
      참여하기
    </RouterLink>
  </article>
</template>
