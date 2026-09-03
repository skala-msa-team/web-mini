# 사랑과 전쟁터

SKALA Full-Stack Engineering 과정의 6조 AI-Ready 웹 서비스 설계 프로젝트입니다.

연인 간 갈등 당사자가 각자의 입장을 정리하고, Mock AI 변호사와 Mock AI 판사의 의견 및 관전자 투표를 참고해 해결 방향을 탐색하는 서비스입니다. 실제 법률 판결이나 법률 상담을 제공하지 않습니다.

현재 저장소에는 팀 개발을 시작하기 위한 문서, 협업 규칙, 편집기 설정과 최소 Frontend·Backend 프로젝트가 구성되어 있습니다. Frontend에는 확정된 디자인 시스템과 shadcn-vue 사용 기반이 설정되어 있으며, 화면과 개별 UI 컴포넌트는 담당 Issue에서 추가합니다. Database, Docker 및 기능 코드는 아직 추가하지 않았습니다.

## 범위와 구현 상태

이 문서에서는 기능의 범위와 현재 구현 여부를 다음 상태로 구분합니다.

| 상태 | 의미 |
| --- | --- |
| `현재 구현` | 현재 Repository에 실행 가능한 코드나 설정이 존재함 |
| `Demo 개발 대상` | 이번 Local Live Demo에서 구현할 기능이며, 완료 여부는 아래 체크리스트와 GitHub Project에서 확인함 |
| `Frontend만 개발` | 화면과 로컬 Mock 데이터만 구현하며 Backend·Database에는 연결하지 않음 |
| `설계 완료·추후 개발` | 화면·ERD·API·권한 등 계약만 설계되었고 이번 Demo에서는 코드로 구현하지 않음 |

Notion에 기능이 작성되어 있거나 관련 Issue가 닫혀 있어도 실제 코드가 존재한다는 뜻은 아닙니다. GitHub Issue의 `Completed`와 `Not planned`도 구분하며, 실제 구현 완료 여부는 Repository 코드와 검증 결과를 기준으로 판단합니다.

자료가 서로 다를 때는 제품 범위·정책·인터페이스는 Notion, 담당자·Priority·Iteration·작업 상태는 GitHub Project, 실제 구현 여부는 Repository 코드와 실행된 검증 결과를 각각 기준으로 판단합니다.

### Demo 개발 대상

이번 Local Live Demo는 공개 재판의 다음 핵심 흐름을 개발 대상으로 합니다.

- 갈등 게시글과 공개 재판 생성
- 한 화면 흐름에서 A측 작성 후 B측 작성
- Mock AI 안내 질문, 답변, 사실관계 요약과 변론문 생성
- Backend 시간 기준 재판 단계 전이와 STOMP Event 전달
- 이전 채팅 조회, 원본 채팅 저장과 실시간 채팅
- 관전자당 한 번의 승소 투표
- 양측 변론을 사용한 Mock AI 판결 생성과 결과 저장
- AI 판결과 대중 투표 결과의 분리 표시

Demo에서는 실제 로그인 대신 Browser별 Demo 사용자 식별값을 사용하고 공개 재판만 동작시킵니다.

### Frontend만 개발

다음 일반 커뮤니티 기능은 Frontend 화면과 로컬 Mock 데이터로만 구현합니다. Backend API, Database 저장, 실제 인증과 권한 처리는 이번 Demo 범위가 아닙니다.

- 홈·인기 게시글·Live 재판·커뮤니티 가이드라인 탭
- 게시글 목록·상세·작성·수정·삭제 화면
- 관계 유형·갈등 사유 필터와 페이지 선택
- 댓글·답글·좋아요·신고 화면 및 로컬 상호작용

### 설계 완료·추후 개발

다음 기능은 전체 서비스 설계에는 포함되지만 이번 Demo에서는 구현하지 않습니다.

| 순서 | 기능 | 이번 Demo 처리 |
| --- | --- | --- |
| Future 1 | 회원가입·로그인·로그아웃·회원탈퇴, JWT 인증, 마이페이지 | Demo 사용자 식별값으로 대체 |
| Future 2 | 재판 중 AI 추가 질문과 지정 당사자 답변 | 상태·화면·API·STOMP·AI 계약만 설계 |
| Future 3 | 재판 등록 당사자의 강제 종료 | 종료 Event·권한·삭제 정책만 설계 |
| Future 4 | 비공개 재판 | 선택 UI를 제공하되 추후 개발로 안내 |
| Future 5 | 서로 다른 A측·B측 사용자의 독립 Form 참여 | Demo에서는 한 사용자가 A측 후 B측을 순차 작성 |

추가로 일반 커뮤니티 Backend와 Database 처리, 게시글·댓글·답글·좋아요·신고 권한, 실제 Amazon Bedrock 연동도 설계 또는 확장 지점만 유지하고 이번 Demo에서는 구현하지 않습니다.

## 프로젝트 자료

- [팀 Notion](https://confused-dietician-c17.notion.site/mini-Project-6-3cd7caa087bd808caf1bc28791f745e2?pvs=73)
- [GitHub Repository](https://github.com/skala-msa-team/web-mini)
- [GitHub Project](https://github.com/orgs/skala-msa-team/projects/3/views/1)
- [Google Stitch](https://stitch.withgoogle.com/projects/416001617538729018)
- [Figma](https://www.figma.com/design/kcaV9To7uU5HQHXDcbssGL/Untitled?node-id=0-1&t=pc3itD8mLpulTZE0-1)

기획, Actor 중심 Use Case, 화면 흐름, 기술 결정, ERD, API 및 AI JSON 계약은 Notion에서 관리합니다. 담당자, Priority, Iteration과 상태는 GitHub Project에서 관리합니다.

### 확정 설계 문서

- [사용자 흐름](https://confused-dietician-c17.notion.site/3cf7caa087bd8148b2d1f56e4245e0e3?pvs=25)
- [기능 명세서](https://confused-dietician-c17.notion.site/3cf7caa087bd8061a61dfae752a62ddc?pvs=25)
- [Demo ERD](https://confused-dietician-c17.notion.site/Demo-ERD-3d07caa087bd81918fb3d21eef3f9066?pvs=25)
- [Demo API 명세서](https://confused-dietician-c17.notion.site/Demo-API-3d07caa087bd8171aaa1fa90ba18db5f?pvs=25)
- [시스템 아키텍처](https://confused-dietician-c17.notion.site/8e27caa087bd83fba11a811959a760af?pvs=25)
- [실시간 AI 재판 구현 방식](https://confused-dietician-c17.notion.site/p/3d07caa087bd8001a677ff2486dd8c6c?pvs=25)

## 현재 확정된 방향

| 영역 | 방향 |
| --- | --- |
| Frontend | Vue 3, Vite, JavaScript, Tailwind CSS 4, shadcn-vue, Vue Router, STOMP Client; HTTP Client 미정 |
| Backend | Java 21, Spring Boot, Gradle, Web, Validation, WebSocket |
| Realtime | WebSocket, STOMP |
| Database | PostgreSQL |
| AI | Mock AI 우선, 추후 Amazon Bedrock 연동 |
| Design | Justice & Empathy 디자인 시스템, Google Stitch, Figma |
| Lint | ESLint, Checkstyle |

REST API, STOMP Destination, Database Schema, Demo 사용자 식별 방식과 AI 입출력 JSON 계약은 위 Notion 문서에서 확정되었습니다. 상태 관리·HTTP Client·Migration·API 문서화 등 확정되지 않은 Library는 도입하지 않습니다.

## 핵심 아키텍처 원칙

- Frontend는 Backend의 REST API와 WebSocket·STOMP만 사용하며 AI Provider와 Database를 직접 호출하지 않습니다.
- Backend가 재판 상태와 단계 시간을 관리하고 Frontend는 `phaseEndsAt`을 기준으로 남은 시간을 표시합니다.
- 재판 Event와 채팅은 Database Transaction Commit 이후 STOMP로 전송합니다.
- 최초 입장과 재연결 시 Topic을 먼저 구독한 뒤 Snapshot과 Sequence 이후 이력을 조회해 누락과 중복을 방지합니다.
- `AiClient` 계약을 유지한 채 Demo의 Mock AI를 향후 Amazon Bedrock Adapter로 교체합니다.
- 관전자 채팅과 투표는 AI 판결 입력에 포함하지 않고 결과 화면에서도 AI 판결과 대중 투표를 분리합니다.

Demo 재판 상태는 다음 순서를 사용합니다.

```text
PREPARING → INTRODUCTION → A_ARGUMENT → B_ARGUMENT → VOTING → VERDICT → ENDED
```

## 저장소 구조

```text
.
├── frontend/
│   ├── .env.example    # Frontend 환경변수 예시
│   ├── AGENTS.md       # Frontend 작업 규칙
│   ├── components.json # shadcn-vue CLI 설정
│   ├── src/app/        # App 진입점과 Router
│   ├── src/assets/     # 디자인 토큰과 공통 Style
│   ├── src/components/ # shadcn 기본 UI와 공통 조합 UI
│   ├── src/api/        # API 통신 인터페이스 뼈대(도메인 확정 후 보완)
│   └── ...             # Vue 3 + Vite 프로젝트
├── backend/
│   ├── .env.example    # Backend 환경변수 예시
│   ├── AGENTS.md       # Backend 작업 규칙
│   └── ...             # Spring Boot 최소 프로젝트
├── docs/
│   ├── AGENTS.md       # 설계 문서 작업 규칙
│   ├── README.md       # docs 책임과 자산 안내
│   └── assets/         # Notion에서 사용하는 정적 자산
├── .github/            # Issue 및 PR 템플릿
├── .vscode/            # 팀 공통 VS Code 설정
└── AGENTS.md           # 저장소 공통 작업 규칙
```

## 로컬 실행

사전 준비:

- Node.js `^20.19.0 || ^22.13.0 || >=24`
- npm
- Java 21

Frontend:

```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

디자인 토큰과 shadcn-vue 컴포넌트 추가 방법은 [`frontend/README.md`](frontend/README.md)의 `디자인 시스템과 shadcn-vue` 항목을 따릅니다. shadcn-vue 초기화는 완료되어 있으므로 `init`을 다시 실행하지 않습니다.

Backend:

```bash
cd backend
./gradlew bootRun
```

현재 환경변수:

| 영역 | 변수 | 예시 | 용도 |
| --- | --- | --- | --- |
| Frontend | `VITE_API_BASE_URL` | `http://localhost:8080` | Backend 기본 URL |
| Backend | `SERVER_PORT` | `8080` | Spring Boot 실행 Port |

실제 비밀값은 `.env` 또는 로컬 실행 환경에만 저장합니다. 새로운 환경변수가 확정되면 영역별 `.env.example`에는 변수명과 비밀값이 아닌 예시만 추가합니다. Spring Boot는 `.env` 파일을 자동으로 읽지 않으므로 Backend 값은 실행 환경변수로 전달합니다.

기본 검증:

```bash
(cd frontend && npm run lint)
(cd frontend && npm run build)
(cd backend && ./gradlew check)
```

## 협업 흐름

1. 최신 `dev`에서 영역 통합 Branch인 `frontend`와 `backend`를 분기하고 최신 상태를 유지합니다.
2. Issue 하나에 Branch 하나와 PR 하나를 연결합니다.
3. Frontend와 Backend 작업은 Task, Branch, Commit, PR을 분리합니다.
4. Frontend 작업 Branch는 최신 `frontend`에서, Backend 작업 Branch는 최신 `backend`에서 생성합니다.
5. Frontend 작업 PR은 `frontend`로, Backend 작업 PR은 `backend`로 생성합니다.
6. `frontend`와 `backend`에서 검증된 변경을 `dev`로 반영합니다.
7. 최종 완료 후 `dev`에서 `main`으로 반영합니다.
8. Review와 검증 후 Squash and merge합니다.

초기 Repository 부트스트랩 Issue는 위 협업 규칙 확정 전에 직접 반영되었습니다. 이후 기능 작업부터 Issue·Branch·PR 연결과 Review 규칙을 적용합니다.

## 협업 컨벤션

### Issue

- 제목: `[영역] type: 한국어 작업명`
- 예시: `[FE] feat: 작업 등록 화면 구현`, `[BE] feat: 작업 등록 API 구현`
- Feature 아래의 Task는 한 사람이 1~3시간 안에 완료할 크기로 나눕니다.
- 한 Task에 Frontend와 Backend 작업을 함께 넣지 않습니다.
- 작업 내용, 완료 조건, 선행 작업, 담당자, Iteration, Priority와 Label을 기록합니다.

영역은 `FE`, `BE`, `DB`, `AI`, `INTEGRATION`, `DESIGN`, `DOCS`, `QA`, `COMMON`을 사용합니다.

### Branch

- 형식: `type/작업영역-이슈번호-영어-작업명`
- 기준 Branch:
  Frontend 작업은 `frontend`, Backend 작업은 `backend`, Integration 작업은 `dev`에서 분기합니다.
- Frontend: `feat/frontend-12-task-form`
- Backend: `feat/backend-13-task-api`
- 연동: `fix/integration-27-task-flow`
- 문서: `docs/api-8-spec`

### Commit

- 형식: `type(scope): 한국어 제목`
- `type`과 `scope`는 영어, 제목과 본문은 한국어로 작성합니다.
- scope는 생략하지 않습니다.

```text
feat(frontend): 작업 등록 폼 추가
feat(backend): 작업 등록 API 추가
test(integration): 작업 등록 흐름 검증
docs(docs): API 명세 갱신
chore(common): 공통 개발환경 구성
```

| type | 용도 |
| --- | --- |
| `feat` | 기능 추가 |
| `fix` | 버그 수정 |
| `refactor` | 기능 변경 없는 코드 개선 |
| `docs` | 문서 작성 및 수정 |
| `test` | 테스트 작성 및 수정 |
| `style` | UI 스타일 또는 코드 Format |
| `chore` | 설정과 개발환경 작업 |

scope는 `frontend`, `backend`, `database`, `ai`, `design`, `docs`, `qa`, `integration`, `common`을 사용합니다.

### Pull Request

- Frontend 작업은 작업 Branch에서 `frontend`로 PR을 생성합니다.
- Backend 작업은 작업 Branch에서 `backend`로 PR을 생성합니다.
- `frontend`와 `backend`는 영역 통합 Branch이며, 각 영역의 검증된 작업을 먼저 모읍니다.
- `frontend`와 `backend`에서 검증된 변경만 `dev`로 반영합니다.
- `dev`는 최종 통합 Branch이며, GitHub 보호 규칙으로 최소 1명의 승인 Review를 요구합니다.
- `main`은 최종 완성본 Branch이며, GitHub의 필수 승인 규칙은 적용하지 않습니다.
- 작업 Branch에서 `main`으로 직접 병합하지 않고 최종 완료 시점에 `dev`의 검증된 내용을 `main`에 반영합니다.
- `Closes #이슈번호`를 작성합니다.
- Frontend와 Backend PR을 각각 영역 Branch에 병합한 뒤 별도 Integration Task와 PR로 `dev` 반영을 진행합니다.
- Review와 필요한 검증을 통과한 뒤 Squash and merge합니다.

### 네이밍

| 대상 | 규칙 | 예시 |
| --- | --- | --- |
| 디렉터리 | kebab-case | `task-result` |
| Vue 컴포넌트 | PascalCase | `TaskResultCard.vue` |
| JavaScript 함수·변수 | camelCase | `fetchTaskResult` |
| Java Package | lowercase | `com.skala.team6.webmini` |
| Java Class | PascalCase | `TaskService` |
| Java 함수·변수 | camelCase | `createTask` |
| 상수 | UPPER_SNAKE_CASE | `DEFAULT_TIMEOUT_MS` |
| API Path | 소문자 kebab-case, 복수 명사 | `/api/task-results` |
| JSON 필드 | camelCase | `createdAt` |
| DB 테이블·컬럼 | snake_case | `task_results`, `created_at` |
| 환경변수 | UPPER_SNAKE_CASE | `DB_PASSWORD` |

### 코드 작성

- Frontend는 Vue Composition API와 `<script setup>`을 기본으로 사용합니다.
- Backend는 Domain 기준 Package 아래에서 Controller, Service, Repository 책임을 분리합니다.
- Controller는 요청·응답 변환, Service는 비즈니스 규칙, Repository는 영속성을 담당합니다.
- Entity를 API 응답으로 직접 반환하지 않습니다.
- 코드와 로그에 Secret 및 민감정보를 남기지 않습니다.

### 작업 완료 기준

- Issue의 요구사항과 완료 조건을 충족합니다.
- 해당 작업의 Format, Lint, Test와 Build를 실행합니다.
- API, Database, WebSocket/STOMP, AI JSON 계약 변경 시 관련 문서를 함께 갱신합니다.
- 실제 실행한 검증 결과와 아직 확인하지 않은 항목을 구분합니다.
- 다른 팀원이 README만 보고 프로젝트를 실행할 수 있도록 실행 방법을 갱신합니다.

## 현재 완료 범위

- [x] 루트 및 영역별 `AGENTS.md`
- [x] `.gitignore`, `.editorconfig`, `.gitattributes`
- [x] VS Code 공통 설정과 추천 Extension
- [x] GitHub Feature·Task·Bug·Refactor Issue 템플릿
- [x] Pull Request 템플릿
- [x] 과제용 README와 협업 컨벤션
- [x] Notion에서 사용하는 기존 가이드 이미지 보존
- [x] Vue 3 + Vite 최소 Frontend 프로젝트 생성
- [x] Java 21 + Spring Boot 최소 Backend 프로젝트 생성
- [x] 확정된 Frontend·Backend 필수 Library 의존성 추가
- [x] Justice & Empathy 디자인 토큰과 shadcn-vue 사용 기반 구성
- [x] Frontend·Backend 환경변수 예시 작성
- [x] ESLint·Checkstyle 설정
- [x] REST API·공통 응답·오류 계약 확정
- [x] Demo ERD와 Database 제약조건 확정
- [x] 재판 상태·STOMP Message 계약 확정
- [x] Mock AI 입출력 JSON 계약 확정
- [ ] Database와 Docker 구성
- [ ] API, WebSocket/STOMP, Mock AI와 기능 구현
