<script setup>
import { computed } from 'vue'
import { cva } from 'class-variance-authority'
import { cn } from '@/lib/utils.js'

const props = defineProps({
  variant: { type: String, default: 'default' },
  size: { type: String, default: 'default' },
  as: { type: String, default: 'button' },
})

const buttonVariants = cva(
  'inline-flex items-center justify-center gap-2 rounded-lg text-sm font-semibold transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring disabled:pointer-events-none disabled:opacity-50',
  {
    variants: {
      variant: {
        default: 'bg-primary text-primary-foreground hover:bg-primary/90',
        outline: 'border border-border bg-card text-foreground hover:bg-muted',
        ghost: 'text-muted-foreground hover:bg-muted hover:text-foreground',
        destructive: 'bg-destructive text-destructive-foreground hover:bg-destructive/90',
      },
      size: {
        default: 'min-h-10 px-4 py-2',
        sm: 'min-h-9 px-3 text-xs',
        lg: 'min-h-11 px-5',
        icon: 'size-10',
      },
    },
    defaultVariants: { variant: 'default', size: 'default' },
  },
)

const classes = computed(() => cn(buttonVariants({ variant: props.variant, size: props.size })))
</script>

<template>
  <component :is="as" v-bind="$attrs" :class="classes">
    <slot />
  </component>
</template>
