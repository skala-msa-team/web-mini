# Repository Instructions

## Project scope

- This repository is for SKALA Full-Stack Engineering Mini Project Team 6.
- Follow the approved Notion documents for product scope and interfaces, the GitHub Project for work status, and Repository code plus executed verification for actual implementation state.
- Do not add optional application dependencies, Docker configuration, environment-variable files, or feature code unless the user explicitly requests the relevant Task.
- Treat undecided product features and interfaces as `미정`; do not infer them from examples.

## Project resources and sources of truth

- [Repository](https://github.com/skala-msa-team/web-mini): source code and versioned project configuration.
- [GitHub Project execution board](https://github.com/orgs/skala-msa-team/projects/3/views/6): assignees, priority, iteration, and status.
- [Notion](https://confused-dietician-c17.notion.site/mini-Project-6-3cd7caa087bd808caf1bc28791f745e2?pvs=73): product planning, technical decisions, schedules, meetings, and R&R.
- [Google Stitch](https://stitch.withgoogle.com/projects/416001617538729018) and [Figma](https://www.figma.com/design/kcaV9To7uU5HQHXDcbssGL/Untitled?node-id=0-1): approved design sources.
- Root `README.md`: Git, naming, execution, and collaboration conventions.
- Closest nested `AGENTS.md`: directory-specific instructions; closer files override this file only within their directory.

## Tracking rules

- Treat the links above as canonical resource locations, not copies of their live content.
- Do not duplicate live progress in `AGENTS.md`; update a link only when its canonical resource changes.

## Repository boundaries

- Keep Frontend work in `frontend/`, Backend work in `backend/`, and approved design documents in `docs/`.
- Do not mix Frontend and Backend changes in one Task, Branch, Commit, or PR.
- Handle cross-area changes in a separate Integration Task after each area branch is merged and promoted to `dev`.
- Preserve unrelated user changes.

## Git workflow

- Keep `frontend` and `backend` as long-lived area branches created from the latest `dev`.
- Create Frontend work branches from the latest `frontend` and Backend work branches from the latest `backend`.
- Create Integration work branches from the latest `dev` after the required Frontend and Backend changes are merged.
- Use `type/작업영역-이슈번호-영어-작업명` for branch names.
- Use `type(scope): 한국어 제목` for commits; scope is required.
- Link one Issue to one Branch and one PR.
- Open Frontend work PRs into `frontend` and Backend work PRs into `backend`.
- Promote verified `frontend` and `backend` changes into `dev`; use `dev` to `main` only for final integration.
- Require at least one approving review before merging a PR into `frontend`, `backend`, or `dev`.
- `main` has no required-approval protection rule, but team convention allows only final promotion from `dev`, not direct work-branch merges.
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
