<script setup>
import { Bell, Scale, Search } from '@lucide/vue'
</script>

<template>
  <header class="app-header">
    <div class="app-header__inner">
      <RouterLink class="app-brand" to="/">
        <Scale :size="22" aria-hidden="true" />
        <span>사랑과 전쟁</span>
      </RouterLink>

      <nav class="app-nav" aria-label="주요 메뉴">
        <RouterLink class="app-nav__active" to="/">홈</RouterLink>
        <a href="/#popular-posts">인기게시글</a>
        <a href="/#live-trials">LIVE 재판</a>
        <a href="/#community-guidelines">커뮤니티 가이드라인</a>
        <a href="#">마이페이지</a>
      </nav>

      <div class="app-header__actions">
        <button class="icon-button" type="button" aria-label="검색"><Search :size="19" /></button>
        <button class="icon-button" type="button" aria-label="알림"><Bell :size="19" /></button>
        <span class="user-avatar" aria-label="로그인 사용자">U</span>
      </div>
    </div>
  </header>
</template>
