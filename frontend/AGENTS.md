# Frontend Instructions

These instructions apply to `frontend/` in addition to the repository-level `AGENTS.md`.

## Current state

- A minimal Vue 3 and Vite JavaScript project exists.
- Vue Router, Axios, and STOMP Client are installed as dependencies only; their application configuration is not implemented.
- Do not choose or install state-management, test, or other optional libraries without an explicit decision or Issue.

## Change boundaries

- Do not implement screens before the user flow and Google Stitch/Figma design are approved.
- Do not define REST payloads or STOMP destinations before the shared interface is approved.
- Keep Backend files out of Frontend Tasks and PRs.

## Commands

- Install: `npm install`
- Develop: `npm run dev`
- Build: `npm run build`

## Code review rules

- Flag UI behavior that is not supported by approved requirements or designs.
- Flag duplicated API or realtime clients and contracts that diverge from the approved specification.
- Flag secrets or sensitive data placed in browser-visible configuration or console output.
