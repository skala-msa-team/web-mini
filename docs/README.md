# Docs

팀에서 확정해 Git으로 버전 관리해야 하는 설계 문서와 Notion에서 사용하는 정적 자산을 보관합니다.

## 문서 책임

- 기획, 일정, 회의록과 R&R의 최신 기준: Notion
- 담당자, Priority, Iteration과 상태: GitHub Project
- 공용 실행·검증 방법과 Git 협업 규칙: 루트 `README.md`
- 파트별 코드 작성 규칙: `frontend/README.md`, `backend/README.md`
- 확정된 기술 계약의 버전 기록: `docs/`

Architecture, ERD, API 명세, AI Prompt/JSON Schema와 QA 결과는 팀 Review로 내용이 확정된 뒤 별도 Issue에서 추가합니다. 빈 문서나 추정한 계약은 만들지 않습니다.

## 문서 컨벤션

| 대상 | 규칙 | 예시 |
| --- | --- | --- |
| 문서 파일 | kebab-case Markdown | `api-spec.md` |
| 이미지·정적 자산 | kebab-case | `pull-request-template.jpg` |
| API ID·이벤트명 | 승인된 명세의 이름 유지 | `TRIAL_STARTED` |
| 미확정 항목 | `미정`으로 표기 | `인증 방식: 미정` |

- 하나의 문서는 하나의 책임만 가집니다.
- 구현 완료, 검증 완료, 데모 완료는 실제 코드와 실행 결과가 있을 때만 작성합니다.
- API, Database, STOMP, AI 계약 예시는 승인된 명세와 현재 구현을 함께 확인한 뒤 갱신합니다.
- Notion이나 GitHub Project에 있어야 하는 일정, 담당자, 진행 상태를 `docs/`에 중복 기록하지 않습니다.
- Notion에서 Raw URL로 참조하는 `assets/github-guide/` 파일은 이름과 경로를 임의로 바꾸지 않습니다.

## AI Prompt

- [AI Prompt 목록](ai/prompts/README.md)

## 설계 문서

- [User Flow](user-flow.md)
- [프로젝트 한계점 및 추후 AI 실제 결합 로드맵](ai-roadmap.md)

## GitHub 가이드 이미지

`assets/github-guide/`의 이미지는 Notion의 GitHub Issue · PR 사용 가이드에서 `main` Branch의 Raw URL로 사용합니다. 이미지 이름이나 경로를 바꾸거나 삭제할 때는 Notion의 연결 주소도 같은 작업에서 갱신하고 확인해야 합니다.
