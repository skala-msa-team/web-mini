<script setup>
import { Bell, Scale, Search, UserRound } from '@lucide/vue'
</script>

<template>
  <header class="border-b border-border bg-card">
    <div class="mx-auto flex h-16 max-w-[var(--ds-container-max)] items-center justify-between px-4 sm:px-6">
      <a class="flex items-center gap-2 font-heading text-xl font-bold text-[var(--ds-color-primary)]" href="#">
        <Scale class="size-6 text-primary" aria-hidden="true" />
        사랑과 전쟁터
      </a>

      <nav class="hidden items-center gap-8 text-sm font-medium text-foreground md:flex" aria-label="주요 메뉴">
        <a href="#">재판소</a>
        <a href="#">명예의 전당</a>
        <a href="#">커뮤니티</a>
      </nav>

      <div class="flex items-center gap-4 text-[var(--ds-color-primary)]">
        <button class="rounded-full p-1.5 hover:bg-muted" type="button" aria-label="검색">
          <Search class="size-5" />
        </button>
        <button class="rounded-full p-1.5 hover:bg-muted" type="button" aria-label="알림">
          <Bell class="size-5" />
        </button>
        <span class="grid size-8 place-items-center rounded-full bg-primary text-primary-foreground" aria-label="사용자">
          <UserRound class="size-4" />
        </span>
      </div>
    </div>
  </header>
</template>
