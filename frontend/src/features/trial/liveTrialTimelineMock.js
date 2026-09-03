import { LIVE_TRIAL_TIMING } from './liveTrialStateMock.js'

const INTRODUCTION = Object.freeze({
  phase: 'AI 판사 사건 소개',
  notice: '지금부터 양측의 입장을 바탕으로 공개 재판을 시작합니다.',
  currentLabel: '사건 소개',
  argument: Object.freeze({
    speaker: 'AI 판사',
    content:
      '이번 사건의 핵심 쟁점은 연인 사이에서 이성 친구와 단둘이 술을 마신 행동이 신뢰를 훼손했는지 여부입니다. 먼저 A측의 입장을 듣겠습니다.',
  }),
  summary: Object.freeze({
    title: 'A측 입장 발표 대기',
    content: 'A측 AI 변호사가 신뢰 훼손과 약속 위반에 대한 입장을 준비하고 있습니다.',
    progress: 10,
  }),
  waitingMessage: 'A측 AI 변호사가 입장을 준비 중입니다...',
})

const A_ARGUMENT = Object.freeze({
  phase: 'A측 입장',
  notice: 'A측 AI 변호사, 입장을 발표해 주세요.',
  currentLabel: 'A측 입장',
  argument: Object.freeze({
    speaker: 'A측 AI 변호사',
    content:
      'B측은 이성 친구와 단둘이 술을 마시지 않기로 한 연인 간의 신뢰를 명백히 저버렸습니다. 이는 A측에게 심리적 불안감을 조성한 중대한 약속 위반입니다.',
  }),
  summary: Object.freeze({
    title: 'A측 핵심 주장',
    content: '사전 합의를 어긴 행동과 뒤늦은 설명으로 인해 관계의 신뢰가 훼손되었다는 주장입니다.',
    progress: 20,
  }),
  waitingMessage: 'B측 AI 변호사가 입장을 준비 중입니다...',
})

const B_ARGUMENT = Object.freeze({
  phase: 'B측 입장',
  notice: 'B측 AI 변호사, 입장을 발표해 주세요.',
  currentLabel: 'B측 입장',
  argument: Object.freeze({
    speaker: 'B측 AI 변호사',
    content:
      '해당 만남은 오랜 친구와의 일상적인 자리였으며 A측을 기만하려는 의도는 없었습니다. 미리 알리지 못한 점은 잘못이지만 관계 전체의 신뢰를 부정할 사안은 아닙니다.',
  }),
  summary: Object.freeze({
    title: 'B측 핵심 주장',
    content: '고의적인 은폐가 아니었고 상황 설명과 사과를 통해 해결할 수 있는 갈등이라는 주장입니다.',
    progress: 30,
  }),
  waitingMessage: 'AI 판사가 양측의 쟁점을 정리 중입니다...',
})

const AGENT_DEBATE_TURNS = Object.freeze([
  Object.freeze({
    phase: 'AI 상호 변론 1/6',
    notice: '양측은 약속 위반 여부를 중심으로 반론해 주세요.',
    currentLabel: 'A측 반론',
    argument: Object.freeze({
      speaker: 'A측 AI 변호사',
      content:
        '문제는 술자리 자체만이 아니라 사전에 공유하기로 한 약속을 지키지 않은 점입니다. B측의 의도와 별개로 반복된 소통 부족이 신뢰 훼손의 직접적인 원인입니다.',
    }),
    summary: Object.freeze({
      title: '쟁점 1 · 사전 합의',
      content: '양측이 이성 친구와의 만남을 미리 공유하기로 합의했는지가 핵심입니다.',
      progress: 40,
    }),
    waitingMessage: 'B측 AI 변호사가 반론을 준비 중입니다...',
  }),
  Object.freeze({
    phase: 'AI 상호 변론 2/6',
    notice: 'B측은 사전 고지하지 못한 이유를 설명해 주세요.',
    currentLabel: 'B측 반론',
    argument: Object.freeze({
      speaker: 'B측 AI 변호사',
      content:
        '갑작스럽게 정해진 자리였고 연락할 시점을 놓친 것이지 의도적으로 숨긴 것은 아닙니다. 한 번의 판단 착오를 지속적인 기만과 동일하게 평가해서는 안 됩니다.',
    }),
    summary: Object.freeze({
      title: '쟁점 2 · 고의성',
      content: '사전 고지 누락이 의도적인 은폐였는지 단순한 판단 착오였는지를 검토합니다.',
      progress: 55,
    }),
    waitingMessage: 'AI 판사가 양측 주장 사이의 모순을 확인 중입니다...',
  }),
  Object.freeze({
    phase: 'AI 상호 변론 3/6',
    notice: '양측은 갈등 이후의 소통 태도를 설명해 주세요.',
    currentLabel: 'AI 판사 쟁점 질문',
    argument: Object.freeze({
      speaker: 'AI 판사',
      content:
        '행동 이후 B측이 충분히 설명하고 사과했는지, A측이 설명을 들을 기회를 제공했는지도 책임 비율을 판단하는 중요한 요소입니다.',
    }),
    summary: Object.freeze({
      title: '쟁점 3 · 사후 대응',
      content: '사건 이후 설명과 사과, 감정적인 대응이 갈등을 얼마나 키웠는지 확인합니다.',
      progress: 70,
    }),
    waitingMessage: 'A측 AI 변호사가 최종 의견을 정리 중입니다...',
  }),
  Object.freeze({
    phase: 'AI 상호 변론 4/6',
    notice: 'A측 AI 변호사, 최종 의견을 말씀해 주세요.',
    currentLabel: 'A측 최종 변론',
    argument: Object.freeze({
      speaker: 'A측 AI 변호사',
      content:
        'A측의 감정적 대응에 일부 아쉬움이 있더라도, 최초 원인은 합의를 지키지 않고 사실을 뒤늦게 알린 B측의 행동입니다. 관계 회복을 위해 명확한 책임 인정이 필요합니다.',
    }),
    summary: Object.freeze({
      title: 'A측 최종 요지',
      content: '신뢰 회복을 위해 B측의 약속 위반과 소통 부족에 더 큰 책임을 인정해야 한다는 입장입니다.',
      progress: 80,
    }),
    waitingMessage: 'B측 AI 변호사가 최종 의견을 정리 중입니다...',
  }),
  Object.freeze({
    phase: 'AI 상호 변론 5/6',
    notice: 'B측 AI 변호사, 최종 의견을 말씀해 주세요.',
    currentLabel: 'B측 최종 변론',
    argument: Object.freeze({
      speaker: 'B측 AI 변호사',
      content:
        'B측은 사전 공유를 놓친 책임을 인정합니다. 다만 기만 의도가 없었고 관계 회복을 위한 대화 의지가 있으므로, 일방적인 신뢰 파괴로 단정하지 않는 균형 잡힌 판단을 요청합니다.',
    }),
    summary: Object.freeze({
      title: '최종 쟁점 정리',
      content: '약속 위반의 책임과 고의성, 사건 이후 양측의 소통 태도를 종합해 배심원 투표를 진행합니다.',
      progress: 90,
    }),
    waitingMessage: 'AI 판사가 최종 쟁점을 정리 중입니다...',
  }),
  Object.freeze({
    phase: 'AI 상호 변론 6/6',
    notice: '양측 변론을 마쳤습니다. 최종 투표 전 쟁점을 정리하겠습니다.',
    currentLabel: 'AI 판사 최종 정리',
    argument: Object.freeze({
      speaker: 'AI 판사',
      content:
        '이번 재판에서는 사전 합의 위반 여부, 행동의 고의성, 사건 이후 양측의 소통 태도가 핵심 쟁점으로 확인되었습니다. 배심원은 세 쟁점을 종합해 판단해 주세요.',
    }),
    summary: Object.freeze({
      title: '배심원 최종 판단 기준',
      content: '약속 위반의 책임, 의도적인 은폐 여부, 갈등 이후 관계 회복 노력을 종합해 투표합니다.',
      progress: 100,
    }),
    waitingMessage: '곧 최종 판결 투표가 시작됩니다...',
  }),
])

export function getLiveTrialTimelineMock(remainingSeconds) {
  if (remainingSeconds === null) return INTRODUCTION

  const elapsedSeconds = Math.max(0, LIVE_TRIAL_TIMING.TOTAL_SECONDS - remainingSeconds)
  const introductionEndsAt = LIVE_TRIAL_TIMING.INTRODUCTION_SECONDS
  const aArgumentEndsAt = introductionEndsAt + LIVE_TRIAL_TIMING.ARGUMENT_SECONDS
  const bArgumentEndsAt = aArgumentEndsAt + LIVE_TRIAL_TIMING.ARGUMENT_SECONDS

  if (elapsedSeconds < introductionEndsAt) return INTRODUCTION
  if (elapsedSeconds < aArgumentEndsAt) return A_ARGUMENT
  if (elapsedSeconds < bArgumentEndsAt) return B_ARGUMENT

  const debateElapsedSeconds = elapsedSeconds - bArgumentEndsAt
  const turnDurationSeconds =
    LIVE_TRIAL_TIMING.AGENT_DEBATE_SECONDS / AGENT_DEBATE_TURNS.length
  const turnIndex = Math.min(
    AGENT_DEBATE_TURNS.length - 1,
    Math.floor(debateElapsedSeconds / turnDurationSeconds),
  )

  return AGENT_DEBATE_TURNS[turnIndex]
}
