<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { Bell, Scale, UserRound } from '@lucide/vue'

const route = useRoute()
const trialId = computed(() => route.params.trialId ?? '1')
const liveTrialRouteNames = ['live-trial', 'trial-voting', 'trial-result']
const communityRouteNames = ['home', 'post-create', 'post-detail']

const navigation = computed(() => [
  {
    label: '홈',
    to: { name: 'home' },
    active: communityRouteNames.includes(route.name) && !route.hash,
  },
  {
    label: '인기게시글',
    to: { name: 'home', hash: '#popular-posts' },
    active: route.name === 'home' && route.hash === '#popular-posts',
  },
  {
    label: 'Live 재판',
    to: { name: 'live-trial', params: { trialId: trialId.value } },
    active: liveTrialRouteNames.includes(route.name),
  },
  {
    label: '커뮤니티 가이드라인',
    to: { name: 'home', hash: '#community-guidelines' },
    active: route.name === 'home' && route.hash === '#community-guidelines',
  },
  { label: '마이페이지', href: '#my-page' },
])
</script>

<template>
  <header class="sticky top-0 z-20 border-b border-border bg-white/90 backdrop-blur-xl">
    <div class="mx-auto grid min-h-[72px] w-[min(calc(100%-2rem),var(--ds-container-max))] grid-cols-[minmax(190px,1fr)_auto_minmax(190px,1fr)] items-center gap-6 max-[960px]:grid-cols-[1fr_auto] max-[960px]:gap-3 max-[960px]:py-3">
      <RouterLink
        class="inline-flex w-fit items-center gap-2.5 font-heading text-xl font-bold text-[var(--ds-color-verdict-blue)]"
        :to="{ name: 'home' }"
        aria-label="사랑과 전쟁터 홈"
      >
        <Scale :size="24" stroke-width="2.4" />
        <span>사랑과 전쟁터</span>
      </RouterLink>

      <nav class="flex h-full items-stretch gap-2 max-[960px]:col-span-2 max-[960px]:row-start-2 max-[960px]:-mx-4 max-[960px]:min-h-11 max-[960px]:overflow-x-auto max-[960px]:px-4" aria-label="주 메뉴">
        <component
          v-for="item in navigation"
          :key="item.label"
          :is="item.to ? 'RouterLink' : 'a'"
          :to="item.to"
          :href="item.href"
          class="relative inline-flex items-center whitespace-nowrap px-3 text-sm font-semibold text-muted-foreground transition hover:text-primary"
          :class="item.active ? 'text-primary after:absolute after:inset-x-3 after:bottom-0 after:h-0.5 after:rounded-t-full after:bg-primary' : ''"
          :aria-current="item.active ? 'page' : undefined"
        >
          {{ item.label }}
        </component>
      </nav>

      <div class="flex items-center justify-end gap-2.5" aria-label="사용자 메뉴">
        <button class="relative grid size-10 place-items-center rounded-full border-0 bg-muted text-primary transition hover:bg-accent" type="button" aria-label="알림">
          <Bell :size="20" />
          <span class="absolute right-2 top-2 size-1.5 rounded-full border border-white bg-red-400" aria-hidden="true"></span>
        </button>
        <button class="inline-flex min-h-10 items-center gap-2 rounded-lg border-0 bg-primary px-4 text-sm font-semibold text-primary-foreground transition hover:bg-primary/90 max-[560px]:size-10 max-[560px]:justify-center max-[560px]:px-0" type="button">
          <UserRound :size="17" />
          <span class="max-[560px]:sr-only">내 정보</span>
        </button>
      </div>
    </div>
  </header>
</template>
