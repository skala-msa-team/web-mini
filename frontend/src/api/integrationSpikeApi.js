import axios from 'axios'

export async function fetchTrialList({ page = 0, size = 10 } = {}) {
  const response = await axios.get('/api/v1/trials', { params: { page, size } })
  return response.data
}
