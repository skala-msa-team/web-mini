<script setup>
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'
import { Flag, MessageCircle, Reply, ThumbsUp } from '@lucide/vue'
import CommunityLayout from '@/components/community/CommunityLayout.vue'
import { useCommunityStore } from '@/stores/communityStore.js'

const route = useRoute()
const {
  state,
  findPost,
  toggleLike,
  toggleReport,
  addComment,
  updateComment,
  removeComment,
} = useCommunityStore()

const post = computed(() => findPost(route.params.postId))
const relatedPosts = computed(() =>
  state.posts
    .filter((item) => item.id !== post.value?.id && item.category === post.value?.category)
    .slice(0, 3),
)
const commentInput = ref('')
const replyTargetId = ref(null)
const replyInput = ref('')
const editingId = ref(null)
const editingInput = ref('')

function submitComment() {
  const content = commentInput.value.trim()
  if (!content || !post.value) return

  addComment(post.value.id, content)
  commentInput.value = ''
}

function openReply(commentId) {
  replyTargetId.value = replyTargetId.value === commentId ? null : commentId
  replyInput.value = ''
}

function submitReply() {
  const content = replyInput.value.trim()
  if (!content || !post.value || !replyTargetId.value) return

  addComment(post.value.id, content, replyTargetId.value)
  replyTargetId.value = null
  replyInput.value = ''
}

function startEditing(comment) {
  editingId.value = comment.id
  editingInput.value = comment.content
}

function saveEditing() {
  const content = editingInput.value.trim()
  if (!content || !post.value || !editingId.value) return

  updateComment(post.value.id, editingId.value, content)
  editingId.value = null
  editingInput.value = ''
}
</script>

<template>
  <CommunityLayout>
    <section v-if="post" class="post-detail-layout">
      <article class="post-detail-card">
        <RouterLink class="breadcrumb" :to="{ name: 'home' }">커뮤니티 홈 › {{ post.category }}</RouterLink>
        <div class="post-detail-card__title">
          <h1>{{ post.title }}</h1>
          <span v-if="post.isLive" class="live-badge">● LIVE</span>
        </div>
        <p class="post-author">{{ post.author }} · {{ post.createdAt }} · {{ post.relationshipType }}</p>
        <div class="post-body">{{ post.content }}</div>

        <div class="reaction-buttons">
          <button
            type="button"
            :class="{ 'reaction-button--active': post.liked }"
            :aria-pressed="post.liked"
            @click="toggleLike(post.id)"
          >
            <ThumbsUp :size="17" /> 좋아요 {{ post.likeCount }}
          </button>
          <span><MessageCircle :size="17" /> 댓글 {{ post.commentCount }}</span>
          <button
            type="button"
            :class="{ 'reaction-button--reported': post.reported }"
            :aria-pressed="post.reported"
            @click="toggleReport(post.id)"
          >
            <Flag :size="16" /> {{ post.reported ? '신고 취소' : '신고' }}
          </button>
        </div>
        <p v-if="post.reported" class="report-notice" role="status">신고가 접수된 상태입니다.</p>
      </article>

      <aside class="related-posts">
        <h2>같은 카테고리 고민</h2>
        <RouterLink
          v-for="item in relatedPosts"
          :key="item.id"
          :to="{ name: 'post-detail', params: { postId: item.id } }"
        >
          {{ item.title }}
        </RouterLink>
        <p v-if="!relatedPosts.length">관련 게시글이 없습니다.</p>
      </aside>

      <section v-if="post.isLive" class="trial-banner">
        <div><strong>이 고민은 LIVE 재판이 진행 중입니다.</strong><p>배심원으로 참여해 의견을 남겨보세요.</p></div>
        <RouterLink class="button" :to="{ name: 'live-trial', params: { trialId: post.id } }">재판 참여하기</RouterLink>
      </section>

      <section class="comments-section">
        <h2><MessageCircle :size="20" /> 댓글</h2>
        <form class="comment-form" @submit.prevent="submitComment">
          <textarea v-model="commentInput" placeholder="따뜻한 의견을 남겨주세요." maxlength="1000" />
          <button class="button button--primary" type="submit" :disabled="!commentInput.trim()">댓글 등록</button>
        </form>

        <article v-for="comment in post.comments" :key="comment.id" class="comment-item">
          <strong>{{ comment.author }}</strong><time>{{ comment.createdAt }}</time>
          <template v-if="editingId === comment.id">
            <textarea v-model="editingInput" class="comment-edit-input" maxlength="1000" />
            <div class="comment-actions"><button type="button" @click="editingId = null">취소</button><button type="button" @click="saveEditing">저장</button></div>
          </template>
          <p v-else>{{ comment.content }}</p>
          <div class="comment-actions">
            <button type="button" @click="openReply(comment.id)"><Reply :size="14" /> 답글</button>
            <button v-if="comment.isOwner" type="button" @click="startEditing(comment)">수정</button>
            <button v-if="comment.isOwner" type="button" @click="removeComment(post.id, comment.id)">삭제</button>
          </div>

          <form v-if="replyTargetId === comment.id" class="reply-form" @submit.prevent="submitReply">
            <input v-model="replyInput" placeholder="답글을 입력하세요." maxlength="1000" />
            <button class="button button--primary" type="submit" :disabled="!replyInput.trim()">등록</button>
          </form>

          <div v-for="reply in comment.replies" :key="reply.id" class="comment-reply">
            <strong>{{ reply.author }}</strong><time>{{ reply.createdAt }}</time>
            <template v-if="editingId === reply.id">
              <textarea v-model="editingInput" class="comment-edit-input" maxlength="1000" />
              <div class="comment-actions"><button type="button" @click="editingId = null">취소</button><button type="button" @click="saveEditing">저장</button></div>
            </template>
            <p v-else>{{ reply.content }}</p>
            <div v-if="reply.isOwner" class="comment-actions">
              <button type="button" @click="startEditing(reply)">수정</button>
              <button type="button" @click="removeComment(post.id, reply.id)">삭제</button>
            </div>
          </div>
        </article>

        <p v-if="!post.comments.length" class="empty-comments">첫 번째 댓글을 남겨보세요.</p>
      </section>
    </section>

    <section v-else class="empty-posts">
      <h1>게시글을 찾을 수 없습니다.</h1>
      <p>새로고침으로 로컬 게시글이 초기화되었거나 존재하지 않는 글입니다.</p>
      <RouterLink class="button button--primary" :to="{ name: 'home' }">목록으로 돌아가기</RouterLink>
    </section>
  </CommunityLayout>
</template>
