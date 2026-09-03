<script setup>
import { AlertTriangle, CheckCircle2, Gavel, LoaderCircle, UserRound } from '@lucide/vue'

defineProps({
  trial: {
    type: Object,
    required: true,
  },
  parties: {
    type: Object,
    required: true,
  },
  bothConfirmed: {
    type: Boolean,
    required: true,
  },
  startLoading: {
    type: Boolean,
    default: false,
  },
  startError: {
    type: String,
    default: '',
  },
})

defineEmits(['back', 'start'])
</script>

<template>
  <section class="rounded-xl border border-border bg-card p-5 shadow-interactive sm:p-7">
    <div class="mb-6 flex gap-3 rounded-lg border border-[var(--ds-color-accent-red)] bg-[var(--ds-color-error-container)] p-4 text-[var(--ds-color-on-error-container)]">
      <AlertTriangle class="mt-0.5 size-5 shrink-0" />
      <div>
        <h2 class="font-heading font-semibold">주의사항</h2>
        <p class="mt-1 text-sm">재판이 시작되면 진술을 수정할 수 없습니다. 양측의 정보가 정확한지 확인해주세요.</p>
      </div>
    </div>

    <div class="grid gap-4">
      <article class="rounded-xl border border-border p-5">
        <div class="mb-4 border-b border-border pb-3">
          <h2 class="flex items-center gap-2 font-heading text-lg font-semibold"><Gavel class="size-5 text-primary" /> 사건 개요</h2>
        </div>
        <dl class="grid gap-4 text-sm">
          <div><dt class="text-muted-foreground">재판 제목</dt><dd class="mt-1 text-base">{{ trial.title }}</dd></div>
          <div><dt class="text-muted-foreground">당사자 관계</dt><dd class="mt-1">{{ trial.aDisplayName }}(A측) · {{ trial.bDisplayName }}(B측)</dd></div>
          <div><dt class="text-muted-foreground">사건 요약</dt><dd class="mt-1 leading-6">{{ trial.summary }}</dd></div>
        </dl>
      </article>

      <article v-for="side in ['A', 'B']" :key="side" class="rounded-xl border border-border p-5">
        <div class="mb-4 flex items-center justify-between border-b border-border pb-3">
          <h2 class="flex items-center gap-2 font-heading text-lg font-semibold">
            <UserRound class="size-4" :class="side === 'A' ? 'text-primary' : 'text-destructive'" />
            {{ side }}측 진술
          </h2>
          <span class="flex items-center gap-1 text-sm" :class="parties[side].confirmed ? 'text-primary' : 'text-muted-foreground'">
            <CheckCircle2 class="size-4" /> {{ parties[side].confirmed ? '확정 완료' : '확정 필요' }}
          </span>
        </div>
        <p class="mb-2 text-xs text-muted-foreground">사건 개요 및 쟁점 파악</p>
        <p class="mb-5 text-sm leading-6">{{ parties[side].factSummary }}</p>
        <p class="mb-2 text-xs text-muted-foreground">완성 변론문</p>
        <p class="text-sm leading-6">{{ parties[side].argumentText }}</p>
      </article>
    </div>

    <p v-if="startError" class="mt-5 rounded-lg bg-destructive/10 px-4 py-3 text-sm text-destructive" role="alert">
      {{ startError }}
    </p>

    <div class="mt-6 grid grid-cols-[1fr_2fr] gap-3">
      <button class="rounded-lg border border-primary px-5 py-3 text-primary" type="button" :disabled="startLoading" @click="$emit('back')">이전</button>
      <button
        class="flex items-center justify-center gap-2 rounded-lg bg-[var(--ds-color-primary)] px-5 py-3 font-semibold text-white disabled:cursor-not-allowed disabled:opacity-40"
        type="button"
        :disabled="!bothConfirmed || startLoading"
        @click="$emit('start')"
      >
        <LoaderCircle v-if="startLoading" class="size-4 animate-spin" />
        <Gavel v-else class="size-4" />
        {{ startLoading ? '재판 시작 중...' : '재판 시작하기' }}
      </button>
    </div>
  </section>
</template>
