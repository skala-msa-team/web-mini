<script setup>
import { Scale } from '@lucide/vue'
</script>

<template>
  <footer class="border-t border-border bg-card">
    <div class="mx-auto grid min-h-[76px] w-[min(calc(100%-2rem),var(--ds-container-max))] grid-cols-3 items-center gap-6 text-sm text-muted-foreground max-[760px]:flex max-[760px]:flex-col max-[760px]:justify-center max-[760px]:gap-3 max-[760px]:py-6 max-[760px]:text-center">
      <RouterLink class="inline-flex w-fit items-center gap-2 font-semibold text-[var(--ds-color-verdict-blue)]" :to="{ name: 'home' }">
        <Scale :size="19" />
        <span>사랑과 전쟁터</span>
      </RouterLink>
      <nav class="flex gap-5 max-[760px]:flex-wrap max-[760px]:justify-center" aria-label="하단 메뉴">
        <a href="#terms">이용약관</a>
        <a href="#privacy">개인정보처리방침</a>
        <a href="#help">고객센터</a>
      </nav>
      <p class="m-0 justify-self-end max-[760px]:self-center">© 2026 사랑과 전쟁터. All rights reserved.</p>
    </div>
  </footer>
</template>
