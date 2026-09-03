<script setup>
defineProps({
  trial: {
    type: Object,
    required: true,
  },
})
</script>

<template>
  <article class="live-trial-card">
    <span class="live-badge">● LIVE</span>
    <h3>{{ trial.title }}</h3>
    <p>시청자 {{ trial.viewerCount }}명</p>
    <button class="button button--primary" type="button">참여하기</button>
  </article>
</template>
