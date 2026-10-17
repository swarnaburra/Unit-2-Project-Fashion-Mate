# Project Memory Index

Last updated: 2026-10-17

## Active entries

- `decisions/decision-001.md` — FEATURES.md item 2: review ownership via `findByIdAndUserId` + 404 (not 403), errors via custom exceptions + one `@RestControllerAdvice` with `{"message": ...}`; includes current state, next step (ReviewControllerTest only), and deferred UserController work. Read before working on reviews, error handling, or UserController. Recorded 2026-10-17. Review by 2027-01-15.

- `decisions/decision-002.md` — Test layout: tests live in a separate test tree mirroring source packages (backend: existing Maven `src/test/java`), never alongside source files; no `/tests` move or `pom.xml` change. Read before adding or moving any test files. Recorded 2026-10-17. Review by 2027-01-15.

- `../knowledge/coding-standards.md` — Coding standards for this project (exceptions and error body, 404-not-403, no secrets or passwords in responses, repository naming, Mockito test style, tests for every fix, scope discipline). Human-maintained, read-only. Last reviewed 2026-10-17.

## Archived entries

(none yet)

## Pruning schedule

- Workflow-scoped entries: archived when the branch merges to main
- Project-scoped entries: reviewed every 90 days
