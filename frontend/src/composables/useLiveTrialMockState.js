import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { CONNECTION_STATUS } from '@/consts/liveTrialUiStatus.js'
import { getLiveTrialStateMock } from '@/mock/trial/liveTrialStateMock.js'

export function useLiveTrialMockState(defaultScenario) {
  const route = useRoute()
  const router = useRouter()

  const scenario = computed(() => {
    const queryValue = route.query.mockState
    return Array.isArray(queryValue) ? queryValue[0] : queryValue
  })

  const state = computed(() => getLiveTrialStateMock(scenario.value || defaultScenario))
  const interactionsDisabled = computed(
    () =>
      state.value.connection.status !== CONNECTION_STATUS.CONNECTED ||
      !state.value.snapshot ||
      state.value.snapshot.ended,
  )
  const interactionDisabledMessage = computed(() => {
    if (state.value.snapshot?.ended) return '종료된 재판입니다.'

    switch (state.value.connection.status) {
      case CONNECTION_STATUS.CONNECTING:
        return '재판에 연결 중입니다.'
      case CONNECTION_STATUS.RECONNECTING:
        return '연결을 복구하는 중입니다.'
      case CONNECTION_STATUS.ERROR:
        return '연결 오류로 사용할 수 없습니다.'
      default:
        return ''
    }
  })

  function retryConnection() {
    const query = { ...route.query }
    delete query.mockState

    return router.replace({
      name: route.name,
      params: route.params,
      query,
    })
  }

  return {
    state,
    interactionsDisabled,
    interactionDisabledMessage,
    retryConnection,
  }
}
