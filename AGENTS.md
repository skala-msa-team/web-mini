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

## Project resources

- Repository: https://github.com/skala-msa-team/web-mini
- GitHub Project: https://github.com/orgs/skala-msa-team/projects/3/views/1
- Notion: https://confused-dietician-c17.notion.site/mini-Project-6-3cd7caa087bd808caf1bc28791f745e2?pvs=73
- Google Stitch: https://stitch.withgoogle.com/projects/416001617538729018
- Figma: https://www.figma.com/design/kcaV9To7uU5HQHXDcbssGL/Untitled?node-id=0-1

## Tracking rules

- Treat the links above as canonical resource locations, not copies of their live content.
- Keep product requirements and decisions in Notion.
- Keep Issue ownership, priority, iteration, and status in GitHub Project.
- Keep approved screen designs in Google Stitch and Figma.
- Do not duplicate live progress in `AGENTS.md`; update a link only when its canonical resource changes.

## Recent applied work (for review basis)

- `feat/frontend-189-design-system` (PR #274): Frontend 기초 세팅 정비를 완료했으며, Axios 공통 인스턴스/인터셉터 및 `axios` 의존성을 제거한 상태에서 구조/디자인 토큰/문서를 정리했습니다.

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
- Require at least one approving review before merging a PR into `dev`.
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
