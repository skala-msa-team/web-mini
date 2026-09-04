import { CHAT_STATUS, CONNECTION_STATUS, VOTE_STATUS } from '@/consts/liveTrialUiStatus.js'
import { TRIAL_STATUS } from '@/consts/trialStatus.js'

export const LIVE_TRIAL_TIMING = Object.freeze({
  INTRODUCTION_SECONDS: 7,
  ARGUMENT_SECONDS: 10,
  AGENT_DEBATE_SECONDS: 80,
  VOTING_SECONDS: 15,
  TOTAL_SECONDS: 7 + 10 + 10 + 80 + 15,
})

export const LIVE_TRIAL_MOCK_SCENARIO = Object.freeze({
  CONNECTING: 'connecting',
  ARGUMENT: 'argument',
  RECONNECTING: 'reconnecting',
  ERROR: 'error',
  VOTE_WAITING: 'vote-waiting',
  VOTE_OPEN: 'vote-open',
  VOTE_SUBMITTED: 'vote-submitted',
  ENDED: 'ended',
})

const mockTrialStartedAt = new Date()
const mockTrialEndsAt = new Date(
  mockTrialStartedAt.getTime() + LIVE_TRIAL_TIMING.TOTAL_SECONDS * 1000,
)

const BASE_SNAPSHOT = Object.freeze({
  trialId: 1,
  status: TRIAL_STATUS.A_ARGUMENT,
  phaseStartedAt: mockTrialStartedAt.toISOString(),
  phaseEndsAt: mockTrialEndsAt.toISOString(),
  scheduledEndAt: mockTrialEndsAt.toISOString(),
  latestEventSequence: 4,
  latestMessageSequence: 4,
  voteOpen: false,
  ended: false,
})

function createSnapshot(overrides = {}) {
  return Object.freeze({ ...BASE_SNAPSHOT, ...overrides })
}

function createState({
  connectionStatus = CONNECTION_STATUS.CONNECTED,
  snapshot = createSnapshot(),
  chatStatus = CHAT_STATUS.READY,
  voteStatus = VOTE_STATUS.WAITING,
  selectedSide = null,
  votedAt = null,
  error = null,
} = {}) {
  return Object.freeze({
    connection: Object.freeze({
      status: connectionStatus,
      error: error ? Object.freeze({ ...error }) : null,
    }),
    snapshot,
    chat: Object.freeze({ status: chatStatus }),
    vote: Object.freeze({
      status: voteStatus,
      selectedSide,
      votedAt,
    }),
  })
}

export const LIVE_TRIAL_STATE_MOCKS = Object.freeze({
  [LIVE_TRIAL_MOCK_SCENARIO.CONNECTING]: createState({
    connectionStatus: CONNECTION_STATUS.CONNECTING,
    snapshot: null,
    chatStatus: CHAT_STATUS.LOADING,
  }),
  [LIVE_TRIAL_MOCK_SCENARIO.ARGUMENT]: createState(),
  [LIVE_TRIAL_MOCK_SCENARIO.RECONNECTING]: createState({
    connectionStatus: CONNECTION_STATUS.RECONNECTING,
    chatStatus: CHAT_STATUS.LOADING,
  }),
  [LIVE_TRIAL_MOCK_SCENARIO.ERROR]: createState({
    connectionStatus: CONNECTION_STATUS.ERROR,
    chatStatus: CHAT_STATUS.ERROR,
    error: {
      code: 'CONNECTION_LOST',
      message: '재판 연결이 끊어졌습니다. 잠시 후 다시 시도해 주세요.',
    },
  }),
  [LIVE_TRIAL_MOCK_SCENARIO.VOTE_WAITING]: createState({
    snapshot: createSnapshot({
      status: TRIAL_STATUS.B_ARGUMENT,
      latestEventSequence: 6,
    }),
  }),
  [LIVE_TRIAL_MOCK_SCENARIO.VOTE_OPEN]: createState({
    snapshot: createSnapshot({
      status: TRIAL_STATUS.VOTING,
      latestEventSequence: 7,
      voteOpen: true,
    }),
    voteStatus: VOTE_STATUS.OPEN,
  }),
  [LIVE_TRIAL_MOCK_SCENARIO.VOTE_SUBMITTED]: createState({
    snapshot: createSnapshot({
      status: TRIAL_STATUS.VOTING,
      latestEventSequence: 7,
      voteOpen: true,
    }),
    voteStatus: VOTE_STATUS.SUBMITTED,
    selectedSide: 'A',
    votedAt: mockTrialStartedAt.toISOString(),
  }),
  [LIVE_TRIAL_MOCK_SCENARIO.ENDED]: createState({
    snapshot: createSnapshot({
      status: TRIAL_STATUS.ENDED,
      phaseStartedAt: mockTrialStartedAt.toISOString(),
      phaseEndsAt: null,
      latestEventSequence: 10,
      voteOpen: false,
      ended: true,
    }),
    chatStatus: CHAT_STATUS.CLOSED,
    voteStatus: VOTE_STATUS.SUBMITTED,
    selectedSide: 'A',
    votedAt: mockTrialStartedAt.toISOString(),
  }),
})

export const DEFAULT_LIVE_TRIAL_MOCK_SCENARIO = LIVE_TRIAL_MOCK_SCENARIO.ARGUMENT

export function getLiveTrialStateMock(scenario = DEFAULT_LIVE_TRIAL_MOCK_SCENARIO) {
  return LIVE_TRIAL_STATE_MOCKS[scenario] ?? LIVE_TRIAL_STATE_MOCKS[DEFAULT_LIVE_TRIAL_MOCK_SCENARIO]
}
