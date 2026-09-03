# Frontend Instructions

These instructions apply to `frontend/` in addition to the repository-level `AGENTS.md`.

## Current state

- A Vue 3 and Vite JavaScript project with the approved design-system foundation and Vue Router structure exists.
- Shared live-trial STOMP connection and subscription helpers are implemented under `src/realtime/`.
- Axios is not installed. Do not select or implement a common HTTP client or interceptors without an explicit decision or Task.
- Do not choose or install state-management, test, or other optional libraries without an explicit decision or Issue.

## Change boundaries

- Do not implement screens before the user flow and Google Stitch/Figma design are approved.
- Do not define REST payloads or add STOMP destinations outside the approved shared interface.
- Keep Backend files out of Frontend Tasks and PRs.
- Create Frontend work branches from the latest `frontend` branch and open Frontend PRs back into `frontend`.

## Commands

- Install: `npm install`
- Develop: `npm run dev`
- Lint: `npm run lint`
- Build: `npm run build`

## Code review rules

- Flag UI behavior that is not supported by approved requirements or designs.
- Flag duplicated API or realtime clients and contracts that diverge from the approved specification.
- Flag secrets or sensitive data placed in browser-visible configuration or console output.
