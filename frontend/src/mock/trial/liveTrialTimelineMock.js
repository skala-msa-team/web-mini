import { LIVE_TRIAL_TIMING } from "./liveTrialStateMock.js";

const INTRODUCTION = Object.freeze({
  phase: "AI 판사 사건 소개",
  notice: "지금부터 양측의 입장을 바탕으로 공개 재판을 시작합니다.",
  currentLabel: "사건 소개",
  argument: Object.freeze({
    speaker: "AI 판사",
    content:
      "이번 사건의 핵심 쟁점은 친구의 '여우짓'과 남자친구의 미온적 반응이 관계의 경계와 신뢰에 어떤 영향을 미쳤는지입니다. 먼저 A측의 입장을 듣겠습니다.",
  }),
  summary: Object.freeze({
    title: "A측 입장 발표 대기",
    content:
      "A측 AI 변호사가 신뢰 훼손과 약속 위반에 대한 입장을 준비하고 있습니다.",
    progress: 10,
  }),
  waitingMessage: "A측 AI 변호사가 입장을 준비 중입니다...",
});

const A_ARGUMENT = Object.freeze({
  phase: "A측 입장",
  notice: "A측 AI 변호사, 입장을 발표해 주세요.",
  currentLabel: "A측 입장",
  argument: Object.freeze({
    speaker: "A측 AI 변호사",
    content:
      '며칠 전 A, B, 그리고 A의 친구가 함께 있던 술자리에서 친구가 "오빠가 아깝다~"라며 계속 장난스럽게 배배꼬고 선을 넘는 행동을 보였습니다. B(남자친구)는 그 자리에서 별다른 제지 없이 허허 웃으며 넘겼고, 이후 A가 불편함을 표현했을 때에도 "에이 그냥 한 소리지"라며 가볍게 넘겼습니다. A는 B의 미온적 태도가 선을 긋지 못하는 것으로 느껴 불편합니다.',
  }),
  summary: Object.freeze({
    title: "A측 핵심 주장",
    content:
      "친구의 선 넘는 행동과 남자친구의 선을 긋지 않는 태도가 갈등의 핵심 원인이라는 주장입니다.",
    progress: 20,
  }),
  waitingMessage: "B측 AI 변호사가 입장을 준비 중입니다...",
});

const B_ARGUMENT = Object.freeze({
  phase: "B측 입장",
  notice: "B측 AI 변호사, 입장을 발표해 주세요.",
  currentLabel: "B측 입장",
  argument: Object.freeze({
    speaker: "B측 AI 변호사",
    content:
      "해당 만남은 오랜 친구와의 가벼운 자리였고, 친구의 발언도 가볍게 받아들여질 수 있는 농담이었습니다. B는 그 자리에서 대화를 심각하게 받아들이지 않았고 고의적인 선 넘음이나 은폐 의도는 없었다고 주장합니다. 사후 설명과 대화를 통해 해결할 수 있다는 입장입니다.",
  }),
  summary: Object.freeze({
    title: "B측 핵심 주장",
    content:
      "의도적 기만이 아니며 사후 해명과 사과로 관계 회복이 가능하다는 주장입니다.",
    progress: 30,
  }),
  waitingMessage: "AI 판사가 양측의 쟁점을 정리 중입니다...",
});

const AGENT_DEBATE_TURNS = Object.freeze([
  Object.freeze({
    phase: "AI 상호 변론 1/6",
    notice: "양측은 약속 위반 여부를 중심으로 반론해 주세요.",
    currentLabel: "A측 반론",
    argument: Object.freeze({
      speaker: "A측 AI 변호사",
      content:
        "문제는 친구의 선 넘는 행동과 B측의 방관입니다. 사전에 경계와 연락 기준을 합의했는지가 중요합니다.",
    }),
    summary: Object.freeze({
      title: "쟁점 1 · 사전 합의",
      content:
        "양측이 친구의 행동에 대한 경계와 연락 기준을 사전에 합의했는지가 핵심입니다.",
      progress: 40,
    }),
    waitingMessage: "B측 AI 변호사가 반론을 준비 중입니다...",
  }),
  Object.freeze({
    phase: "AI 상호 변론 2/6",
    notice: "B측은 사전 고지하지 못한 이유를 설명해 주세요.",
    currentLabel: "B측 반론",
    argument: Object.freeze({
      speaker: "B측 AI 변호사",
      content:
        "자리는 갑자기 정해졌고, B측에게 은폐 의도는 없었습니다. 한 번의 실수로 단정해선 안 됩니다.",
    }),
    summary: Object.freeze({
      title: "쟁점 2 · 고의성",
      content:
        "사건 당시 발언의 의도와 B의 인지 여부, 고의성이 있었는지를 검토합니다.",
      progress: 55,
    }),
    waitingMessage: "AI 판사가 양측 주장 사이의 모순을 확인 중입니다...",
  }),
  Object.freeze({
    phase: "AI 상호 변론 3/6",
    notice: "양측은 갈등 이후의 소통 태도를 설명해 주세요.",
    currentLabel: "AI 판사 쟁점 질문",
    argument: Object.freeze({
      speaker: "AI 판사",
      content:
        "갈등 후 B측이 충분히 설명하고 사과했는지도 책임 판단의 중요한 기준입니다.",
    }),
    summary: Object.freeze({
      title: "쟁점 3 · 사후 대응",
      content:
        "사건 이후의 설명과 사과, 그리고 B의 문제 인지 및 태도 변화가 갈등 해결에 어느 정도 기여했는지를 확인합니다.",
      progress: 70,
    }),
    waitingMessage: "A측 AI 변호사가 최종 의견을 정리 중입니다...",
  }),
  Object.freeze({
    phase: "AI 상호 변론 4/6",
    notice: "A측 AI 변호사, 최종 의견을 말씀해 주세요.",
    currentLabel: "A측 최종 변론",
    argument: Object.freeze({
      speaker: "A측 AI 변호사",
      content:
        "최초 원인은 경계 위반과 B측의 미온적 대응입니다. 책임을 인정하고 명확한 기준을 세워야 합니다.",
    }),
    summary: Object.freeze({
      title: "A측 최종 요지",
      content:
        "신뢰 회복을 위해 B측의 방관과 약속 미준수에 대한 책임 인정과 경계 재설정이 필요하다는 입장입니다.",
      progress: 80,
    }),
    waitingMessage: "B측 AI 변호사가 최종 의견을 정리 중입니다...",
  }),
  Object.freeze({
    phase: "AI 상호 변론 5/6",
    notice: "B측 AI 변호사, 최종 의견을 말씀해 주세요.",
    currentLabel: "B측 최종 변론",
    argument: Object.freeze({
      speaker: "B측 AI 변호사",
      content:
        "사전 공유를 놓친 점은 인정합니다. 다만 고의적 기만은 아니었고, 대화로 관계를 회복할 의지가 있습니다.",
    }),
    summary: Object.freeze({
      title: "최종 쟁점 정리",
      content:
        "약속 위반, 고의성 여부, 사건 이후의 소통 태도와 책임 인지를 종합해 배심원 투표로 판단합니다.",
      progress: 90,
    }),
    waitingMessage: "AI 판사가 최종 쟁점을 정리 중입니다...",
  }),
  Object.freeze({
    phase: "AI 상호 변론 6/6",
    notice: "양측 변론을 마쳤습니다. 최종 투표 전 쟁점을 정리하겠습니다.",
    currentLabel: "AI 판사 최종 정리",
    argument: Object.freeze({
      speaker: "AI 판사",
      content:
        "사전 합의, 고의성, 갈등 후 소통 태도를 종합해 판단해 주세요.",
    }),
    summary: Object.freeze({
      title: "배심원 최종 판단 기준",
      content:
        "약속 위반의 책임, 의도적인 은폐 여부, 갈등 이후 관계 회복 노력을 종합해 투표합니다.",
      progress: 100,
    }),
    waitingMessage: "곧 최종 판결 투표가 시작됩니다...",
  }),
]);

export function getLiveTrialTimelineMock(remainingSeconds) {
  if (remainingSeconds === null) return INTRODUCTION;

  const elapsedSeconds = Math.max(
    0,
    LIVE_TRIAL_TIMING.TOTAL_SECONDS - remainingSeconds,
  );
  const introductionEndsAt = LIVE_TRIAL_TIMING.INTRODUCTION_SECONDS;
  const aArgumentEndsAt =
    introductionEndsAt + LIVE_TRIAL_TIMING.ARGUMENT_SECONDS;
  const bArgumentEndsAt = aArgumentEndsAt + LIVE_TRIAL_TIMING.ARGUMENT_SECONDS;

  if (elapsedSeconds < introductionEndsAt) return INTRODUCTION;
  if (elapsedSeconds < aArgumentEndsAt) return A_ARGUMENT;
  if (elapsedSeconds < bArgumentEndsAt) return B_ARGUMENT;

  const debateElapsedSeconds = elapsedSeconds - bArgumentEndsAt;
  const turnDurationSeconds =
    LIVE_TRIAL_TIMING.AGENT_DEBATE_SECONDS / AGENT_DEBATE_TURNS.length;
  const turnIndex = Math.min(
    AGENT_DEBATE_TURNS.length - 1,
    Math.floor(debateElapsedSeconds / turnDurationSeconds),
  );

  return AGENT_DEBATE_TURNS[turnIndex];
}
