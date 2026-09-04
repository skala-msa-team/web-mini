<script setup>
import { onBeforeUnmount, ref } from 'vue'
import { getTrials } from '@/apis/trialApi.js'
import { useDemoUser } from '@/composables/useDemoUser.js'
import { createStompClient, readMessage, STOMP_DESTINATION } from '@/lib/realtime.js'

const trialId = ref('1')
const restState = ref('대기 중')
const restResponse = ref(null)
const restError = ref('')
const stompState = ref('DISCONNECTED')
const receivedMessages = ref([])
let stompClient
let unsubscribeChat
let unsubscribeStatus

async function callRest() {
  restState.value = '호출 중'
  restError.value = ''
  try {
    restResponse.value = await getTrials()
    restState.value = '성공'
  } catch (error) {
    restState.value = '실패'
    restError.value = error.response?.data?.message ?? error.message
  }
}

function connectStomp() {
  if (stompClient?.active) return

  const { getDemoUserId } = useDemoUser()
  stompClient = createStompClient({ userId: getDemoUserId() })
  unsubscribeStatus = stompClient.onStatusChange((status) => {
    stompState.value = status
  })
  unsubscribeChat = stompClient.subscribe(
    STOMP_DESTINATION.trialChat(trialId.value),
    (frame) => receivedMessages.value.unshift(readMessage(frame)),
  )
  stompClient.activate()
}

function sendTestEvent() {
  stompClient?.publish(STOMP_DESTINATION.sendChat(trialId.value), {
    content: 'Integration spike test event',
  })
}

async function disconnectStomp() {
  unsubscribeChat?.()
  unsubscribeStatus?.()
  await stompClient?.deactivate()
  stompClient = undefined
  stompState.value = 'DISCONNECTED'
}

onBeforeUnmount(disconnectStomp)
</script>

<template>
  <main class="integration-spike">
    <header>
      <p class="eyebrow">INTEGRATION SPIKE · #208</p>
      <h1>연결 경로 검증</h1>
      <p>REST 응답과 저장 후 브로드캐스트되는 STOMP 메시지를 한 화면에서 확인합니다.</p>
    </header>

    <section class="card">
      <div class="section-heading">
        <div>
          <p class="eyebrow">01 · REST</p>
          <h2>Backend 재판 목록 조회</h2>
        </div>
        <button type="button" @click="callRest">Axios 호출</button>
      </div>
      <p class="state">상태: {{ restState }}</p>
      <p v-if="restError" class="error">{{ restError }}</p>
      <pre v-if="restResponse">{{ JSON.stringify(restResponse, null, 2) }}</pre>
    </section>

    <section class="card">
      <div class="section-heading">
        <div>
          <p class="eyebrow">02 · STOMP</p>
          <h2>두 브라우저 동기화 테스트</h2>
        </div>
        <span class="state">{{ stompState }}</span>
      </div>
      <label>
        Trial ID
        <input v-model="trialId" inputmode="numeric" :disabled="stompClient?.active" />
      </label>
      <div class="actions">
        <button type="button" :disabled="stompClient?.active" @click="connectStomp">CONNECT · SUBSCRIBE</button>
        <button type="button" :disabled="!stompClient?.connected" @click="sendTestEvent">저장 후 이벤트 전송</button>
        <button type="button" :disabled="!stompClient?.active" @click="disconnectStomp">DISCONNECT</button>
      </div>
      <ul v-if="receivedMessages.length" class="messages">
        <li v-for="(message, index) in receivedMessages" :key="index">
          {{ message.content }} · sequence {{ message.messageSequence }}
        </li>
      </ul>
      <p v-else class="muted">아직 수신한 메시지가 없습니다.</p>
    </section>
  </main>
</template>

<style scoped>
.integration-spike { max-width: 960px; margin: 0 auto; padding: 72px 24px 120px; }
header { margin-bottom: 32px; }
.eyebrow { color: var(--ds-color-justice-blue); font-size: 12px; font-weight: 700; letter-spacing: .12em; }
h1 { margin: 8px 0; font-size: clamp(2rem, 5vw, 3.5rem); }
h2 { margin: 4px 0 0; font-size: 1.35rem; }
.card { margin-top: 20px; padding: 24px; border: 1px solid var(--ds-color-card-border); border-radius: 20px; background: var(--ds-color-surface-container-lowest); box-shadow: var(--ds-shadow-interactive); }
.section-heading { display: flex; align-items: start; justify-content: space-between; gap: 16px; }
button { border: 0; border-radius: 999px; padding: 10px 16px; color: white; background: var(--ds-color-justice-blue); cursor: pointer; }
button:disabled { cursor: not-allowed; opacity: .45; }
.state, .muted { color: var(--ds-color-on-surface-variant); }
.error { color: var(--ds-color-error); }
pre { overflow: auto; margin: 16px 0 0; padding: 16px; border-radius: 12px; background: #101827; color: #dce8f7; }
label { display: grid; gap: 6px; margin-top: 20px; font-weight: 600; }
input { max-width: 180px; padding: 10px 12px; border: 1px solid var(--ds-color-card-border); border-radius: 10px; }
.actions { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 16px; }
.messages { display: grid; gap: 8px; padding-left: 20px; }
@media (max-width: 600px) { .integration-spike { padding: 40px 16px 80px; } .section-heading { display: grid; } }
</style>
