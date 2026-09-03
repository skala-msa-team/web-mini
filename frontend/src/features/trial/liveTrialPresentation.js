import { TRIAL_STATUS, TRIAL_STATUS_LABEL } from "@/constants/trialStatus.js";

const WAITING_MESSAGE = Object.freeze({
  [TRIAL_STATUS.PREPARING]: "재판 시작을 기다리고 있습니다.",
  [TRIAL_STATUS.INTRODUCTION]: "AI 판사가 사건을 소개하고 있습니다.",
  [TRIAL_STATUS.A_ARGUMENT]: "A측 AI 변호사의 발언이 진행 중입니다.",
  [TRIAL_STATUS.B_ARGUMENT]: "B측 AI 변호사의 발언이 진행 중입니다.",
  [TRIAL_STATUS.DEBATE]: "A측과 B측 AI 변호사가 상호 변론을 진행 중입니다.",
  [TRIAL_STATUS.VOTING]: "배심원 최종 투표가 진행 중입니다.",
  [TRIAL_STATUS.VERDICT]: "AI 판사가 최종 판결을 준비하고 있습니다.",
  [TRIAL_STATUS.ENDED]: "재판이 종료되었습니다.",
});

export function getTrialPhaseLabel(status) {
  return TRIAL_STATUS_LABEL[status] || "상태 복원 중";
}

export function getTrialWaitingMessage(status) {
  return WAITING_MESSAGE[status] || "현재 재판 상태를 불러오고 있습니다.";
}
