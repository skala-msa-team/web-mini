<script setup>
import { computed, ref } from "vue";
import { useRouter } from "vue-router";
import { Bold, Image, Italic, Link, Scale } from "@lucide/vue";

import { postApi } from "@/api/postApi.js";
import CommunityLayout from "@/features/community/components/CommunityLayout.vue";
import { categoryTypes } from "@/features/community/mock/communityData.js";
import { useCommunityStore } from "@/features/community/stores/communityStore.js";

const TRIAL_DRAFT_STORAGE_KEY = "love-war:trial-draft";
const relationshipOptions = [
  { label: "연인", value: "COUPLE" },
  { label: "썸", value: "SOME" },
  { label: "부부", value: "SPOUSE" },
];
const router = useRouter();
const { addPost } = useCommunityStore();

const category = ref("");
const relationshipType = ref("");
const title = ref("");
const content = ref("");
const requestTrial = ref(false);
const submitPending = ref(false);
const submitError = ref("");
const canSubmit = computed(() =>
  Boolean(
    category.value &&
      relationshipType.value &&
      title.value.trim() &&
      content.value.trim(),
  ),
);

async function submitPost() {
  if (!canSubmit.value || submitPending.value) return;

  submitPending.value = true;
  submitError.value = "";

  try {
    const createdPost = await postApi.createPost({
      title: title.value.trim(),
      content: content.value.trim(),
      relationshipType: relationshipType.value,
      trialRequested: requestTrial.value,
    });

    if (!requestTrial.value) {
      const localPost = addPost({
        id: `created-${createdPost.postId}-${crypto.randomUUID()}`,
        backendPostId: createdPost.postId,
        category: category.value,
        relationshipType: createdPost.relationshipType,
        title: createdPost.title,
        content: createdPost.content,
      });
      sessionStorage.removeItem(TRIAL_DRAFT_STORAGE_KEY);
      await router.push({
        name: "post-detail",
        params: { postId: localPost.id },
      });
      return;
    }

    sessionStorage.setItem(
      TRIAL_DRAFT_STORAGE_KEY,
      JSON.stringify({
        postId: createdPost.postId,
        title: createdPost.title,
        content: createdPost.content,
        relationshipType: createdPost.relationshipType,
        category: category.value,
      }),
    );

    await router.push({
      name: "trial-preparation",
      query: { postId: createdPost.postId },
    });
  } catch (error) {
    submitError.value =
      error?.message || "게시글을 등록하지 못했습니다. 잠시 후 다시 시도해주세요.";
  } finally {
    submitPending.value = false;
  }
}
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
      <label class="form-field">
        <span>관계 유형</span>
        <select v-model="relationshipType">
          <option value="" disabled>관계 유형을 선택하세요</option>
          <option
            v-for="option in relationshipOptions"
            :key="option.value"
            :value="option.value"
          >
            {{ option.label }}
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
      <p v-if="submitError" class="form-error" role="alert">
        {{ submitError }}
      </p>
      <div class="form-actions">
        <RouterLink to="/community">취소</RouterLink
        ><button
          class="button button--primary"
          type="button"
          :disabled="!canSubmit || submitPending"
          @click="submitPost"
        >
          {{ submitPending ? "등록 중..." : "등록하기" }}
        </button>
      </div>
    </section>
  </CommunityLayout>
</template>

<style scoped>
.form-field + .form-field {
  margin-top: 12px;
}

.form-error {
  margin: 12px 0 0;
  color: var(--ds-color-error);
  font-size: 13px;
}
</style>
