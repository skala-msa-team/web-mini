<script setup>
import { MessageCircle, ThumbsUp } from '@lucide/vue'

defineProps({
  post: {
    type: Object,
    required: true,
  },
})
</script>

<template>
  <article class="post-card">
    <div class="post-card__meta">
      <span class="category-badge">{{ post.category }}</span>
      <span v-if="post.isLive" class="live-badge">● LIVE</span>
      <time>{{ post.createdAt }}</time>
    </div>
    <RouterLink
      class="post-card__title"
      :to="{ name: 'post-detail', params: { postId: post.id } }"
    >
      {{ post.title }}
    </RouterLink>
    <p class="post-card__summary">{{ post.summary }}</p>
    <div class="post-card__stats">
      <span><ThumbsUp :size="15" /> {{ post.likeCount }}</span>
      <span><MessageCircle :size="15" /> {{ post.commentCount }}</span>
    </div>
  </article>
</template>
