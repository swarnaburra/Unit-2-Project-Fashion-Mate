# Project Memory Index

Last updated: 2026-10-20

## Active entries

- `decisions/decision-001.md` — FEATURES.md item 2 approach: ownership via `findByIdAndUserId` + `delete(review)`, 404 not 403, custom exceptions + one `@RestControllerAdvice` with `{"message": ...}`; rejected findById+compare and ResponseStatusException. Current state superseded by decision-005. Recorded 2026-10-17. Review by 2027-01-15.

- `decisions/decision-002.md` — Test layout: tests live in a separate test tree mirroring source packages (backend: existing Maven `src/test/java`), never alongside source files; no `/tests` move or `pom.xml` change. Read before adding or moving any test files. Recorded 2026-10-17. Review by 2027-01-15.

- `decisions/decision-003.md` — How to run backend tests: `maven:3.9-eclipse-temurin-21` Docker image from PowerShell, `fashionmate-m2` cache volume, `contextLoads` excluded (needs MySQL); dev container has no JDK. Baseline 34 passing (2026-10-20). Read before running or reporting on tests. Recorded 2026-10-18. Review by 2027-01-16.

- `decisions/decision-004.md` — Error status mapping: MissingFields 400, EmailAlreadyInUse 409, InvalidCredentials 401, UserNotFound 404 (now also StyleLens missing-user), ReviewNotFound 404, InvalidUserId 400 (new, StyleLens); no catch-all handler. Read before adding or changing any error handling. Recorded 2026-10-18, updated 2026-10-20. Review by 2027-01-16.

- `decisions/decision-005.md` — Status: FEATURES.md item 2 complete (29 tests); gap 1 (password leak) resolved/verified 2026-10-19 (decision-007); gap 3 (StyleLens raw exceptions) resolved/verified 2026-10-20 (34 tests passing); open: item 1 (plaintext passwords/JWT), gap 4 (no end-to-end HTTP check), gap 5 (response DTOs). Read before choosing the next backend task. Recorded 2026-10-18, updated 2026-10-20. Review by 2026-11-17.

- `decisions/decision-006.md` — API connection approach: service account; the key lives only in the `ANTHROPIC_API_KEY` environment variable and is never written to memory, knowledge, or code. (Replaces decision-bad.md, which contained a hardcoded fake key.) Recorded 2026-10-18. Review by 2027-01-16.

- `decisions/decision-007.md` — Password never serialized: `@JsonProperty(access = WRITE_ONLY)` on `User.password` covers createUser and the Review/StyleLens embeds; new secret fields need the same; DTOs deferred, `@JsonIgnore` rejected (breaks signup). Read before touching `User`, any response containing a user, or auth. Recorded 2026-10-19. Review by 2027-01-17.

- `decisions/decision-008.md` — Frontend API contract: which endpoints `UserContext.jsx`/`About.jsx`/others call and exactly which response fields and statuses they rely on; `POST /api/users` is unused by the frontend (open question: remove or validate). Read before changing any controller's request/response shape. Recorded 2026-10-19. Review by 2027-01-17.

- `../knowledge/coding-standards.md` — Coding standards for this project (exceptions and error body, 404-not-403, no secrets or passwords in responses, repository naming, Mockito test style, tests for every fix, scope discipline). Human-maintained, read-only. Last reviewed 2026-10-17.

- `../reference/INDEX.md` — Pointers to large, on-demand documents (FEATURES.md, review final report, PRDs, rubrics, iteration log, memory architecture) with when to consult each. Human-maintained, read-only. Last reviewed 2026-10-17.

## Archived entries

(none yet)

## Pruning schedule

- Workflow-scoped entries (e.g. decision-001, tied to FEATURES.md item 2): moved to Archived when the feature's final commit merges to main; lasting rules are promoted to knowledge/ by the human first.
- Project-scoped entries (e.g. decision-002): reviewed by their "Review by" date (90 days); flag any past-due entry and ask before acting on it.
- Superseded entries: replaced in place (update the existing entry), never duplicated.
