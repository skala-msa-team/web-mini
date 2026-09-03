<script setup>
import { AlertTriangle, FilePenLine, Gavel, UserRound } from '@lucide/vue'

defineProps({
  trial: {
    type: Object,
    required: true,
  },
  parties: {
    type: Object,
    required: true,
  },
})

defineEmits(['back', 'edit'])
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
        <div class="mb-4 flex items-center justify-between border-b border-border pb-3">
          <h2 class="flex items-center gap-2 font-heading text-lg font-semibold"><Gavel class="size-5 text-primary" /> 사건 개요</h2>
          <button class="flex items-center gap-1 text-sm text-primary" type="button" @click="$emit('edit', 1)"><FilePenLine class="size-4" />수정</button>
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
          <button class="flex items-center gap-1 text-sm text-primary" type="button" @click="$emit('edit', side === 'A' ? 2 : 3)"><FilePenLine class="size-4" />수정</button>
        </div>
        <p class="mb-2 text-xs text-muted-foreground">사건 개요 및 쟁점 파악</p>
        <p class="mb-5 text-sm leading-6">{{ parties[side].caseOverview }}</p>
        <p class="mb-2 text-xs text-muted-foreground">핵심 진술 요지</p>
        <ul class="mb-5 list-disc space-y-1 pl-5 text-sm leading-6">
          <li v-for="point in parties[side].keyPoints" :key="point">{{ point }}</li>
        </ul>
        <p class="mb-2 text-xs text-muted-foreground">최종 변론문</p>
        <p class="text-sm leading-6">{{ parties[side].argumentText }}</p>
      </article>
    </div>

    <div class="mt-6 grid grid-cols-[1fr_2fr] gap-3">
      <button class="rounded-lg border border-primary px-5 py-3 text-primary" type="button" @click="$emit('back')">이전</button>
      <button class="flex items-center justify-center gap-2 rounded-lg bg-[var(--ds-color-primary)] px-5 py-3 font-semibold text-white" type="button">
        재판 시작하기 <Gavel class="size-4" />
      </button>
    </div>
  </section>
</template>
