<script setup>
import { Check } from '@lucide/vue'

defineProps({
  currentStep: {
    type: Number,
    required: true,
  },
})

const steps = ['기본 정보', 'A측 진술', 'B측 진술', '최종 확인']
</script>

<template>
  <ol class="grid grid-cols-4" aria-label="재판 생성 진행 단계">
    <li v-for="(label, index) in steps" :key="label" class="relative flex flex-col items-center gap-2 text-center">
      <div
        v-if="index < steps.length - 1"
        class="absolute left-[calc(50%+2rem)] right-[calc(-50%+2rem)] top-4 h-px"
        :class="index + 1 < currentStep ? 'bg-primary' : 'bg-border'"
      />
      <span
        class="relative z-10 grid size-8 place-items-center rounded-full border text-sm font-semibold"
        :class="
          index + 1 === currentStep
            ? 'border-primary bg-primary text-primary-foreground'
            : index + 1 < currentStep
              ? 'border-[var(--ds-color-primary-fixed-dim)] bg-[var(--ds-color-primary-fixed)] text-[var(--ds-color-primary)]'
              : 'border-transparent bg-muted text-muted-foreground'
        "
      >
        <Check v-if="index + 1 < currentStep" class="size-4" />
        <template v-else>{{ index + 1 }}</template>
      </span>
      <span
        class="text-xs sm:text-sm"
        :class="index + 1 === currentStep ? 'font-semibold text-primary' : 'text-muted-foreground'"
      >
        {{ label }}
      </span>
    </li>
  </ol>
</template>
