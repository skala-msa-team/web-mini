# 사랑과 전쟁터

SKALA Full-Stack Engineering 과정의 6조 AI-Ready 웹 서비스 설계 프로젝트입니다.

연인 간 갈등 당사자가 각자의 입장을 정리하고, Mock AI 변호사와 Mock AI 판사의 의견 및 관전자 투표를 참고해 해결 방향을 탐색하는 서비스입니다. 실제 법률 판결이나 법률 상담을 제공하지 않습니다.

현재 저장소에는 팀 개발을 위한 문서, 협업 규칙, 편집기 설정, Justice & Empathy 디자인 시스템, Frontend·Backend 프로젝트, PostgreSQL Schema, Docker Compose와 Local Live Demo 기능 코드가 구성되어 있습니다.

## 범위와 구현 상태

이 문서에서는 기능의 범위와 현재 구현 여부를 다음 상태로 구분합니다.

| 상태                  | 의미                                                                                              |
| --------------------- | ------------------------------------------------------------------------------------------------- |
| `현재 구현`           | 현재 Repository에 실행 가능한 코드나 설정이 존재함                                                |
| `Demo 개발 대상`      | 이번 Local Live Demo에서 구현할 기능이며, 완료 여부는 아래 체크리스트와 GitHub Project에서 확인함 |
| `Frontend만 개발`     | 화면과 로컬 Mock 데이터만 구현하며 Backend·Database에는 연결하지 않음                             |
| `설계 완료·추후 개발` | 화면·ERD·API·권한 등 계약만 설계되었고 이번 Demo에서는 코드로 구현하지 않음                       |

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

## 기술 스택

| 영역 | 기술 |
| --- | --- |
| Design | Google Stitch, Figma, Justice & Empathy Design System |
| Frontend | Vue 3, Vite, JavaScript, Vue Router, Tailwind CSS 4, shadcn-vue, lucide-vue |
| Frontend API | Axios, REST API Client, STOMP Client |
| Backend | Java 21, Spring Boot 4, Gradle, Spring Web, Bean Validation |
| Backend Realtime | Spring WebSocket/STOMP, Topic Broker, User Queue |
| Database | PostgreSQL 17, Spring Data JPA, Flyway |
| AI | Mock AI Client, Mock AI Lawyer, Mock AI Judge |
| API Docs | springdoc-openapi, Swagger UI |
| Infra | Docker Compose, Nginx Reverse Proxy |
| External Ops (미검증) | AWS, Amazon API Gateway, Amazon EC2, Amazon RDS, GitHub Actions |
| Future AI (추후 예정) | Amazon Bedrock Agents, Bedrock Knowledge Base, Amazon S3, OpenSearch Serverless |
| Collaboration | Notion, GitHub Project, GitHub Issues, Pull Requests |

`External Ops`와 `Future AI`는 현재 저장소의 로컬 실행 코드와 분리해 관리합니다. 운영 한계와 Bedrock 기반 AI 결합 계획은 [프로젝트 한계점 및 추후 AI 실제 결합 로드맵](docs/ai-roadmap.md)을 기준으로 확인합니다.

## 디렉토리 구조

빌드 산출물인 `frontend/dist/`, `backend/build/`, `backend/.gradle/`, 의존성 디렉토리인 `node_modules/`는 구조 설명에서 제외합니다.

```text
.
├── frontend/                         # Vue 3 + Vite Frontend 애플리케이션
│   ├── public/                       # 정적 공개 파일
│   │   └── images/                   # 화면에서 직접 사용하는 이미지 자산
│   └── src/
│       ├── apis/                     # REST API 요청 함수
│       ├── app/                      # 앱 진입점, 최상위 App, Router
│       │   └── router/               # Vue Router 설정
│       ├── assets/                   # Frontend 전용 스타일 자산
│       │   └── styles/               # Justice & Empathy 토큰, 폰트, Global CSS
│       ├── components/               # 재사용 UI 컴포넌트
│       │   ├── chat/                 # 재판 채팅 UI
│       │   ├── common/               # Header, Footer 등 공통 레이아웃
│       │   ├── community/            # 커뮤니티 목록·게시글 UI
│       │   ├── trial/                # 재판 준비·진행 UI
│       │   ├── ui/                   # Tailwind CSS + shadcn-vue UI Primitive
│       │   ├── verdict/              # AI 판결 결과 UI
│       │   └── vote/                 # 관전자 투표 UI
│       ├── composables/              # Vue Composition API 기반 상태·흐름 로직
│       ├── consts/                   # API, 상태, 메시지, STOMP 상수
│       ├── lib/                      # HTTP Client, Realtime Client, 공통 유틸
│       ├── mock/                     # Frontend-only 화면 검증용 Mock 데이터
│       │   ├── community/
│       │   ├── trial/
│       │   ├── verdict/
│       │   └── vote/
│       ├── pages/                    # Router 단위 화면
│       │   ├── community/
│       │   ├── integration/
│       │   ├── live-trial/
│       │   ├── trial-preparation/
│       │   └── trial-result/
│       ├── stores/                   # 화면 상태 Store
│       └── utils/                    # 순수 유틸 함수
├── backend/                          # Java 21 + Spring Boot Backend 애플리케이션
│   ├── config/
│   │   └── checkstyle/               # Java Code Style 검증 규칙
│   ├── gradle/
│   │   └── wrapper/                  # Gradle Wrapper
│   └── src/
│       ├── main/
│       │   ├── java/com/skala/team6/webmini/
│       │   │   ├── ai/               # Mock AI Client, 변론·판결 Service, AI API
│       │   │   ├── common/           # 공통 응답, 설정, 예외, Enum
│       │   │   │   ├── api/
│       │   │   │   ├── config/
│       │   │   │   ├── exception/
│       │   │   │   └── model/
│       │   │   ├── database/         # JPA Entity와 Repository
│       │   │   │   ├── entity/
│       │   │   │   └── repository/
│       │   │   ├── demo/             # Demo 사용자 식별과 저장
│       │   │   ├── post/             # 게시글 REST API와 Service
│       │   │   ├── trial/            # 재판 생성, 진행, 채팅, 투표, 결과, STOMP
│       │   │   └── websocket/        # STOMP 인증·오류·Presence 처리
│       │   └── resources/
│       │       ├── db/
│       │       │   └── migration/    # Flyway Database Migration
│       │       └── static/           # 로컬 STOMP 테스트 페이지
│       └── test/
│           └── java/com/skala/team6/webmini/
│               ├── ai/               # AI Service·Controller Test
│               ├── database/         # Migration·Persistence Test
│               ├── post/             # 게시글 Acceptance Test
│               ├── trial/            # 재판 흐름·채팅·투표 Test
│               └── websocket/        # STOMP Interceptor·Error Test
├── docs/                             # 승인된 설계 문서와 정적 가이드 자산
│   ├── ai-roadmap.md                 # 운영 한계와 Bedrock 기반 AI 결합 로드맵
│   ├── ai/
│   │   └── prompts/                  # Mock AI Prompt 계약
│   ├── assets/
│   │   └── github-guide/             # Notion에서 참조하는 GitHub 가이드 이미지
│   └── user-flow.md                  # Actor, Use Case, 유저플로우 노드·엣지
└── .vscode/                          # 팀 공통 VS Code 설정
```

### 디자인 시스템

Justice & Empathy 디자인 시스템은 공정한 재판장 이미지와 커뮤니티의 따뜻함을 함께 주기 위한 Frontend 시각 기준입니다. Frontend 스타일링은 Tailwind CSS를 중심으로 구성하며, 색상, Typography, Radius, Spacing Token은 `frontend/src/assets/styles/tokens.css`에 정의합니다. Tailwind Theme 연결과 전역 스타일은 `frontend/src/assets/styles/global.css`에서 관리합니다.

기본 컴포넌트는 Tailwind CSS Utility Class와 shadcn-vue 구조를 함께 사용합니다. `frontend/components.json`에서 `new-york` 스타일, JavaScript, CSS Variables, `@/components/ui` Alias와 `lucide` Icon Library를 설정합니다. 실제 UI Primitive는 `frontend/src/components/ui/`에 두고, 화면별 컴포넌트는 이 Primitive와 Token을 조합합니다.

디자인 기준은 색상, 폰트, 간격, 카드, 버튼, 입력창, LIVE Badge, 투표 Progress Bar를 포함합니다. Variant 관리는 `class-variance-authority`, Class 병합은 `clsx`와 `tailwind-merge`를 사용합니다.

### 역할 경계

| 영역         | 책임                                                  | 변경 기준 Branch |
| ------------ | ----------------------------------------------------- | ---------------- |
| `frontend/`  | 화면, Router, REST 호출, STOMP Client, 화면 상태 관리 | `frontend`       |
| `frontend/src/assets/styles/` | Justice & Empathy 디자인 토큰과 전역 스타일           | `frontend`       |
| `frontend/src/components/ui/` | Tailwind CSS와 shadcn-vue 기반 UI Primitive           | `frontend`       |
| `backend/`   | REST API, STOMP, Domain Service, DB, Mock AI Adapter   | `backend`        |
| `docs/`      | 승인된 API, ERD, Database, STOMP, AI 계약 문서         | 작업 성격에 따름 |
| `.vscode/`   | 팀 공통 편집기 설정                                   | `dev` 기준 협의  |
| 루트 설정 파일 | Git, Docker Compose, Repository 협업 규칙             | `dev` 기준 협의  |

## 실행 방법

사전 준비: Docker와 Docker Compose 설치

### 일반 실행

루트 `compose.yaml`로 Frontend(Nginx), Backend(Spring Boot), PostgreSQL을 한 번에 빌드·실행합니다.

```bash
docker compose up -d --build
```

접속 주소:

```text
http://localhost:8081
```

종료:

```bash
docker compose down
```

### 데모용 실행

데모용 실행도 같은 Docker Compose를 사용합니다. 차이는 실행 명령이 아니라 접속 주소입니다. 발표자 PC에서 Compose를 실행한 뒤, 같은 네트워크의 팀원이나 시연 기기는 발표자 PC의 LAN IP로 접속합니다.

```bash
docker compose up -d --build
```

발표자 PC의 LAN IP 확인(macOS 예시):

```bash
ipconfig getifaddr en0
```

Mac의 네트워크 인터페이스는 환경에 따라 `en0`이 아닐 수 있습니다. 값이 나오지 않으면 `en1`을 확인하거나 `networksetup -listallhardwareports`로 Wi-Fi 장치명을 먼저 확인합니다.

```bash
ipconfig getifaddr en1
networksetup -listallhardwareports
```

데모 접속 주소:

```text
http://<HOST_LAN_IP>:8081
```

예시:

```text
http://192.168.0.15:8081
```

루트 Compose 실행에서는 Frontend가 호스트의 `8081`로 매핑되고, Nginx가 `/api`와 `/ws` 요청을 내부 Backend Service로 프록시합니다. `compose.yaml`의 Backend CORS는 로컬 LAN 데모 접속을 위해 모든 Origin을 허용하도록 설정되어 있습니다.

운영 또는 외부 배포 환경에서는 `APP_CORS_ALLOWED_ORIGINS=*`를 절대 사용하지 않습니다. 배포 환경에서는 실제 Frontend Origin만 명시적으로 허용해야 합니다.

종료:

```bash
docker compose down
```

주의사항:

- 같은 Wi-Fi여도 회사/학교 네트워크의 클라이언트 분리 또는 방화벽 설정에 따라 접속이 차단될 수 있습니다.
- `8081` Port가 이미 사용 중이면 `FRONTEND_PORT` 환경변수로 호스트 Port를 바꿔 실행합니다.

```bash
FRONTEND_PORT=8082 docker compose up -d --build
```

현재 환경변수:

| 영역     | 변수                                   | 예시                                          | 용도                                                    |
| -------- | -------------------------------------- | --------------------------------------------- | ------------------------------------------------------- |
| Frontend | `VITE_API_BASE_URL`                    | `/api/v1` 또는 `http://localhost:8080/api/v1` | Docker 통합 실행 또는 Backend 개별 실행용 REST Base URL |
| Backend  | `SERVER_PORT`                          | `8080`                                        | Spring Boot 실행 Port                                   |
| Backend  | `APP_CORS_ALLOWED_ORIGINS`             | `http://localhost:5173,http://localhost:8081` | REST API와 STOMP Endpoint 허용 Origin                   |
| Backend  | `APP_DEMO_USER_HEADER_NAME`            | `X-Demo-User-Id`                              | Demo 사용자 식별 Header 이름                            |
| Backend  | `APP_WEBSOCKET_ENDPOINT`               | `/ws`                                         | STOMP Handshake Endpoint                                |
| Backend  | `APP_WEBSOCKET_HEARTBEAT`              | `10000,10000`                                 | STOMP Heartbeat 송수신 간격                             |
| Backend  | `APP_WEBSOCKET_MESSAGE_SIZE_LIMIT`     | `65536`                                       | 수신 메시지 최대 크기                                   |
| Backend  | `APP_WEBSOCKET_SEND_BUFFER_SIZE_LIMIT` | `131072`                                      | 송신 버퍼 최대 크기                                     |
| Backend  | `APP_WEBSOCKET_SEND_TIME_LIMIT`        | `20000`                                       | 송신 시간 제한                                          |
| Backend  | `APP_AI_PROVIDER`                      | `mock`                                        | 현재 AI Adapter 선택                                    |
| Backend  | `APP_AI_PROMPT_VERSION`                | `judge-v1`                                    | AI 요청 Prompt Version                                  |
| Backend  | `DB_NAME`                              | `webmini`                                     | Docker PostgreSQL Database 이름                         |
| Backend  | `DB_PORT`                              | `5432`                                        | Docker PostgreSQL 호스트 Port                           |
| Backend  | `DB_URL`                               | `jdbc:postgresql://localhost:5432/webmini`    | Backend Database JDBC URL                               |
| Backend  | `DB_USERNAME`                          | `webmini`                                     | 로컬 개발 Database 사용자                               |
| Backend  | `DB_PASSWORD`                          | `webmini`                                     | 로컬 개발 Database 비밀번호 예시                        |

실제 비밀값은 `.env` 또는 로컬 실행 환경에만 저장합니다. 새로운 환경변수가 확정되면 영역별 `.env.example`에는 변수명과 비밀값이 아닌 예시만 추가합니다. Spring Boot는 `.env` 파일을 자동으로 읽지 않으므로 Backend 값은 실행 환경변수로 전달합니다.

Backend에는 [backend/.env.example](backend/.env.example)에 실행 예시를 두었습니다. 이 파일은 참고용이며 자동 로드되지 않습니다. 아무 환경변수를 주지 않아도 기본값으로 실행되지만, 팀원별 또는 환경별 차이는 실행 환경변수로 override합니다.

### Backend WebSocket/STOMP 기본값

현재 Backend에는 Demo 재판 실시간 연결과 재판 진행을 위한 WebSocket/STOMP 구현이 포함되어 있습니다.

- Handshake Endpoint: `/ws`
- Application Prefix: `/app`
- Broker Prefix: `/topic`
- User Destination Prefix: `/user`
- 개인 오류 Queue: `/user/queue/errors`
- Demo 사용자 식별: `CONNECT` 프레임의 `X-Demo-User-Id`

현재 구현은 Demo 사용자 식별, 기본 Broker 설정, 개인 오류 Queue, 채팅 `@MessageMapping`, 재판 상태 전이, 채팅·Event 저장, Commit 이후 Broadcast를 포함합니다.

간단한 로컬 확인 예시:

```bash
cd backend
export APP_CORS_ALLOWED_ORIGINS=http://localhost:5173
export APP_WEBSOCKET_ENDPOINT=/ws
./gradlew bootRun
```

이후 STOMP Client에서 `/ws` 로 연결하고 다음 경로를 확인합니다.

- `SEND /app/trials/{trialId}/chat`
- `SUBSCRIBE /topic/trials/{trialId}/chat`
- `SUBSCRIBE /topic/trials/{trialId}/events`
- `SUBSCRIBE /user/queue/errors`

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

공용 협업 규칙은 이 문서를 기준으로 합니다. Frontend와 Backend의 세부 코드 작성 규칙, 디렉토리 역할, 실행·검증 방법은 각 파트 README를 따릅니다.

- Frontend 세부 규칙: [frontend/README.md](frontend/README.md)
- Backend 세부 규칙: [backend/README.md](backend/README.md)
- 문서 관리 규칙: [docs/README.md](docs/README.md)

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

| type       | 용도                       |
| ---------- | -------------------------- |
| `feat`     | 기능 추가                  |
| `fix`      | 버그 수정                  |
| `refactor` | 기능 변경 없는 코드 개선   |
| `docs`     | 문서 작성 및 수정          |
| `test`     | 테스트 작성 및 수정        |
| `style`    | UI 스타일 또는 코드 Format |
| `chore`    | 설정과 개발환경 작업       |

scope는 `frontend`, `backend`, `database`, `ai`, `design`, `docs`, `qa`, `integration`, `common`을 사용합니다.

### Pull Request

- Frontend 작업은 작업 Branch에서 `frontend`로 PR을 생성합니다.
- Backend 작업은 작업 Branch에서 `backend`로 PR을 생성합니다.
- `frontend`와 `backend`는 영역 통합 Branch이며, 각 영역의 검증된 작업을 먼저 모읍니다.
- `frontend`와 `backend`에서 검증된 변경만 `dev`로 반영합니다.
- `dev`는 최종 통합 Branch이며, GitHub 보호 규칙으로 최소 1명의 승인 Review를 요구합니다.
- `main`은 최종 완성본 Branch이며, GitHub의 필수 승인 규칙은 적용하지 않습니다.
- 작업 Branch에서 `main`으로 직접 병합하지 않고 최종 완료 시점에 `dev`의 검증된 내용을 `main`에 반영합니다.
- 기본적으로 같은 R&R 영역의 동료에게 Review를 요청하고, 승인 후 작성자와 Reviewer가 함께 변경 범위와 검증 결과를 확인한 뒤 병합합니다.
- `Closes #이슈번호`를 작성합니다.
- Frontend와 Backend PR을 각각 영역 Branch에 병합한 뒤 별도 Integration Task와 PR로 `dev` 반영을 진행합니다.
- Review와 필요한 검증을 통과한 뒤 Squash and merge합니다.

### 공용 네이밍

| 대상                 | 규칙                         | 예시                         |
| -------------------- | ---------------------------- | ---------------------------- |
| 디렉터리             | kebab-case                   | `task-result`                |
| API Path             | 소문자 kebab-case, 복수 명사 | `/api/task-results`          |
| JSON 필드            | camelCase                    | `createdAt`                  |
| 환경변수             | UPPER_SNAKE_CASE             | `DB_PASSWORD`                |

파트별 네이밍과 코드 작성 규칙은 각 파트 README를 따릅니다.

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
- [x] PostgreSQL Schema, Flyway와 로컬 Docker Compose 구성
- [x] API, WebSocket/STOMP, Mock AI와 기능 구현
