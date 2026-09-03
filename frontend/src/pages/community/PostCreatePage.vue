<script setup>
import { computed, ref } from "vue";
import { Bold, Image, Italic, Link, Scale } from "@lucide/vue";

import CommunityLayout from "@/features/community/components/CommunityLayout.vue";
import { categoryTypes } from "@/features/community/mock/communityData.js";

const category = ref("");
const title = ref("");
const content = ref("");
const requestTrial = ref(false);
const canSubmit = computed(() =>
  Boolean(category.value && title.value.trim() && content.value.trim()),
);
</script>

<template>
  <CommunityLayout>
    <section class="post-editor">
      <h1>글쓰기</h1>
      <label class="form-field">
        <span>카테고리</span>
        <select v-model="category">
          <option value="" disabled>카테고리를 선택하세요</option>
          <option v-for="type in categoryTypes.slice(1)" :key="type">
            {{ type }}
          </option>
        </select>
      </label>
      <input
        v-model="title"
        class="post-editor__title"
        placeholder="제목을 입력하세요."
      />
      <div class="post-editor__toolbar" aria-label="글 편집 도구">
        <button type="button" aria-label="굵게"><Bold :size="17" /></button
        ><button type="button" aria-label="기울임"><Italic :size="17" /></button
        ><button type="button" aria-label="이미지"><Image :size="17" /></button
        ><button type="button" aria-label="링크"><Link :size="17" /></button>
      </div>
      <textarea
        v-model="content"
        class="post-editor__content"
        placeholder="당신의 이야기를 자세히 적어주세요. 판결에 도움이 될 구체적인 상황일수록 좋습니다."
      />
      <label class="trial-request"
        ><Scale :size="25" /><span
          ><strong>재판 신청 (Request Trial)</strong
          ><small
            >게시글 등록과 동시에 AI 재판을 신청하시겠습니까? 신청 시 AI
            변호사와 함께 재판을 준비하게 됩니다.</small
          ></span
        ><input v-model="requestTrial" type="checkbox"
      /></label>
      <div class="form-actions">
        <RouterLink to="/community">취소</RouterLink
        ><button
          class="button button--primary"
          type="button"
          :disabled="!canSubmit"
        >
          등록하기
        </button>
      </div>
    </section>
  </CommunityLayout>
</template>
