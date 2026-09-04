# Documentation Instructions

These instructions apply to `docs/` in addition to the repository-level `AGENTS.md`.

## Document ownership

- Store only approved, versioned Architecture, ERD, API, Database, WebSocket/STOMP, AI contract, and QA documents in `docs/`. Planning, decisions, progress, and task state remain in the canonical tools listed in the root `AGENTS.md`.
- Keep assets here only when an external document intentionally references their stable Repository URL.
- Keep one responsibility per document and mark undecided content as `미정`.

## Change boundaries

- Do not create empty or speculative contract documents.
- Distinguish assignment requirements, team decisions, implemented behavior, and verified results.
- Do not rename or remove `assets/github-guide/` files without updating and verifying the Notion pages that use their GitHub Raw URLs.
- Follow the repository branch workflow: `frontend` and `backend` are area branches from `dev`, and cross-area documentation should stay aligned with promotions into `dev`.

## Code review rules

- Flag documents that claim unexecuted tests, integrations, or demos as complete.
- Flag API, Database, STOMP, or AI examples that disagree with approved contracts or current code.
- Flag duplicated operational information that should remain in Notion or GitHub Project.
