export function mergeMessages(previousMessages, nextMessages) {
  const messages = [...previousMessages, ...nextMessages]
  const uniqueMessages = new Map(messages.map((message) => [message.id, message]))

  return [...uniqueMessages.values()]
}
