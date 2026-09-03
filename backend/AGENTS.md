# Backend Instructions

These instructions apply to `backend/` in addition to the repository-level `AGENTS.md`.

## Current state

- A minimal Java 21 Spring Boot Gradle project exists.
- Web, Validation, and WebSocket/STOMP dependencies are present without application configuration.
- JPA, PostgreSQL, Database schema, WebSocket/STOMP endpoints, REST APIs, persistence code, and Docker configuration are not present yet.
- 최근 Frontend PR(#274)만 반영되었으며, Backend는 범위 미적용 상태입니다.

## Change boundaries

- Do not create build files, dependencies, containers, Entities, or migrations before their Task and design decision exist.
- Do not define REST paths, status codes, WebSocket endpoints, STOMP destinations, or message payloads before interface review.
- Do not implement Mock AI or provider adapters before Prompt and JSON Schema approval.
- Keep Frontend files out of Backend Tasks and PRs.

## Commands

- Run: `./gradlew bootRun`
- Test: `./gradlew test`
- Lint and test: `./gradlew check`

## Code review rules

- Flag controllers that contain business rules or expose persistence objects directly.
- Flag Database structures that do not match the approved ERD.
- Flag REST, STOMP, or AI contracts that do not match the approved specification.
- Flag secrets, sensitive prompts, personal data, or credentials in code, configuration, logs, and tests.
