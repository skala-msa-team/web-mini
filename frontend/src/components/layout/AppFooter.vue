<template>
  <footer class="app-footer">
    <div class="app-footer__inner">
      <RouterLink class="footer-brand" to="/"><Scale :size="20" aria-hidden="true" /><strong>사랑과 전쟁</strong></RouterLink>
      <nav aria-label="푸터 메뉴">
        <a href="#">이용약관</a>
        <a href="#">개인정보처리방침</a>
        <a href="#">고객센터</a>
      </nav>
      <p>© 2026 사랑과 전쟁 All rights reserved.</p>
    </div>
  </footer>
</template>

<script setup>
import { Scale } from '@lucide/vue'
</script>
