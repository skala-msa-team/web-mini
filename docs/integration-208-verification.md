# #208 Integration Spike 검증

## 범위

기존 계약을 사용해 다음 경로를 확인한다.

- Frontend Axios → `GET /api/v1/trials` → 응답 렌더링
- Backend JPA/Flyway → PostgreSQL 저장·조회
- Frontend STOMP `CONNECT`·`SUBSCRIBE` → `/topic/trials/{trialId}/chat`
- 채팅 저장 트랜잭션 `AFTER_COMMIT` → 두 클라이언트 브로드캐스트
- Backend Mock AI Adapter → 승인 JSON Schema DTO 역직렬화

## 실행

```bash
docker compose up -d --build
```

전체 Compose를 사용하지 않고 Backend 통합 테스트만 실행할 때:

```bash
cd backend
docker compose up -d postgres
DB_INTEGRATION_TEST=true ./gradlew test
./gradlew bootRun
```

다른 터미널에서 다음을 실행한다.

```bash
cd frontend
npm install
npm run dev
```

전체 Compose에서는 `http://localhost:8081/integration-spike`, 개별 개발에서는 `http://localhost:5173/integration-spike`를 두 브라우저에서 연다. 각 브라우저에서 CONNECT·SUBSCRIBE한 뒤 한 브라우저에서 `저장 후 이벤트 전송`을 누른다. 두 브라우저에 같은 `content`와 `messageSequence`가 표시되어야 한다.

## 검증 결과

2026-09-04 로컬 검증 결과:

- `DB_INTEGRATION_TEST=true ./gradlew clean test`: `BUILD SUCCESSFUL`
- `./gradlew check`: `BUILD SUCCESSFUL`
- `npm run lint`: 통과
- `npm run build`: 통과
- 루트 `docker compose up -d --build`: PostgreSQL healthy, Backend·Frontend 실행 확인
- `http://localhost:8081/`: `200 OK`
- `http://localhost:8081/api/v1/trials`: `200 OK`
- Mock AI `POST /api/v1/mock-ai/lawyer/questions`: 질문 3개, `schemaVersion=1.0` 확인
- `/integration-spike` REST: 두 브라우저에서 Axios 응답 렌더링 확인
- `/integration-spike` STOMP: 두 브라우저가 `CONNECTED` 후 같은 `Integration spike test event · sequence 1` 수신

`DB_INTEGRATION_TEST=true`가 없으면 PostgreSQL 의존 테스트는 의도적으로 skip되므로, skip을 성공으로 간주하지 않는다.

## 실패 시 확인

- `Connection refused`: PostgreSQL 컨테이너가 healthy인지 `docker compose ps`로 확인한다.
- `Flyway` 또는 schema 오류: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`가 compose 기본값과 일치하는지 확인한다.
- REST `5xx`: 전체 Compose에서는 `docker compose logs backend`, 개별 실행에서는 Backend가 `8080`에서 실행 중인지 확인한다.
- STOMP 연결 실패: Backend의 `/ws`와 Frontend의 `VITE_API_BASE_URL` 및 CORS 허용 origin을 확인한다.
- 메시지 미수신: 두 브라우저가 같은 Trial ID로 chat destination을 구독했는지 확인한다.
