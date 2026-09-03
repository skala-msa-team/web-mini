<script setup>
import { Scale } from '@lucide/vue'

defineProps({
  participants: {
    type: Array,
    required: true,
  },
})
</script>

<template>
  <section class="trial-stage" aria-label="재판 참여 AI">
    <article
      v-for="participant in participants"
      :key="participant.id"
      class="participant"
      :class="[`participant-${participant.position}`, `tone-${participant.tone}`]"
    >
      <div
        class="portrait"
        :class="`portrait-${participant.position}`"
        role="img"
        :aria-label="participant.name"
      ></div>
      <div class="participant-label">
        <Scale v-if="participant.position === 'center'" :size="16" />
        <span>{{ participant.name }}</span>
        <small v-if="participant.position !== 'center'">({{ participant.role }})</small>
      </div>
    </article>
  </section>
</template>

<style scoped>
.trial-stage {
  min-height: 286px;
  padding: 24px 44px 22px;
  display: grid;
  grid-template-columns: 1fr 1.15fr 1fr;
  align-items: end;
  gap: 28px;
  border: 1px solid var(--ds-color-outline-variant);
  border-radius: var(--ds-radius-md);
  background:
    radial-gradient(circle at 50% 12%, rgb(255 255 255 / 72%), transparent 25%),
    linear-gradient(135deg, #d7e6fb 0%, #eff5ff 54%, #eef4ff 100%);
  overflow: hidden;
}

.participant {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14px;
}

.participant-center {
  align-self: start;
  gap: 16px;
}

.portrait {
  width: 120px;
  aspect-ratio: 1;
  border: 4px solid white;
  border-radius: 50%;
  background-color: #cbdcf3;
  background-image: url('/images/trial-portraits.png');
  background-repeat: no-repeat;
  background-size: 300% auto;
  box-shadow: 0 12px 30px rgb(26 54 93 / 18%);
}

.portrait-left {
  background-position: left center;
}

.portrait-center {
  width: 174px;
  border-color: var(--ds-color-primary);
  background-position: center;
}

.portrait-right {
  background-position: right center;
}

.participant-label {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  min-width: 150px;
  min-height: 32px;
  padding: 0 16px;
  border-radius: var(--ds-radius-full);
  background: white;
  box-shadow: 0 3px 8px rgb(15 35 62 / 16%);
  color: var(--ds-color-primary);
  font-size: 0.78rem;
  font-weight: 700;
}

.participant-label small {
  font-size: inherit;
}

.tone-navy .participant-label {
  background: var(--ds-color-primary);
  color: white;
}

.tone-blue .participant-label {
  background: var(--ds-color-justice-blue);
  color: white;
}

@media (max-width: 680px) {
  .trial-stage {
    min-height: auto;
    padding: 28px 16px 22px;
    gap: 12px;
  }

  .portrait {
    width: min(25vw, 100px);
  }

  .portrait-center {
    width: min(31vw, 132px);
  }

  .participant-label {
    min-width: 0;
    width: 100%;
    padding: 7px 8px;
    text-align: center;
    font-size: 0.67rem;
  }

  .participant-label small {
    display: none;
  }
}
</style>
