export function formatDate(value) {
  return new Intl.DateTimeFormat('ko-KR').format(new Date(value))
}
