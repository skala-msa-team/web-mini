function toSequence(value) {
  const sequence = Number(value)
  return Number.isInteger(sequence) && sequence > 0 ? sequence : null
}

export function normalizeChatMessage(message = {}) {
  return {
    messageId: message.messageId ?? message.id ?? null,
    messageSequence: toSequence(message.messageSequence),
    trialId: message.trialId ?? null,
    sender: {
      demoUserId: message.sender?.demoUserId ?? null,
      nickname: message.sender?.nickname ?? message.nickname ?? '관전자',
    },
    content: message.content ?? message.message ?? '',
    createdAt: message.createdAt ?? null,
  }
}

function messageKey(message) {
  if (message.messageId !== null) return `id:${message.messageId}`
  if (message.messageSequence !== null) return `sequence:${message.messageSequence}`
  return null
}

function mergeMessage(previous, next) {
  return {
    ...previous,
    ...next,
    messageId: next.messageId ?? previous.messageId,
    messageSequence: next.messageSequence ?? previous.messageSequence,
    trialId: next.trialId ?? previous.trialId,
    sender: {
      ...previous.sender,
      ...next.sender,
      demoUserId: next.sender.demoUserId ?? previous.sender.demoUserId,
      nickname: next.sender.nickname || previous.sender.nickname,
    },
    content: next.content || previous.content,
    createdAt: next.createdAt || previous.createdAt,
  }
}

export function mergeMessages(previousMessages = [], nextMessages = []) {
  const uniqueMessages = new Map()

  for (const rawMessage of [...previousMessages, ...nextMessages]) {
    const message = normalizeChatMessage(rawMessage)
    const key = messageKey(message)
    if (!key) continue

    const previous = uniqueMessages.get(key)
    uniqueMessages.set(key, previous ? mergeMessage(previous, message) : message)
  }

  return [...uniqueMessages.values()].sort((left, right) => {
    if (left.messageSequence === null) return 1
    if (right.messageSequence === null) return -1
    return left.messageSequence - right.messageSequence
  })
}

export function getLastContiguousMessageSequence(messages = []) {
  let cursor = 0

  for (const message of mergeMessages([], messages)) {
    const sequence = message.messageSequence ?? 0
    if (sequence <= cursor) continue
    if (sequence !== cursor + 1) break
    cursor = sequence
  }

  return cursor
}
