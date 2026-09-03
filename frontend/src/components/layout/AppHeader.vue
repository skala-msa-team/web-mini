<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { Bell, Gavel, UserRound } from '@lucide/vue'

const route = useRoute()
const trialId = computed(() => route.params.trialId ?? '1')

const navigation = computed(() => [
  { label: '홈', href: '#home' },
  { label: '인기게시글', href: '#popular-posts' },
  {
    label: 'Live 재판',
    to: { name: 'live-trial', params: { trialId: trialId.value } },
    active: true,
  },
  { label: '커뮤니티 가이드라인', href: '#community-guide' },
  { label: '마이페이지', href: '#my-page' },
])
</script>

<template>
  <header class="app-header">
    <div class="header-inner">
      <RouterLink
        class="brand"
        :to="{ name: 'live-trial', params: { trialId } }"
        aria-label="사랑과 전쟁터 홈"
      >
        <span class="brand-mark"><Gavel :size="21" stroke-width="2.4" /></span>
        <span>사랑과 전쟁터</span>
      </RouterLink>

      <nav class="primary-nav" aria-label="주 메뉴">
        <component
          v-for="item in navigation"
          :key="item.label"
          :is="item.to ? 'RouterLink' : 'a'"
          :to="item.to"
          :href="item.href"
          class="nav-link"
          :class="{ active: item.active }"
          :aria-current="item.active ? 'page' : undefined"
        >
          {{ item.label }}
        </component>
      </nav>

      <div class="header-actions" aria-label="사용자 메뉴">
        <button class="icon-button" type="button" aria-label="알림">
          <Bell :size="20" />
          <span class="notification-dot" aria-hidden="true"></span>
        </button>
        <button class="profile-button" type="button">
          <UserRound :size="17" />
          <span>내 정보</span>
        </button>
      </div>
    </div>
  </header>
</template>

<style scoped>
.app-header {
  border-bottom: 1px solid var(--ds-color-card-border);
  background: rgb(255 255 255 / 92%);
  backdrop-filter: blur(12px);
}

.header-inner {
  width: min(calc(100% - 32px), var(--ds-container-max));
  min-height: 72px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: minmax(190px, 1fr) auto minmax(190px, 1fr);
  align-items: center;
  gap: 24px;
}

.brand {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  width: fit-content;
  color: var(--ds-color-primary);
  font-family: var(--ds-font-heading);
  font-size: 1.15rem;
  font-weight: 700;
  text-decoration: none;
}

.brand-mark {
  display: grid;
  place-items: center;
  width: 34px;
  height: 34px;
  border-radius: 10px;
  background: var(--ds-color-primary);
  color: white;
}

.primary-nav {
  display: flex;
  align-items: stretch;
  gap: 8px;
  align-self: stretch;
}

.nav-link {
  position: relative;
  display: inline-flex;
  align-items: center;
  padding: 0 12px;
  color: var(--ds-color-on-surface-variant);
  font-size: 0.875rem;
  font-weight: 600;
  text-decoration: none;
  white-space: nowrap;
}

.nav-link::after {
  content: '';
  position: absolute;
  right: 12px;
  bottom: 0;
  left: 12px;
  height: 3px;
  border-radius: 999px 999px 0 0;
  background: transparent;
}

.nav-link:hover,
.nav-link.active {
  color: var(--ds-color-justice-blue);
}

.nav-link.active::after {
  background: var(--ds-color-justice-blue);
}

.header-actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 10px;
}

.icon-button,
.profile-button {
  border: 0;
  cursor: pointer;
}

.icon-button {
  position: relative;
  display: grid;
  place-items: center;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--ds-color-surface-container-low);
  color: var(--ds-color-primary);
}

.notification-dot {
  position: absolute;
  top: 9px;
  right: 9px;
  width: 6px;
  height: 6px;
  border: 1.5px solid white;
  border-radius: 50%;
  background: var(--ds-color-accent-red);
}

.profile-button {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  min-height: 40px;
  padding: 0 16px;
  border-radius: var(--ds-radius-default);
  background: var(--ds-color-primary);
  color: white;
  font-size: 0.84rem;
  font-weight: 600;
}

@media (max-width: 960px) {
  .header-inner {
    grid-template-columns: 1fr auto;
    padding: 12px 0 0;
  }

  .primary-nav {
    grid-column: 1 / -1;
    grid-row: 2;
    overflow-x: auto;
    justify-content: flex-start;
    min-height: 44px;
    margin: 0 -16px;
    padding: 0 16px;
  }
}

@media (max-width: 560px) {
  .header-inner {
    width: min(calc(100% - 24px), var(--ds-container-max));
  }

  .profile-button {
    width: 40px;
    padding: 0;
    justify-content: center;
  }

  .profile-button span {
    position: absolute;
    width: 1px;
    height: 1px;
    overflow: hidden;
    clip: rect(0 0 0 0);
  }
}
</style>
