import { createRouter, createWebHistory } from 'vue-router'
import CommunityPage from '@/pages/community/CommunityPage.vue'
import PostCreatePage from '@/pages/community/PostCreatePage.vue'
import PostDetailPage from '@/pages/community/PostDetailPage.vue'
import LiveTrialEntryPage from '@/pages/live-trial/LiveTrialEntryPage.vue'
import TrialVotingPage from '@/pages/live-trial/TrialVotingPage.vue'
import TrialPreparationPage from '@/pages/trial-preparation/TrialPreparationPage.vue'
import TrialResultPage from '@/pages/trial-result/TrialResultPage.vue'
import IntegrationSpikePage from '@/pages/integration/IntegrationSpikePage.vue'

const DEFAULT_DEMO_TRIAL_ID = '1'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'home', component: CommunityPage },
    { path: '/community', redirect: '/' },
    { path: '/community/posts/new', name: 'post-create', component: PostCreatePage },
    { path: '/community/posts/:postId', name: 'post-detail', component: PostDetailPage },
    { path: '/trials/new', name: 'trial-preparation', component: TrialPreparationPage },
    { path: '/integration-spike', name: 'integration-spike', component: IntegrationSpikePage },

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
