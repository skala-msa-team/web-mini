import {
  LIVE_TRIAL_MOCK_SCENARIO,
  getLiveTrialStateMock,
} from '@/features/trial/liveTrialStateMock.js'

export const trialResultMock = Object.freeze({
  state: getLiveTrialStateMock(LIVE_TRIAL_MOCK_SCENARIO.ENDED),
  caseNumber: '2026-LV-0145',
  title: '연락 빈도 문제로 인한 감정적 소모 및 신뢰 훼손 건',
  winner: 'B측',
  faultRatio: {
    sideA: 30,
    sideB: 70,
    sideAReason: '감정적 대응',
    sideBReason: '소통 회피',
  },
  grounds: [
    {
      title: '소통 의무의 중대한 해태',
      side: 'B측 70%',
      description:
        '연인 관계의 기본 전제인 상호 소통을 합리적 이유 없이 지속적으로 회피하여 상대방의 불안을 가중시켰습니다. 이는 신뢰 관계를 심각하게 훼손하는 행위로 인정됩니다.',
    },
    {
      title: '과도한 감정적 표현을 통한 상황 악화',
      side: 'A측 30%',
      description:
        'B측의 회피가 원인이었으나 문제 해결 과정에서 이성적 대화보다 감정적 비난을 앞세워 갈등을 심화시킨 책임이 일부 인정됩니다.',
    },
  ],
  judgment:
    'B측은 A측에게 진심 어린 사과문을 작성하고, 향후 갈등 발생 시 최소 24시간 이내에 소통을 재개할 것을 권고합니다.',
  aiResult: {
    sideA: 30,
    sideB: 70,
  },
  juryResult: {
    sideA: 65,
    sideB: 35,
    participantCount: 1420,
  },
})
