import { reactive } from 'vue'
import { communityPosts } from '@/mock/community/communityData.js'

const relationshipLabels = Object.freeze({
  COUPLE: '연인',
  SOME: '썸',
  SPOUSE: '부부',
})

const initialComments = [
  {
    id: 1,
    author: '연애고수',
    content: '상대가 편한 시간과 연락 빈도를 먼저 솔직하게 물어보는 게 좋을 것 같아요.',
    createdAt: '5분 전',
    isOwner: false,
    replies: [
      {
        id: 2,
        author: '나',
        content: '기준을 직접 확인해보는 게 가장 정확하겠네요.',
        createdAt: '방금 전',
        isOwner: true,
      },
    ],
  },
]

const state = reactive({
  posts: communityPosts.map((post, index) => ({
    ...post,
    content: post.summary,
    author: index === 0 ? '고민중인사람' : '익명 배심원',
    liked: false,
    reported: false,
    comments: index === 0 ? structuredClone(initialComments) : [],
  })),
})

function findPost(postId) {
  return state.posts.find((post) => String(post.id) === String(postId))
}

function addPost({ id, backendPostId, category, relationshipType, title, content }) {
  const post = {
    id,
    backendPostId,
    category,
    relationshipType: relationshipLabels[relationshipType] ?? relationshipType,
    title,
    summary: content,
    content,
    author: '나',
    createdAt: '방금 전',
    likeCount: 0,
    commentCount: 0,
    isLive: false,
    liked: false,
    reported: false,
    isOwner: true,
    comments: [],
  }

  state.posts.unshift(post)
  return post
}

function toggleLike(postId) {
  const post = findPost(postId)
  if (!post) return

  post.liked = !post.liked
  post.likeCount += post.liked ? 1 : -1
}

function toggleReport(postId) {
  const post = findPost(postId)
  if (post) post.reported = !post.reported
}

function addComment(postId, content, parentId = null) {
  const post = findPost(postId)
  if (!post) return

  const comment = {
    id: crypto.randomUUID(),
    author: '나',
    content,
    createdAt: '방금 전',
    isOwner: true,
    replies: [],
  }

  if (parentId) {
    const parent = post.comments.find((item) => String(item.id) === String(parentId))
    parent?.replies.push(comment)
  } else {
    post.comments.push(comment)
  }
  post.commentCount += 1
}

function updateComment(postId, commentId, content) {
  const comment = findComment(findPost(postId), commentId)
  if (comment?.isOwner) comment.content = content
}

function removeComment(postId, commentId) {
  const post = findPost(postId)
  if (!post) return

  const rootIndex = post.comments.findIndex((comment) => String(comment.id) === String(commentId))
  if (rootIndex >= 0 && post.comments[rootIndex].isOwner) {
    const removedCount = 1 + post.comments[rootIndex].replies.length
    post.comments.splice(rootIndex, 1)
    post.commentCount = Math.max(0, post.commentCount - removedCount)
    return
  }

  for (const comment of post.comments) {
    const replyIndex = comment.replies.findIndex((reply) => String(reply.id) === String(commentId))
    if (replyIndex >= 0 && comment.replies[replyIndex].isOwner) {
      comment.replies.splice(replyIndex, 1)
      post.commentCount = Math.max(0, post.commentCount - 1)
      return
    }
  }
}

function findComment(post, commentId) {
  if (!post) return null

  for (const comment of post.comments) {
    if (String(comment.id) === String(commentId)) return comment
    const reply = comment.replies.find((item) => String(item.id) === String(commentId))
    if (reply) return reply
  }
  return null
}

export function useCommunityStore() {
  return {
    state,
    findPost,
    addPost,
    toggleLike,
    toggleReport,
    addComment,
    updateComment,
    removeComment,
  }
}
