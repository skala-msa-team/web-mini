# Repository Instructions

## Project scope

- This repository is for SKALA Full-Stack Engineering Mini Project Team 6.
- The current phase includes collaboration documents, editor settings, and minimal Frontend and Backend project shells.
- Do not add optional application dependencies, Docker configuration, environment-variable files, or feature code unless the user explicitly requests the relevant Task.
- Treat undecided product features and interfaces as `미정`; do not infer them from examples.

## Sources of truth

- Use Notion for product planning, technical decisions, schedules, meetings, and R&R.
- Use GitHub Project for assignees, priority, iteration, and status.
- Use the Figma project linked in `README.md` for approved screen designs.
- Use `README.md` for Git, naming, and collaboration conventions.
- Use the closest nested `AGENTS.md` for directory-specific instructions.

## Repository boundaries

- Keep Frontend work in `frontend/`, Backend work in `backend/`, and approved design documents in `docs/`.
- Do not mix Frontend and Backend changes in one Task, Branch, Commit, or PR.
- Handle cross-area changes in a separate Integration Task after each area is merged into `dev`.
- Preserve unrelated user changes.

## Git workflow

- Create work branches from the latest `dev`.
- Use `type/작업영역-이슈번호-영어-작업명` for branch names.
- Use `type(scope): 한국어 제목` for commits; scope is required.
- Link one Issue to one Branch and one PR.
- Open normal work PRs into `dev`; use `dev` to `main` only for final integration.
- Do not commit, push, merge, or edit Notion unless the user explicitly asks.

## Security

- Never commit secrets, credentials, tokens, personal data, or local environment files.
- Do not place sensitive user input, complete prompts, or credentials in logs or fixtures.
- Add example environment files only when real configuration keys have been agreed.

## Verification

- Use the commands documented in the closest nested `AGENTS.md` and do not invent commands for tools that are not configured.
- Run checks relevant to the files changed and report actual results separately from unverified items.
- For document-only changes, check Markdown structure, links, naming consistency, and whitespace.

## Code review rules

- Flag changes that exceed the Issue scope or mix work areas.
- Flag undocumented API, Database, WebSocket/STOMP, or AI contract changes.
- Flag secrets, generated build output, and claims unsupported by executed verification.
