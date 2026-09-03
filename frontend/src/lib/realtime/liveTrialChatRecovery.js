import { trialApi } from '@/api/trialApi.js'
import { mergeMessages } from '@/utils/messageMerge.js'

const MESSAGE_PAGE_SIZE = 100

export async function recoverLiveTrialChat({
  trialId,
  afterMessageSequence = 0,
  pageSize = MESSAGE_PAGE_SIZE,
}) {
  let cursor = afterMessageSequence
  let messages = []
  let hasMore = true

  while (hasMore) {
    const response = await trialApi.getMessages(trialId, cursor, pageSize)
    const items = Array.isArray(response?.items) ? response.items : []
    messages = mergeMessages(messages, items)
    hasMore = response?.hasMore === true

    const lastSequence = items.reduce(
      (latest, message) => Math.max(latest, Number(message.messageSequence) || 0),
      cursor,
    )

    if (lastSequence <= cursor) break
    cursor = lastSequence
  }

  return {
    messages,
    latestMessageSequence: cursor,
  }
}
