import { computed, onBeforeUnmount, onMounted, ref, toValue } from 'vue'

const TIMER_INTERVAL_MS = 250

export function useTrialCountdown(endsAt) {
  const now = ref(Date.now())
  let timerId = null

  const endTimestamp = computed(() => {
    const value = toValue(endsAt)
    if (!value) return null

    const timestamp = new Date(value).getTime()
    return Number.isNaN(timestamp) ? null : timestamp
  })
  const remainingSeconds = computed(() => {
    if (endTimestamp.value === null) return null
    return Math.max(0, Math.ceil((endTimestamp.value - now.value) / 1000))
  })
  const formattedRemainingTime = computed(() => {
    if (remainingSeconds.value === null) return '--:--'

    const minutes = Math.floor(remainingSeconds.value / 60)
    const seconds = remainingSeconds.value % 60
    return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
  })
  const isExpired = computed(
    () => remainingSeconds.value !== null && remainingSeconds.value === 0,
  )

  onMounted(() => {
    now.value = Date.now()
    timerId = window.setInterval(() => {
      now.value = Date.now()
    }, TIMER_INTERVAL_MS)
  })

  onBeforeUnmount(() => {
    if (timerId !== null) window.clearInterval(timerId)
  })

  return {
    remainingSeconds,
    formattedRemainingTime,
    isExpired,
  }
}
