<script setup>
import { Scale } from '@lucide/vue'
</script>

<template>
  <footer class="app-footer">
    <div class="footer-inner">
      <RouterLink class="footer-brand" :to="{ name: 'home' }">
        <Scale :size="19" />
        <span>사랑과 전쟁터</span>
      </RouterLink>
      <nav aria-label="하단 메뉴">
        <a href="#terms">이용약관</a>
        <a href="#privacy">개인정보처리방침</a>
        <a href="#help">고객센터</a>
      </nav>
      <p>© 2026 사랑과 전쟁터. All rights reserved.</p>
    </div>
  </footer>
</template>

<style scoped>
.app-footer {
  border-top: 1px solid var(--ds-color-card-border);
  background: white;
}

.footer-inner {
  width: min(calc(100% - 32px), var(--ds-container-max));
  min-height: 76px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  gap: 24px;
  color: var(--ds-color-on-surface-variant);
  font-size: 0.75rem;
}

.footer-brand {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  width: fit-content;
  color: var(--ds-color-verdict-blue);
  font-weight: 700;
  text-decoration: none;
}

nav {
  display: flex;
  gap: 20px;
}

nav a {
  color: inherit;
  text-decoration: none;
}

p {
  justify-self: end;
  margin: 0;
}

@media (max-width: 760px) {
  .footer-inner {
    grid-template-columns: 1fr;
    justify-items: center;
    padding: 24px 0;
    gap: 14px;
    text-align: center;
  }

  .footer-brand,
  p {
    justify-self: center;
  }
}
</style>
