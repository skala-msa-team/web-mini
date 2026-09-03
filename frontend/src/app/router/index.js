import { createRouter, createWebHistory } from 'vue-router'

import CommunityPage from '@/pages/community/CommunityPage.vue'
import PostCreatePage from '@/pages/community/PostCreatePage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'home', component: CommunityPage },
    { path: '/community', redirect: '/' },
    { path: '/community/posts/new', name: 'post-create', component: PostCreatePage },
  ],
})

export default router
