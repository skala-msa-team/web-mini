# AI-Ready Web Service Mini Project - 6조

SKALA Full-Stack Engineering 과정의 AI-Ready 웹 서비스 설계 팀 프로젝트 저장소입니다.

현재 저장소에는 팀 개발을 시작하기 위한 문서, 협업 규칙, 편집기 설정과 최소 Frontend·Backend 프로젝트가 구성되어 있습니다. 확정된 필수 Library는 의존성만 추가했으며 Database, Docker, Library 설정 및 기능 코드는 아직 추가하지 않았습니다.

## 프로젝트 자료

- [팀 Notion](https://confused-dietician-c17.notion.site/mini-Project-6-3cd7caa087bd808caf1bc28791f745e2?pvs=73)
- [GitHub Repository](https://github.com/skala-msa-team/web-mini)
- [GitHub Project](https://github.com/orgs/skala-msa-team/projects/3/views/1)
- [Google Stitch](https://stitch.withgoogle.com/projects/416001617538729018)
- [Figma](https://www.figma.com/design/kcaV9To7uU5HQHXDcbssGL/Untitled?node-id=0-1&t=pc3itD8mLpulTZE0-1)

기획, Actor 중심 Use Case, 화면 흐름, 기술 결정, ERD, API 및 AI JSON 계약은 Notion에서 관리합니다. 담당자, Priority, Iteration과 상태는 GitHub Project에서 관리합니다.

## 현재 확정된 방향

| 영역 | 방향 |
| --- | --- |
| Frontend | Vue 3, Vite, JavaScript, Vue Router, Axios, STOMP Client |
| Backend | Java 21, Spring Boot, Gradle, Web, Validation, WebSocket |
| Realtime | WebSocket, STOMP |
| Database | PostgreSQL |
| AI | Mock AI 우선, 추후 Amazon Bedrock 연동 |
| Design | Google Stitch, Figma |

미정인 Library와 Version, API Path, STOMP Destination, Database Schema와 AI JSON은 담당 Issue에서 설계가 확정된 후 추가합니다.

## 저장소 구조

```text
.
├── frontend/
│   ├── AGENTS.md       # Frontend 작업 규칙
│   └── ...             # Vue 3 + Vite 최소 프로젝트
├── backend/
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

사전 준비: Node.js, npm, Java 21

Frontend:

```bash
cd frontend
npm install
npm run dev
```

Backend:

```bash
cd backend
./gradlew bootRun
```

기본 검증:

```bash
(cd frontend && npm run build)
(cd backend && ./gradlew test)
```

## 협업 흐름

1. 최신 `dev`에서 작업 Branch를 생성합니다.
2. Issue 하나에 Branch 하나와 PR 하나를 연결합니다.
3. Frontend와 Backend 작업은 Task, Branch, Commit, PR을 분리합니다.
4. 일반 작업 PR은 `dev`로 생성합니다.
5. 최종 완료 후 `dev`에서 `main`으로 PR을 생성합니다.
6. Review와 검증 후 Squash and merge합니다.

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

- 일반 작업은 작업 Branch에서 `dev`로 PR을 생성합니다.
- 최종 완료 후 `dev`에서 `main`으로 PR을 생성합니다.
- `Closes #이슈번호`를 작성합니다.
- Frontend와 Backend PR을 각각 병합한 뒤 별도 Integration Task와 PR로 연동합니다.
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
- [ ] Database와 Docker 구성
- [ ] API, WebSocket/STOMP, Mock AI와 기능 구현
