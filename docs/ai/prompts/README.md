# AI Prompt

Mock AI와 향후 실제 AI Adapter가 동일한 역할과 입출력 계약을 따르기 위한 Prompt 문서입니다.

이 문서는 Prompt의 역할과 처리 규칙을 정의합니다. HTTP Endpoint와 JSON Schema의 기준은 Notion의 Demo API 명세서이며, 이 문서에서 새로운 API 필드를 정의하지 않습니다.

## Prompt 목록

| Prompt | 문서 | Endpoint | Prompt Version |
| --- | --- | --- | --- |
| AI 변호사 안내 질문 | [lawyer-questions.md](lawyer-questions.md) | `POST /mock-ai/lawyer/questions` | `lawyer-questions-v1` |
| AI 변호사 사실 요약·1차 변론 | [lawyer-argument.md](lawyer-argument.md) | `POST /mock-ai/lawyer/argument` | `lawyer-argument-v1` |
| AI 판사 판결 | [judge-verdict.md](judge-verdict.md) | `POST /mock-ai/judge/verdict` | `judge-v1` |

`lawyer-questions-v1`과 `lawyer-argument-v1`은 Prompt 문서의 변경 이력을 식별하는 버전입니다. 현재 변호사 응답 JSON Schema에는 `promptVersion`이 없으므로 응답에 이 값을 추가하지 않습니다. 판결 Prompt는 요청과 응답 모두 확정 계약에 따라 `promptVersion`을 사용합니다.

## 공통 원칙

- 서비스는 관계 갈등을 정리하기 위한 Demo이며 실제 법률 상담이나 법적 판결을 제공하지 않습니다.
- 입력에 없는 사실, 의도, 감정, 날짜와 행동을 만들어 내지 않습니다.
- A측과 B측을 입력의 `side` 또는 `arguments.A`, `arguments.B`로 구분하며 서로 뒤바꾸지 않습니다.
- 성별, 연령, 직업, 경제력이나 사회적 지위를 근거 없이 추정하지 않습니다.
- 모욕, 위협, 차별 표현을 확대하거나 한쪽에 대한 공격을 유도하지 않습니다.
- Markdown, 설명, 코드 펜스 없이 JSON Object 하나만 반환합니다.
- 모든 응답의 `schemaVersion`은 `"1.0"`입니다.
- Backend는 응답 Schema 검증 실패 시 한 번만 재호출하며, 다시 실패하면 `422 MOCK_AI_RESPONSE_INVALID`로 처리합니다.
- Demo는 추가 질문과 휴정 없이 정해진 준비·변론·투표·판결 단계로 진행합니다.

## 계약 기준

- [GitHub Issue #198](https://github.com/skala-msa-team/web-mini/issues/198)
- [Demo API 명세서](https://confused-dietician-c17.notion.site/Demo-API-3d07caa087bd8171aaa1fa90ba18db5f)
- [Demo Mock AI API](https://confused-dietician-c17.notion.site/036d42e6bfbe4c668125dfffdf684bdd)
