export async function requestData(request) {
  const responseBody = await request
  return responseBody?.data
}
