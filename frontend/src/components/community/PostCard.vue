<script setup>
import { MessageCircle, ThumbsUp } from '@lucide/vue'
import Badge from '@/components/ui/Badge.vue'

defineProps({
  post: {
    type: Object,
    required: true,
  },
})
</script>

<template>
  <article class="rounded-xl border border-border bg-card p-5 shadow-sm transition hover:-translate-y-0.5 hover:shadow-md">
    <div class="flex items-center gap-2 text-xs text-muted-foreground">
      <Badge variant="primary">{{ post.category }}</Badge>
      <Badge v-if="post.isLive" variant="live">● LIVE</Badge>
      <time class="ml-auto">{{ post.createdAt }}</time>
    </div>
    <RouterLink
      class="mt-3 block text-lg font-bold tracking-tight text-foreground transition hover:text-primary"
      :to="{ name: 'post-detail', params: { postId: post.id } }"
    >
      {{ post.title }}
    </RouterLink>
    <p class="mt-2 text-sm leading-6 text-muted-foreground">{{ post.summary }}</p>
    <div class="mt-4 flex gap-4 text-xs text-muted-foreground">
      <span class="inline-flex items-center gap-1"><ThumbsUp :size="15" /> {{ post.likeCount }}</span>
      <span class="inline-flex items-center gap-1"><MessageCircle :size="15" /> {{ post.commentCount }}</span>
    </div>
  </article>
</template>
