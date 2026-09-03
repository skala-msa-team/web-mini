import { createRouter, createWebHistory } from 'vue-router'
import LiveTrialEntryPage from '@/pages/live-trial/LiveTrialEntryPage.vue'
import TrialVotingPage from '@/pages/live-trial/TrialVotingPage.vue'
import TrialResultPage from '@/pages/trial-result/TrialResultPage.vue'

const DEFAULT_DEMO_TRIAL_ID = '1'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: {
        name: 'live-trial',
        params: { trialId: DEFAULT_DEMO_TRIAL_ID },
      },
    },
    {
      path: '/live-trial',
      redirect: {
        name: 'live-trial',
        params: { trialId: DEFAULT_DEMO_TRIAL_ID },
      },
    },
    {
      path: '/live-trial/:trialId',
      name: 'live-trial',
      component: LiveTrialEntryPage,
    },
    {
      path: '/live-trial/:trialId/vote',
      name: 'trial-voting',
      component: TrialVotingPage,
    },
    {
      path: '/live-trial/:trialId/result',
      name: 'trial-result',
      component: TrialResultPage,
    },
  ],
})

export default router
