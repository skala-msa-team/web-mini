<script setup>
import { computed, nextTick, ref } from 'vue'
import { MessageSquare, Send } from '@lucide/vue'

const props = defineProps({
  initialMessages: { type: Array, required: true },
  audienceCount: { type: Number, required: true },
  headerLabel: { type: String, default: '' },
  disabled: { type: Boolean, default: false },
  disabledMessage: { type: String, default: '현재 채팅을 사용할 수 없습니다.' },
})

const messages = ref([...props.initialMessages])
const draft = ref('')
const messageList = ref(null)
const formattedAudienceCount = computed(() => props.audienceCount.toLocaleString('ko-KR'))
const displayedHeaderLabel = computed(() => props.headerLabel || `${formattedAudienceCount.value}명`)

async function submitMessage() {
  if (props.disabled) return

  const message = draft.value.trim()
  if (!message) return

  messages.value.push({
    id: Date.now(),
    avatar: '나',
    nickname: '나의 의견',
    message,
    tone: 'navy',
  })
  draft.value = ''
  await nextTick()
  messageList.value?.lastElementChild?.scrollIntoView({ behavior: 'smooth' })
}
</script>

<template>
  <aside class="chat-panel" aria-labelledby="chat-title">
    <header>
      <h2 id="chat-title"><MessageSquare :size="21" /> 배심원 채팅</h2>
      <span><i aria-hidden="true"></i>{{ displayedHeaderLabel }}</span>
    </header>

    <div ref="messageList" class="message-list" aria-live="polite">
      <article v-for="message in messages" :key="message.id" class="message-item">
        <div class="avatar" :class="`tone-${message.tone}`">{{ message.avatar }}</div>
        <div>
          <small>
            <b>{{ message.nickname }}</b>
            <span v-if="message.badge" class="message-badge">{{ message.badge }}</span>
          </small>
          <p>{{ message.message }}</p>
        </div>
      </article>
    </div>

    <form class="chat-form" @submit.prevent="submitMessage">
      <label class="sr-only" for="chat-message">배심원 채팅 의견</label>
      <input
        id="chat-message"
        v-model="draft"
        type="text"
        :placeholder="disabled ? disabledMessage : '의견을 남겨주세요...'"
        :disabled="disabled"
      />
      <button type="submit" aria-label="메시지 보내기" :disabled="disabled || !draft.trim()">
        <Send :size="18" />
      </button>
    </form>
  </aside>
</template>

<style scoped>
.chat-panel {
  min-height: 100%;
  display: grid;
  grid-template-rows: auto 1fr auto;
  border: 1px solid var(--ds-color-outline-variant);
  border-radius: var(--ds-radius-md);
  background: white;
  overflow: hidden;
}

header {
  min-height: 58px;
  padding: 0 18px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  border-bottom: 1px solid var(--ds-color-outline-variant);
  background: var(--ds-color-surface-container-low);
}

h2 {
  margin: 0;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: var(--ds-color-primary);
  font-size: 1rem;
}

header > span {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: var(--ds-color-on-surface-variant);
  font-size: 0.75rem;
  font-weight: 700;
}

header i {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: var(--ds-color-justice-blue);
}

.message-list {
  min-height: 450px;
  max-height: 750px;
  padding: 20px 16px;
  overflow-y: auto;
}

.message-item {
  display: grid;
  grid-template-columns: 30px 1fr;
  gap: 9px;
  margin-bottom: 18px;
}

.avatar {
  display: grid;
  place-items: center;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: #e5edfa;
  color: #38608d;
  font-size: 0.69rem;
}

.avatar.tone-violet {
  background: #e9e8ff;
  color: #5751bc;
}

.avatar.tone-coral {
  background: #ffe3df;
  color: #b75a55;
}

.avatar.tone-sky {
  background: #dceafb;
  color: #3a638c;
}

.avatar.tone-navy {
  background: var(--ds-color-primary);
  color: white;
}

.message-item small {
  display: block;
  margin: 0 0 4px;
  color: #8c95a2;
  font-size: 0.65rem;
}

.message-item small b {
  color: var(--ds-color-on-surface-variant);
  font-weight: 700;
}

.message-badge {
  margin-left: 6px;
  color: var(--ds-color-justice-blue);
}

.message-item p {
  width: fit-content;
  max-width: 100%;
  margin: 0;
  padding: 9px 11px;
  border-radius: 0 10px 10px 10px;
  background: #f7f9fc;
  color: var(--ds-color-on-surface);
  font-size: 0.78rem;
  line-height: 1.45;
}

.chat-form {
  padding: 12px;
  display: grid;
  grid-template-columns: 1fr 40px;
  gap: 8px;
  border-top: 1px solid var(--ds-color-outline-variant);
  background: white;
}

.chat-form input {
  width: 100%;
  min-width: 0;
  padding: 0 13px;
  border: 1px solid var(--ds-color-outline-variant);
  border-radius: var(--ds-radius-default);
  background: white;
  color: var(--ds-color-on-surface);
  font-size: 0.77rem;
  outline: none;
}

.chat-form input:focus {
  border-color: var(--ds-color-justice-blue);
  box-shadow: 0 0 0 3px rgb(37 99 235 / 12%);
}

.chat-form input:disabled {
  background: var(--ds-color-surface-container-low);
  color: var(--ds-color-on-surface-variant);
  cursor: not-allowed;
}

.chat-form button {
  display: grid;
  place-items: center;
  width: 40px;
  height: 40px;
  border: 0;
  border-radius: var(--ds-radius-default);
  background: var(--ds-color-primary);
  color: white;
  cursor: pointer;
}

.chat-form button:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}

@media (max-width: 960px) {
  .message-list {
    min-height: 320px;
    max-height: 420px;
  }
}
</style>
