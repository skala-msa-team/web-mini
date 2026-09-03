import {
  LIVE_TRIAL_MOCK_SCENARIO,
  getLiveTrialStateMock,
} from '@/mock/trial/liveTrialStateMock.js'

export const finalVoteMock = Object.freeze({
  state: getLiveTrialStateMock(LIVE_TRIAL_MOCK_SCENARIO.VOTE_OPEN),
  caseNumber: 402,
  title: '연락 빈도를 둘러싼 갈등',
  subtitle: 'A측: 김지민 (연락 부족) vs B측: 이준호 (개인 시간 존중)',
  viewerCount: 12402,
  choices: [
    {
      id: 'SIDE_A',
      title: 'A측 승소',
      description: '김지민의 주장이 옳다',
      side: 'A측',
    },
    {
      id: 'SIDE_B',
      title: 'B측 승소',
      description: '이준호의 주장이 옳다',
      side: 'B측',
    },
  ],
  messages: [
    {
      id: 101,
      avatar: 'B',
      nickname: 'Bystander99',
      badge: 'A측 지지',
      message: '연락이 너무 없으면 당연히 서운하죠. 이건 A측 입장이 이해가 갑니다.',
      tone: 'navy',
    },
    {
      id: 102,
      avatar: 'L',
      nickname: 'LogicUser',
      badge: 'B측 지지',
      message: '업무 중인데 매번 어떻게 답장을 하나요. 개인 시간도 중요합니다.',
      tone: 'sky',
    },
    {
      id: 103,
      avatar: '나',
      nickname: '나',
      badge: '과실 조정',
      message: '둘 다 이해가 가는데… 합의점을 찾는 게 맞을 듯.',
      tone: 'blue',
    },
  ],
})
