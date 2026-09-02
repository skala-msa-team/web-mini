# Backend

Java 21과 Spring Boot로 생성한 Backend 기본 프로젝트입니다. Spring Web, Validation, WebSocket 의존성과 Checkstyle만 설정되어 있으며 Database, JPA, STOMP Endpoint와 API 기능은 구현하지 않았습니다.

## 실행

```bash
./gradlew bootRun
```

기본 포트는 `SERVER_PORT=8080`입니다. Spring Boot는 `.env` 파일을 자동으로 읽지 않으므로 값 변경이 필요하면 OS 환경변수를 사용합니다.

```bash
SERVER_PORT=8081 ./gradlew bootRun
```

## 검증

```bash
./gradlew check
```

전체 구조와 협업 컨벤션은 [프로젝트 README](../README.md)를 따릅니다.
