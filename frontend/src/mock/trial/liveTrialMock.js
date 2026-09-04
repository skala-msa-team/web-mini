import { LIVE_TRIAL_MOCK_SCENARIO, getLiveTrialStateMock } from './liveTrialStateMock.js'

export const liveTrialMock = Object.freeze({
  id: 1,
  state: getLiveTrialStateMock(LIVE_TRIAL_MOCK_SCENARIO.ARGUMENT),
  title: '남자친구가 여사친과 단둘이 술을 마신 사건',
  viewCount: 142,
  sessionTime: '총 재판 시간 3분',
  audienceCount: 1284,
  participants: [
    {
      id: 'a-attorney',
      name: 'A측 AI 변호사',
      role: 'A측',
      position: 'left',
      tone: 'navy',
      speakerKey: 'A_LAWYER',
      avatar: 'A',
      avatarUrl: '/images/trial-portraits-pixel.png',
    },
    {
      id: 'judge',
      name: 'AI 판사',
      role: '재판 진행',
      position: 'center',
      tone: 'judge',
      speakerKey: 'JUDGE',
      avatar: 'AI',
      avatarUrl: '/images/trial-portraits-pixel.png',
    },
    {
      id: 'b-attorney',
      name: 'B측 AI 변호사',
      role: 'B측',
      position: 'right',
      tone: 'blue',
      speakerKey: 'B_LAWYER',
      avatar: 'B',
      avatarUrl: '/images/trial-portraits-pixel.png',
    },
  ],
  messages: [
    { id: 1, avatar: '배', nickname: '배심원_882', message: '단둘이는 선 넘었지;;', tone: 'blue' },
    { id: 2, avatar: '박', nickname: '박_33', message: '근데 미리 말하려고 했다는 게 진짜일까?', tone: 'violet' },
    { id: 3, avatar: '연', nickname: '연애고수', message: '변호사 A 말빨 장난 아니네 🔥', tone: 'coral' },
    { id: 4, avatar: '김', nickname: '김_J', message: '판결 어떻게 나올지 너무 궁금하다', tone: 'sky' },
  ],
})
