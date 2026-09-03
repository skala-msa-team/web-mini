<script setup>
import { computed, onMounted, ref } from "vue";
import { Search } from "@lucide/vue";

import { trialApi } from "@/api/trialApi.js";
import { TRIAL_STATUS_LABEL } from "@/constants/trialStatus.js";
import CommunityLayout from "@/features/community/components/CommunityLayout.vue";
import EmptyPosts from "@/features/community/components/EmptyPosts.vue";
import LiveTrialCard from "@/features/community/components/LiveTrialCard.vue";
import PostCard from "@/features/community/components/PostCard.vue";
import {
  categoryTypes,
  communityPosts,
  relationshipTypes,
} from "@/features/community/mock/communityData.js";

const searchQuery = ref("");
const selectedCategory = ref("전체");
const selectedRelationship = ref("전체 관계");
const currentPage = ref(1);
const liveTrials = ref([]);
const liveTrialsLoading = ref(true);
const liveTrialsError = ref("");

async function loadLiveTrials() {
  liveTrialsLoading.value = true;
  liveTrialsError.value = "";

  try {
    const response = await trialApi.getTrials({ page: 0, size: 3 });
    liveTrials.value = (response.items || []).map((trial) => ({
      id: trial.trialId,
      title: trial.title,
      statusLabel: TRIAL_STATUS_LABEL[trial.status] || "공개 재판 진행 중",
    }));
  } catch (error) {
    liveTrialsError.value = error.message || "Live 재판 목록을 불러오지 못했습니다.";
  } finally {
    liveTrialsLoading.value = false;
  }
}

onMounted(loadLiveTrials);

const filteredPosts = computed(() => {
  const query = searchQuery.value.trim().toLowerCase();
  return communityPosts.filter((post) => {
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

function resetFilters() {
  searchQuery.value = "";
  selectedCategory.value = "전체";
  selectedRelationship.value = "전체 관계";
  currentPage.value = 1;
}
</script>

<template>
  <CommunityLayout>
    <section class="community-content">
      <div class="community-tools">
        <label class="search-field">
          <Search :size="17" aria-hidden="true" />
          <input
            v-model="searchQuery"
            type="search"
            placeholder="궁금한 고민을 검색해보세요"
          />
        </label>
        <RouterLink class="button button--primary" to="/community/posts/new"
          >글쓰기</RouterLink
        >
      </div>

      <div class="community-filters">
        <div class="filter-chips" aria-label="카테고리 필터">
          <button
            v-for="type in categoryTypes"
            :key="type"
            type="button"
            :class="{ 'filter-chip--active': selectedCategory === type }"
            @click="selectedCategory = type"
          >
            {{ type }}
          </button>
        </div>
        <label class="conflict-filter">
          <span>관계 유형</span>
          <select v-model="selectedRelationship">
            <option v-for="type in relationshipTypes" :key="type">
              {{ type }}
            </option>
          </select>
        </label>
      </div>

      <section id="live-trials" class="live-trials">
        <div class="section-title-row">
          <h2>실시간 라이브 재판</h2>
          <a href="#">전체보기</a>
        </div>
        <div class="live-trial-grid">
          <p v-if="liveTrialsLoading" class="live-trial-feedback">Live 재판을 불러오는 중입니다.</p>
          <p v-else-if="liveTrialsError" class="live-trial-feedback" role="alert">
            {{ liveTrialsError }}
          </p>
          <template v-else-if="liveTrials.length">
            <LiveTrialCard
              v-for="trial in liveTrials"
              :key="trial.id"
              :trial="trial"
            />
          </template>
          <p v-else class="live-trial-feedback">현재 진행 중인 공개 재판이 없습니다.</p>
        </div>
      </section>

      <section id="popular-posts">
        <div class="section-title-row"><h2>인기게시글</h2></div>
        <div v-if="filteredPosts.length" class="post-list">
          <PostCard v-for="post in filteredPosts" :key="post.id" :post="post" />
        </div>
        <EmptyPosts v-else @reset="resetFilters" />
      </section>

      <nav class="pagination" aria-label="페이지 이동">
        <button
          type="button"
          aria-label="이전 페이지"
          :disabled="currentPage === 1"
          @click="currentPage -= 1"
        >
          ‹
        </button>
        <button
          v-for="page in 3"
          :key="page"
          type="button"
          :class="{ pagination__active: currentPage === page }"
          @click="currentPage = page"
        >
          {{ page }}
        </button>
        <button
          type="button"
          aria-label="다음 페이지"
          :disabled="currentPage === 3"
          @click="currentPage += 1"
        >
          ›
        </button>
      </nav>

      <section id="community-guidelines" class="community-guidelines">
        <h2>커뮤니티 가이드라인</h2>
        <p>서로의 고민을 존중하고, 비난보다 따뜻한 의견을 남겨주세요.</p>
      </section>
    </section>
  </CommunityLayout>
</template>

<style scoped>
.live-trial-feedback {
  grid-column: 1 / -1;
  margin: 0;
  padding: 30px 18px;
  border: 1px solid var(--ds-color-card-border);
  border-radius: var(--ds-radius-md);
  background: var(--ds-color-surface-container-lowest);
  color: var(--ds-color-on-surface-variant);
  font-size: 0.82rem;
  text-align: center;
}
</style>
