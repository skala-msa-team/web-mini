<script setup>
import { computed, onMounted, ref, watch } from "vue";
import { PenLine, Search } from "@lucide/vue";
import Input from "@/components/ui/Input.vue";

import { getTrials } from "@/apis/trialApi.js";
import { TRIAL_STATUS_LABEL } from "@/consts/trialStatus.js";
import { useLiveTrialPresenceList } from "@/composables/useLiveTrialPresenceList.js";
import CommunityLayout from "@/components/community/CommunityLayout.vue";
import EmptyPosts from "@/components/community/EmptyPosts.vue";
import LiveTrialCard from "@/components/community/LiveTrialCard.vue";
import PostCard from "@/components/community/PostCard.vue";
import { useCommunityStore } from "@/stores/communityStore.js";
import {
  categoryTypes,
  relationshipTypes,
  liveTrials as mockLiveTrials,
} from "@/mock/community/communityData.js";
import { COMMUNITY_LIVE_TRIAL_PAGE_SIZE, COMMUNITY_POST_PAGE_SIZE } from "@/consts/api.js";

const searchQuery = ref("");
const selectedCategory = ref("전체");
const selectedRelationship = ref("전체 관계");
const currentPage = ref(1);
const pageSize = COMMUNITY_POST_PAGE_SIZE;
const { state } = useCommunityStore();
const liveTrials = ref([]);
const liveTrialsLoading = ref(true);
const liveTrialsError = ref("");
const isMockLiveTrials = ref(false);

function toDisplayTrial(trial) {
  return {
    id: trial.trialId,
    title: trial.title,
    statusLabel: TRIAL_STATUS_LABEL[trial.status] || "공개 재판 진행 중",
    viewerCount: trial.viewerCount ?? trial.audienceCount ?? 0,
  }
}

function setMockLiveTrials(reason = "") {
  liveTrials.value = mockLiveTrials.map((trial) => ({
    id: trial.id,
    title: trial.title,
    statusLabel: "데모 라이브 재판",
    isMock: true,
    viewerCount: trial.viewerCount,
  }));
  isMockLiveTrials.value = true;
  if (reason) liveTrialsError.value = reason
}

async function loadLiveTrials() {
  liveTrialsLoading.value = true;
  liveTrialsError.value = "";
  isMockLiveTrials.value = false;

  try {
    const response = await getTrials({ page: 0, size: COMMUNITY_LIVE_TRIAL_PAGE_SIZE });
    const realTrials = response.items || [];

    if (realTrials.length) {
      liveTrials.value = realTrials.map((trial) => ({
        ...toDisplayTrial(trial),
        isMock: false,
      }));
      liveTrialsError.value = "";
      return;
    }

    setMockLiveTrials("현재 진행 중인 라이브 재판이 없어 데모 목록을 표시합니다.");
  } catch (error) {
    setMockLiveTrials(error.message || "라이브 재판 목록을 불러오지 못해 데모 목록을 표시합니다.");
  } finally {
    liveTrialsLoading.value = false;
  }
}

onMounted(loadLiveTrials);

useLiveTrialPresenceList(liveTrials, ({ trialId, audienceCount }) => {
  liveTrials.value = liveTrials.value.map((trial) =>
    trial.id === trialId ? { ...trial, viewerCount: audienceCount } : trial,
  );
});

const filteredPosts = computed(() => {
  const query = searchQuery.value.trim().toLowerCase();
  return state.posts.filter((post) => {
    const matchesCategory =
      selectedCategory.value === "전체" ||
      post.category === selectedCategory.value;
    const matchesRelationship =
      selectedRelationship.value === "전체 관계" ||
      post.relationshipType === selectedRelationship.value;
    const matchesQuery =
      !query || `${post.title} ${post.summary}`.toLowerCase().includes(query);
    return matchesCategory && matchesRelationship && matchesQuery;
  });
});
const pageCount = computed(() =>
  Math.max(1, Math.ceil(filteredPosts.value.length / pageSize)),
);
const paginatedPosts = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  return filteredPosts.value.slice(start, start + pageSize);
});

watch(
  [searchQuery, selectedCategory, selectedRelationship],
  () => {
    currentPage.value = 1;
  },
);

function resetFilters() {
  searchQuery.value = "";
  selectedCategory.value = "전체";
  selectedRelationship.value = "전체 관계";
  currentPage.value = 1;
}
</script>

<template>
  <CommunityLayout>
    <section class="space-y-10">
      <div class="flex flex-col gap-4 sm:flex-row">
        <label class="flex h-11 max-w-xl flex-1 items-center gap-2 rounded-lg border border-input bg-card px-3 text-muted-foreground focus-within:ring-2 focus-within:ring-ring">
          <Search :size="17" aria-hidden="true" />
          <Input
            v-model="searchQuery"
            type="search"
            placeholder="궁금한 고민을 검색해보세요"
            class="h-9 border-0 bg-transparent p-0 shadow-none focus-visible:ring-0"
          />
        </label>
      </div>

      <div class="flex flex-col items-start gap-4 sm:flex-row sm:items-center">
        <div class="flex flex-wrap gap-2" aria-label="카테고리 필터">
          <button
            v-for="type in categoryTypes"
            :key="type"
            type="button"
            class="rounded-full border px-4 py-2 text-sm font-semibold transition"
            :class="selectedCategory === type ? 'border-primary bg-primary text-primary-foreground' : 'border-border bg-card text-muted-foreground hover:bg-muted'"
            @click="selectedCategory = type"
          >
            {{ type }}
          </button>
        </div>
        <label class="flex items-center gap-2 text-sm text-muted-foreground sm:ml-auto">
          <span class="font-semibold">관계 유형</span>
          <select v-model="selectedRelationship" class="rounded-lg border border-input bg-card px-3 py-2 text-sm text-foreground outline-none focus:ring-2 focus:ring-ring">
            <option v-for="type in relationshipTypes" :key="type">
              {{ type }}
            </option>
          </select>
        </label>
      </div>

      <section id="live-trials">
        <div class="flex items-center justify-between">
          <h2 class="text-xl font-bold tracking-tight">실시간 라이브 재판</h2>
          <a class="text-sm font-semibold text-primary" href="#">전체보기</a>
        </div>
        <div class="mt-4 grid gap-3 md:grid-cols-3">
          <p v-if="liveTrialsLoading" class="col-span-full rounded-xl border border-border bg-card px-5 py-8 text-center text-sm text-muted-foreground">Live 재판을 불러오는 중입니다.</p>
          <p v-else-if="liveTrialsError && !isMockLiveTrials" class="col-span-full rounded-xl border border-red-200 bg-red-50 px-5 py-8 text-center text-sm text-red-700" role="alert">
            {{ liveTrialsError }}
          </p>
          <p v-else-if="liveTrialsError && isMockLiveTrials" class="col-span-full rounded-xl border border-border bg-card px-5 py-4 text-center text-xs text-muted-foreground">
            {{ liveTrialsError }}
          </p>
          <template v-else-if="liveTrials.length">
            <LiveTrialCard
              v-for="trial in liveTrials"
              :key="trial.id"
              :trial="trial"
              :is-mock="trial.isMock"
            />
          </template>
          <p v-else class="col-span-full rounded-xl border border-border bg-card px-5 py-8 text-center text-sm text-muted-foreground">현재 진행 중인 공개 재판이 없습니다.</p>
        </div>
      </section>

      <section id="popular-posts">
        <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <h2 class="text-xl font-bold tracking-tight">인기게시글</h2>
          <RouterLink
            :to="{ name: 'post-create' }"
            class="inline-flex w-fit items-center gap-2 rounded-lg border border-[var(--ds-color-primary-fixed-dim)] bg-[var(--ds-color-primary-fixed)] px-4 py-2 text-sm font-semibold text-[var(--ds-color-on-primary-fixed)] shadow-sm transition hover:border-primary/30 hover:bg-[var(--ds-color-secondary-fixed)] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
          >
            <PenLine :size="16" aria-hidden="true" />
            고민 쓰기
          </RouterLink>
        </div>
        <div v-if="paginatedPosts.length" class="mt-4 grid gap-3">
          <PostCard v-for="post in paginatedPosts" :key="post.id" :post="post" />
        </div>
        <EmptyPosts v-else @reset="resetFilters" />
      </section>

      <nav class="flex justify-center gap-1.5" aria-label="페이지 이동">
        <button
          type="button"
          aria-label="이전 페이지"
          :disabled="currentPage === 1"
          @click="currentPage -= 1"
        >
          ‹
        </button>
        <button
          v-for="page in pageCount"
          :key="page"
          type="button"
          class="size-9 rounded-lg border border-border bg-card text-sm transition hover:bg-muted disabled:cursor-not-allowed disabled:opacity-40"
          :class="currentPage === page ? 'border-primary bg-primary text-primary-foreground' : ''"
          @click="currentPage = page"
        >
          {{ page }}
        </button>
        <button
          type="button"
          aria-label="다음 페이지"
          :disabled="currentPage === pageCount"
          @click="currentPage += 1"
        >
          ›
        </button>
      </nav>

      <section id="community-guidelines" class="rounded-xl bg-primary p-6 text-primary-foreground shadow-sm">
        <h2 class="text-lg font-bold">커뮤니티 가이드라인</h2>
        <p class="mt-2 text-sm text-primary-foreground/75">서로의 고민을 존중하고, 비난보다 따뜻한 의견을 남겨주세요.</p>
      </section>
    </section>
  </CommunityLayout>
</template>
