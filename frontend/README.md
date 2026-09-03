# Frontend

Vue 3와 Vite 기반 Frontend 프로젝트입니다. Tailwind CSS 4와 shadcn-vue 사용 기반, Justice & Empathy 디자인 토큰, Vue Router와 승인된 실시간 재판용 STOMP 연결 구성이 설정되어 있습니다. 상태 관리 Library와 HTTP Client는 미정입니다.

## 책임별 구조

```text
src/
├── app/              # App.vue, main.js, router
├── assets/           # images, styles/fonts·tokens·global.css
├── components/       # ui(shadcn), common, layout
├── pages/            # Router에 연결되는 화면
├── features/         # 기능별 UI와 상태
├── api/              # API 통신 인터페이스 뼈대(도메인 확정 후 추가)
├── realtime/         # WebSocket·STOMP
├── composables/      # 여러 기능이 공유하는 상태 로직
├── constants/        # 상태·이벤트 상수(계약 확정 후 정의)
├── lib/              # shadcn 유틸
└── utils/            # 도메인과 무관한 순수 유틸
```

`components/ui`에는 shadcn-vue가 생성한 기본 UI만 두고, 여러 화면에서 조합하는 UI는 `components/common`, 특정 기능 전용 UI는 `features/*/components`에 둡니다. `pages`는 화면 조합과 라우팅에 집중합니다. REST 요청은 `api` 폴더에서 도메인 인터페이스 형태로 구성하고, 실제 HTTP 라이브러리는 계약이 확정된 시점에 맞춰 결정합니다.

## 실행과 검증

```bash
npm install
npm run dev
npm run lint
npm run build
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
import { Button } from "@/components/ui/button";
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

## API 통신 기초 설정

현재는 기초 세팅 단계이므로 공통 HTTP 라이브러리/인터셉터는 미정 상태로 둡니다. 통신 인터페이스는 `api/` 내 도메인별 모듈을 계약 확정 후 추가합니다.

```text
Page → Feature → Domain API (예정)
     → Backend
     → 화면 갱신
```

도메인 API 파일(`postApi.js`, `trialApi.js` 등)의 URL과 payload는 Backend 계약 확정 전까지 만들지 않습니다.
