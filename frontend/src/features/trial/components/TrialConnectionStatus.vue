<script setup>
import { computed } from 'vue'
import { CircleAlert, Gavel, LoaderCircle, RefreshCw } from '@lucide/vue'
import { CONNECTION_STATUS } from '@/constants/liveTrialUiStatus.js'

const props = defineProps({
  connection: { type: Object, required: true },
  ended: { type: Boolean, default: false },
})

defineEmits(['retry'])

const presentation = computed(() => {
  if (props.ended) {
    return {
      visible: true,
      tone: 'ended',
      title: '재판이 종료되었습니다.',
      description: '채팅과 투표가 마감되었습니다. 최종 판결 결과를 확인해 주세요.',
      retryable: false,
    }
  }

  switch (props.connection.status) {
    case CONNECTION_STATUS.CONNECTING:
      return {
        visible: true,
        tone: 'loading',
        title: 'Live 재판에 연결하고 있습니다.',
        description: '현재 재판 상태와 이전 진행 내역을 불러오는 중입니다.',
        retryable: false,
      }
    case CONNECTION_STATUS.RECONNECTING:
      return {
        visible: true,
        tone: 'warning',
        title: '재판 연결을 복구하고 있습니다.',
        description: '마지막으로 확인한 내용은 유지됩니다. 잠시만 기다려 주세요.',
        retryable: false,
      }
    case CONNECTION_STATUS.ERROR:
      return {
        visible: true,
        tone: 'error',
        title: '재판에 연결하지 못했습니다.',
        description:
          props.connection.error?.message || '연결 상태를 확인한 뒤 다시 시도해 주세요.',
        retryable: true,
      }
    default:
      return { visible: false }
  }
})
</script>

<template>
  <section
    v-if="presentation.visible"
    class="connection-status"
    :class="`tone-${presentation.tone}`"
    :role="presentation.tone === 'error' ? 'alert' : 'status'"
    aria-live="polite"
  >
    <LoaderCircle v-if="presentation.tone === 'loading'" class="status-icon spinning" :size="21" />
    <RefreshCw v-else-if="presentation.tone === 'warning'" class="status-icon" :size="21" />
    <CircleAlert v-else-if="presentation.tone === 'error'" class="status-icon" :size="21" />
    <Gavel v-else class="status-icon" :size="21" />
    <div>
      <strong>{{ presentation.title }}</strong>
      <p>{{ presentation.description }}</p>
    </div>
    <button v-if="presentation.retryable" type="button" @click="$emit('retry')">
      <RefreshCw :size="15" /> 다시 시도
    </button>
  </section>
</template>

<style scoped>
.connection-status {
  min-height: 64px;
  margin-bottom: 14px;
  padding: 12px 16px;
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  gap: 12px;
  border: 1px solid #c9d8eb;
  border-radius: var(--ds-radius-default);
  background: #eef5ff;
  color: var(--ds-color-primary);
}

.connection-status strong {
  display: block;
  font-size: 0.8rem;
}

.connection-status p {
  margin: 3px 0 0;
  color: var(--ds-color-on-surface-variant);
  font-size: 0.7rem;
}

.status-icon {
  color: var(--ds-color-justice-blue);
}

.tone-warning {
  border-color: #e9d5a4;
  background: #fff9e9;
}

.tone-warning .status-icon {
  color: #a56a00;
}

.tone-error {
  border-color: #efc3c3;
  background: #fff1f1;
}

.tone-error .status-icon {
  color: var(--ds-color-error);
}

.tone-ended {
  border-color: #cad2dd;
  background: #f3f5f8;
}

.tone-ended .status-icon {
  color: #687486;
}

button {
  min-height: 34px;
  padding: 0 12px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border: 1px solid currentColor;
  border-radius: var(--ds-radius-default);
  background: white;
  color: var(--ds-color-error);
  font-size: 0.7rem;
  font-weight: 700;
  cursor: pointer;
}

.spinning {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 620px) {
  .connection-status {
    grid-template-columns: auto 1fr;
  }

  button {
    grid-column: 2;
    width: fit-content;
  }
}
</style>
