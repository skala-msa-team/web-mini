import { httpClient } from '@/api/httpClient.js'
import { requestData } from '@/api/apiResponse.js'

export const trialApi = Object.freeze({
  getTrials(params = {}) {
    return requestData(httpClient.get('/trials', { params }))
  },

  getTrial(trialId) {
    return requestData(httpClient.get(`/trials/${trialId}`))
  },

  saveStatement(trialId, side, payload) {
    return requestData(httpClient.put(`/trials/${trialId}/parties/${side}/statement`, payload))
  },

  createGuideQuestions(trialId, side) {
    return requestData(httpClient.post(`/trials/${trialId}/parties/${side}/guide-questions`))
  },

  saveGuideAnswers(trialId, side, payload) {
    return requestData(httpClient.put(`/trials/${trialId}/parties/${side}/guide-answers`, payload))
  },

  createArgumentDraft(trialId, side) {
    return requestData(httpClient.post(`/trials/${trialId}/parties/${side}/argument-draft`))
  },

  updateArgumentDraft(trialId, side, payload) {
    return requestData(httpClient.put(`/trials/${trialId}/parties/${side}/argument-draft`, payload))
  },

  confirmArgument(trialId, side) {
    return requestData(httpClient.post(`/trials/${trialId}/parties/${side}/confirm`))
  },

  startTrial(trialId) {
    return requestData(httpClient.post(`/trials/${trialId}/start`))
  },

  getSnapshot(trialId) {
    return requestData(httpClient.get(`/trials/${trialId}/snapshot`))
  },

  getEvents(trialId, afterSequence = 0) {
    return requestData(httpClient.get(`/trials/${trialId}/events`, {
      params: { afterSequence },
    }))
  },

  getMessages(trialId, afterSequence = 0, size = 100) {
    return requestData(httpClient.get(`/trials/${trialId}/messages`, {
      params: { afterSequence, size },
    }))
  },

  submitVote(trialId, selectedSide) {
    return requestData(httpClient.post(`/trials/${trialId}/votes`, { selectedSide }))
  },

  getResults(trialId) {
    return requestData(httpClient.get(`/trials/${trialId}/results`))
  },
})
