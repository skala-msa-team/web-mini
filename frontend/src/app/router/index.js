import { createRouter, createWebHistory } from 'vue-router'
import TrialPreparationPage from '@/pages/trial-preparation/TrialPreparationPage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/trials/new',
    },
    {
      path: '/trials/new',
      name: 'trial-preparation',
      component: TrialPreparationPage,
    },
  ],
})

export default router
