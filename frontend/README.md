# Frontend

Vue 3와 Vite 기반의 최소 Frontend 프로젝트입니다. Vue Router, Axios와 STOMP Client는 의존성만 설치했으며 Router, API, STOMP 설정과 기능 코드는 아직 추가하지 않았습니다. 상태 관리 Library는 미정입니다.

## 최초 실행

```bash
cp .env.example .env
npm install
npm run dev
```

기본 Backend 주소는 `VITE_API_BASE_URL=http://localhost:8080`입니다. 브라우저에 포함되는 `VITE_` 환경변수에는 비밀정보를 저장하지 않습니다.

## 검증

```bash
npm run lint
npm run build
```

전체 구조와 협업 컨벤션은 [프로젝트 README](../README.md)를 따릅니다.
