# Project Memory Index

Last updated: 2026-10-18

## Active entries

- `decisions/decision-001.md` — FEATURES.md item 2 approach: ownership via `findByIdAndUserId` + `delete(review)`, 404 not 403, custom exceptions + one `@RestControllerAdvice` with `{"message": ...}`; rejected findById+compare and ResponseStatusException. Current state superseded by decision-005. Recorded 2026-10-17. Review by 2027-01-15.

- `decisions/decision-002.md` — Test layout: tests live in a separate test tree mirroring source packages (backend: existing Maven `src/test/java`), never alongside source files; no `/tests` move or `pom.xml` change. Read before adding or moving any test files. Recorded 2026-10-17. Review by 2027-01-15.

- `decisions/decision-003.md` — How to run backend tests: `maven:3.9-eclipse-temurin-21` Docker image from PowerShell, `fashionmate-m2` cache volume, `contextLoads` excluded (needs MySQL); dev container has no JDK. Baseline 29 passing. Read before running or reporting on tests. Recorded 2026-10-18. Review by 2027-01-16.

- `decisions/decision-004.md` — Error status mapping: MissingFields 400, EmailAlreadyInUse 409, InvalidCredentials 401 (same message for unknown email and wrong password), UserNotFound 404, ReviewNotFound 404; no catch-all handler; messages unchanged. Read before adding or changing any error handling. Recorded 2026-10-18. Review by 2027-01-16.

- `decisions/decision-005.md` — Status: FEATURES.md item 2 complete (29 tests passing); open gaps: createUser returns the password (F9, recommended next), plaintext passwords (item 1), StyleLens raw exceptions, no end-to-end HTTP check. Read before choosing the next backend task. Recorded 2026-10-18. Review by 2026-11-17.

- `../knowledge/coding-standards.md` — Coding standards for this project (exceptions and error body, 404-not-403, no secrets or passwords in responses, repository naming, Mockito test style, tests for every fix, scope discipline). Human-maintained, read-only. Last reviewed 2026-10-17.

- `../reference/INDEX.md` — Pointers to large, on-demand documents (FEATURES.md, review final report, PRDs, rubrics, iteration log, memory architecture) with when to consult each. Human-maintained, read-only. Last reviewed 2026-10-17.

## Archived entries

(none yet)

## Pruning schedule

- Workflow-scoped entries (e.g. decision-001, tied to FEATURES.md item 2): moved to Archived when the feature's final commit merges to main; lasting rules are promoted to knowledge/ by the human first.
- Project-scoped entries (e.g. decision-002): reviewed by their "Review by" date (90 days); flag any past-due entry and ask before acting on it.
- Superseded entries: replaced in place (update the existing entry), never duplicated.
