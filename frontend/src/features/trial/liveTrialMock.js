import { LIVE_TRIAL_MOCK_SCENARIO, getLiveTrialStateMock } from './liveTrialStateMock.js'

export const liveTrialMock = Object.freeze({
  id: 1,
  state: getLiveTrialStateMock(LIVE_TRIAL_MOCK_SCENARIO.ARGUMENT),
  title: '남자친구가 여사친과 단둘이 술을 마신 사건',
  viewCount: 142,
  sessionTime: '총 재판 시간 3분',
  audienceCount: 1284,
  participants: [
    { id: 'a-attorney', name: 'A측 AI 변호사', role: 'A측', position: 'left', tone: 'navy' },
    { id: 'judge', name: 'AI 판사', role: '재판 진행', position: 'center', tone: 'judge' },
    { id: 'b-attorney', name: 'B측 AI 변호사', role: 'B측', position: 'right', tone: 'blue' },
  ],
  judgeNotice: 'A측 AI 변호사, 최후 변론을 시작하세요.',
  currentArgument: {
    speaker: 'AI 변호사 A (A측)',
    content:
      '존경하는 배심원 여러분, B측은 이성 친구와 단둘이 술을 마시지 않기로 한 연인 간의 신뢰를 명백히 저버렸습니다. 이는 단순한 우정의 범위를 넘어 A측에게 심리적 불안감을 조성한 중대한 약속 위반입니다.',
  },
  nextSummary: {
    title: 'B측 주장 요약 대기',
    content: '단순한 친구이었으며 미리 말하려 했으나 상황상 타이밍을 놓쳤을 뿐, 기만할 의도는 없었다.',
    progress: 33,
  },
  messages: [
    { id: 1, avatar: '배', nickname: '배심원_882', message: '단둘이는 선 넘었지;;', tone: 'blue' },
    { id: 2, avatar: '박', nickname: '박_33', message: '근데 미리 말하려고 했다는 게 진짜일까?', tone: 'violet' },
    { id: 3, avatar: '연', nickname: '연애고수', message: '변호사 A 말빨 장난 아니네 🔥', tone: 'coral' },
    { id: 4, avatar: '김', nickname: '김_J', message: '판결 어떻게 나올지 너무 궁금하다', tone: 'sky' },
  ],
})
