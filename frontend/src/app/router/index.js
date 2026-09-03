import { createRouter, createWebHistory } from 'vue-router'
import LiveTrialEntryPage from '@/pages/live-trial/LiveTrialEntryPage.vue'
import TrialVotingPage from '@/pages/live-trial/TrialVotingPage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/live-trial',
    },
    {
      path: '/live-trial',
      name: 'live-trial',
      component: LiveTrialEntryPage,
    },
    {
      path: '/live-trial/vote',
      name: 'trial-voting',
      component: TrialVotingPage,
    },
  ],
})

export default router
