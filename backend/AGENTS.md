# Backend Instructions

These instructions apply to `backend/` in addition to the repository-level `AGENTS.md`.

## Current state

- A Java 21 and Spring Boot Gradle project exists.
- Web, Validation, Data JPA, Flyway, PostgreSQL, springdoc-openapi, and WebSocket/STOMP dependencies are configured.
- JPA Entities, Flyway migrations, PostgreSQL connection, REST APIs, WebSocket/STOMP endpoints, Mock AI services, persistence code, and tests are present.
- The root Compose file runs PostgreSQL, Backend, and Frontend together; `backend/compose.yaml` remains PostgreSQL-only for Backend development.

## Change boundaries

- Do not create build files, dependencies, containers, Entities, or migrations before their Task and design decision exist.
- Do not define REST paths, status codes, WebSocket endpoints, STOMP destinations, or message payloads before interface review.
- Do not implement Mock AI or provider adapters before Prompt and JSON Schema approval.
- Keep Frontend files out of Backend Tasks and PRs.
- Create Backend work branches from the latest `backend` branch and open Backend PRs back into `backend`.

## Commands

- Run: `./gradlew bootRun`
- Test: `./gradlew test`
- Lint and test: `./gradlew check`

## Code review rules

- Flag controllers that contain business rules or expose persistence objects directly.
- Flag Database structures that do not match the approved ERD.
- Flag REST, STOMP, or AI contracts that do not match the approved specification.
- Flag secrets, sensitive prompts, personal data, or credentials in code, configuration, logs, and tests.
