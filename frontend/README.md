# Frontend

Vue 3와 Vite 기반 Frontend 프로젝트입니다. Tailwind CSS 4와 shadcn-vue 사용 기반, Justice & Empathy 디자인 토큰, Vue Router, Axios REST Client와 승인된 실시간 재판용 STOMP 연결 구성이 설정되어 있습니다. 상태 관리 Library는 미정입니다.

## 책임별 구조

```text
src/
├── app/              # App.vue, main.js, router
├── assets/           # Tailwind CSS 진입점, 디자인 토큰, 폰트, 전역 스타일
├── components/       # Tailwind CSS + shadcn-vue UI Primitive, 공통·도메인 컴포넌트
├── pages/            # Router에 연결되는 화면
├── apis/             # API별 순수 요청 함수
├── lib/              # Axios·HTTP·STOMP 등 외부 의존 로직
├── utils/            # 공용 순수 함수
├── consts/           # 상수·메시지·매직넘버
├── mock/             # 도메인별 Demo 목 데이터
├── stores/           # 화면 상태
└── composables/      # 여러 기능이 공유하는 상태 로직
```

`assets/styles`는 Tailwind CSS 진입점, Justice & Empathy 디자인 토큰, 폰트와 전역 스타일을 담당합니다. `components/ui`에는 Tailwind CSS와 shadcn-vue 구조를 사용하는 재사용 기본 UI를 두고, 도메인 조합 UI는 `components/{domain}`에 둡니다. `pages`는 화면 조합과 라우팅에 집중합니다. REST 요청은 `apis`의 개별 함수가 `lib/http.js`를 통해 호출합니다. STOMP 연결·구독·복구는 `lib/realtime.js`에서 관리합니다.

## 실행과 검증

### 전체 서비스 실행

일반 실행과 데모용 실행은 프로젝트 루트의 Docker Compose를 사용합니다.

```bash
docker compose up -d --build
```

일반 접속 주소는 `http://localhost:8081`입니다. 데모에서는 같은 네트워크의 기기에서 `http://<HOST_LAN_IP>:8081`로 접속합니다. Frontend Container의 Nginx가 `/api`와 `/ws` 요청을 Backend Container로 프록시합니다.

종료:

```bash
docker compose down
```

### Frontend 개발 실행

Frontend 환경 파일을 준비합니다. 실제 `.env`는 Git에서 제외되므로 팀원별 주소를 안전하게 설정할 수 있습니다.

## 최초 실행

```bash
cp .env.example .env
npm install
npm run dev
```

기본 설정에서는 Vite가 `0.0.0.0:5173`으로 열리고 `/api`, `/ws` 요청을 로컬 Backend `localhost:8080`으로 전달합니다. `5173`이 이미 사용 중이면 다른 Port로 자동 변경하지 않고 실행을 중단하므로, 중복 실행을 먼저 종료합니다.

PostgreSQL만 Docker로 실행하고 Backend는 로컬에서 실행하려면 프로젝트 루트와 Backend에서 각각 다음 명령을 사용합니다.

```bash
# 프로젝트 루트
docker compose up -d postgres

# backend 디렉터리
cp .env.example .env
set -a
source .env
set +a
./gradlew bootRun
```

Frontend 검증:

```bash
npm run lint
npm run build
```

### 같은 네트워크에서 공유

1. 호스트 PC에서 위 방식으로 PostgreSQL, Backend, Frontend를 실행합니다.
2. `npm run dev` 출력의 `Network` 주소(예: `http://192.168.0.15:5173`)를 팀원에게 공유합니다.
3. 호스트 PC의 `backend/.env`에서 `APP_CORS_ALLOWED_ORIGINS`에 해당 Network 주소를 추가한 후 Backend를 다시 실행합니다.

```dotenv
APP_CORS_ALLOWED_ORIGINS=http://localhost:5173,http://127.0.0.1:5173,http://192.168.0.15:5173
```

Frontend `.env`의 `VITE_API_BASE_URL`은 `/api/v1`로 유지합니다. 팀원 브라우저가 `localhost:8080`을 직접 호출하지 않고, 공유받은 Vite 서버가 REST와 WebSocket 요청을 Backend로 프록시합니다.

Backend가 다른 장비나 Port에서 실행되는 경우에만 Frontend `.env`의 프록시 대상을 변경합니다.

```dotenv
BACKEND_HTTP_ORIGIN=http://192.168.0.20:8080
BACKEND_WS_ORIGIN=ws://192.168.0.20:8080
```

## 디자인 시스템과 shadcn-vue

### 설정 위치

| 파일                           | 책임                                                                |
| ------------------------------ | ------------------------------------------------------------------- |
| `src/assets/styles/tokens.css` | 원본 디자인 시스템의 색상, 글꼴, 크기, 간격, Radius, Elevation 토큰 |
| `src/assets/styles/global.css` | Tailwind CSS 진입점, shadcn 의미 토큰 연결, 전역 기본 스타일        |
| `src/assets/styles/fonts.css`  | Plus Jakarta Sans, Be Vietnam Pro 웹 폰트                           |
| `components.json`              | shadcn-vue CLI의 JavaScript, 경로, 스타일 설정                      |
| `src/lib/utils.js`             | shadcn-vue 컴포넌트가 사용하는 `cn()` 클래스 병합 함수              |
| `vite.config.js`               | Tailwind CSS Vite Plugin과 `@` → `src` 경로 별칭                    |
| `jsconfig.json`                | Editor가 `@/*` 경로 별칭을 해석하기 위한 설정                       |

제목은 Plus Jakarta Sans, 본문과 Label은 Be Vietnam Pro를 사용합니다. 웹 폰트는 `index.html`에서 불러오며 불러오지 못하면 지정된 sans-serif 또는 system font로 대체됩니다.

shadcn의 의미 토큰은 다음 원칙으로 디자인 시스템에 연결되어 있습니다.

| UI 의미                     | 디자인 토큰                                                    |
| --------------------------- | -------------------------------------------------------------- |
| 기본 배경과 글자            | `--ds-color-background`, `--ds-color-on-background`            |
| Card와 Popover              | `--ds-color-surface-container-lowest`, `--ds-color-on-surface` |
| Primary Action과 Focus Ring | `--ds-color-justice-blue`                                      |
| Error와 Destructive         | `--ds-color-error`, `--ds-color-on-error`                      |
| Border와 Input              | `--ds-color-card-border`                                       |

구조화된 원본 색상은 `--ds-color-*` 이름으로 모두 보존했습니다. 원본 설명에 별도로 명시된 Verdict Blue, Justice Blue, Accent Red, Trust Mint도 이름이 있는 브랜드 토큰으로 분리했습니다. 디자인 변경 시 컴포넌트마다 색상을 직접 수정하지 말고 먼저 `tokens.css`와 `global.css`의 의미 연결을 확인합니다.

화면에서는 `bg-background`, `text-foreground`, `bg-primary`, `border-border`처럼 shadcn 의미 Class를 우선 사용합니다. 제목은 `font-heading text-heading-1`, 작은 본문은 `text-body-sm`, Label은 `text-label`처럼 조합합니다. 디자인에 없는 임의의 Hex 색상이나 간격을 컴포넌트에 직접 추가하지 않습니다.

### 팀원 사용 방법

shadcn-vue는 완성된 컴포넌트 Package를 통째로 가져오는 방식이 아니라, 선택한 컴포넌트 소스 코드를 프로젝트의 `src/components/ui/` 아래에 추가하는 방식입니다. 필요한 컴포넌트만 담당 Frontend Issue 범위 안에서 추가합니다.

Frontend 디렉터리에서 다음 명령을 실행합니다.

```bash
npx shadcn-vue@latest add button
```

추가한 컴포넌트는 다음처럼 사용합니다.

```vue
<script setup>
import Button from "@/components/ui/Button.vue"
</script>

<template>
  <Button>확인</Button>
</template>
```

팀 공통 규칙:

1. `npx shadcn-vue@latest init`은 다시 실행하지 않습니다. 현재 `components.json`과 디자인 토큰을 덮어쓸 수 있습니다.
2. `add` 실행 전 해당 컴포넌트가 이미 `src/components/ui/`에 있는지 확인합니다.
3. 한 번에 전체 컴포넌트를 설치하지 않고 현재 Issue에 필요한 컴포넌트만 추가합니다.
4. 생성된 컴포넌트는 프로젝트 코드이므로 수정할 수 있지만, 색상과 간격은 기존 디자인 토큰을 우선 사용합니다.
5. 생성·변경된 파일과 `package.json`, `package-lock.json`을 함께 Review하고 `npm run lint`, `npm run build`를 실행합니다.

자세한 CLI 사용법은 [shadcn-vue 공식 CLI 문서](https://www.shadcn-vue.com/docs/cli)를 참고합니다.

## API 통신 규약

Backend를 별도로 실행하는 로컬 개발에서는 `.env.example`을 복사합니다. Demo REST Base URL에는 `/api/v1`을 포함합니다. 루트 Docker Compose로 실행할 때는 기본값 `/api/v1`을 사용하므로 별도 `.env`가 필요하지 않습니다.

```bash
cp .env.example .env
```

```dotenv
VITE_API_BASE_URL=/api/v1
BACKEND_HTTP_ORIGIN=http://localhost:8080
BACKEND_WS_ORIGIN=ws://localhost:8080
```

공통 규약:

- `src/lib/http.js`의 Axios Instance만 사용합니다.
- 공통 timeout은 15초이며 자동 재시도하지 않습니다.
- 요청 인터셉터가 Browser Local Storage의 UUID를 `X-Demo-User-Id`에 자동으로 추가합니다.
- 응답 변환은 `src/utils/apiResponse.js`의 순수 함수가 담당합니다.
- 도메인 API는 성공 Envelope의 `data`를 반환합니다.
- Backend 오류는 `ApiError`로 변환하며 `status`, `code`, `message`, `fieldErrors`, `timestamp`, `path`를 제공합니다.
- 서버 응답이 없는 timeout과 네트워크 오류는 각각 `REQUEST_TIMEOUT`, `NETWORK_ERROR` Code를 사용합니다.
- 로딩 상태는 전역 인터셉터가 아니라 요청을 호출하는 Page 또는 Composable에서 관리합니다.

```js
import { getSnapshot } from '@/apis/trialApi.js'

const snapshot = await getSnapshot(trialId)
```

화면은 `ApiError.code`를 기준으로 오류 상태를 분기합니다.

```js
import { getResults } from '@/apis/trialApi.js'

try {
  return await getResults(trialId)
} catch (error) {
  if (error.code === 'RESULT_NOT_FOUND') {
    // 결과 준비 중 화면 처리
  }
  throw error
}
```

```text
Page → Composable → Domain API → HTTP Client
     → Backend
     → 화면 갱신
```
