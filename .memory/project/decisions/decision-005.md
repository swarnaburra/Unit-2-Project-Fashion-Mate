# Decision 005 - FEATURES.md item 2 complete; known gaps it leaves

**Date:** 2026-10-18
**Review by:** 2026-11-17 (30 days; status entry)
**Status:** Active
**Scope:** Workflow-scoped. Archive once item 2's commit is on `main` and each gap below is either picked up or explicitly dropped.

**Decision:** Treat FEATURES.md item 2 as done once committed: ownership check, global error handling, user/review status mapping (decision-004), and tests (29 passing, decision-003). decision-001 is superseded for current state and next step; its approach and rejected alternatives still apply.

**Rationale:** Both acceptance-criteria bullets in FEATURES.md item 2 are met and verified by unit tests: deleting someone else's review returns 404 (not 200), duplicate signup returns 409, and every mapped error has the `{"message": ...}` shape. A future session should not reopen this item or redo its tests.

**Known gaps deliberately left open (not part of item 2):**
1. ~~`POST /api/users` (`UserController.createUser`) still returns the full `User` entity, including the plaintext password (finding F9 in `docs/context-run/final-report.md`), and bypasses `signUp`'s validation.~~ **Resolved and verified 2026-10-19 (32 tests passing; see decision-007)**: `User.password` now carries `@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)`, so it's never serialized in any response (`createUser`, and `Review`/`StyleLens` which embed `User`), while `@RequestBody User` deserialization for `signUp`/`signIn` is unaffected. Chosen over introducing response DTOs (deferred, see below) to stay in scope. Tests added: `UserTest.serializingUserOmitsPassword`, `UserTest.deserializingUserStillSetsPassword`, `ReviewTest.serializingReviewOmitsReviewersPassword`. `createUser` still bypasses `signUp`'s validation (unchanged) and `POST /api/users` was intentionally not removed — out of scope for this fix.
2. Passwords are stored and compared in plaintext (S2); belongs to FEATURES.md item 1 (JWT + BCrypt).
3. ~~`StyleLensController` still throws raw `Exception` (500) for a missing user and non-numeric `userId`.~~ **Resolved and verified 2026-10-20 (34 tests passing)**: `StyleLensController.processImage` no longer throws raw `Exception("Wrong user")` for a missing user, nor lets an unhandled `NumberFormatException` propagate for a non-numeric `userId`. New `InvalidUserIdException` mapped to 400 in `GlobalExceptionHandler` (message "Invalid user id"; see decision-004); the missing-user case reuses the existing `UserNotFoundException` (404, "User does not exist") rather than adding a new exception. Tests: fixed the previously-weak `StyleLensControllerTest` regression test, added one new `StyleLensControllerTest` case, added one new `GlobalExceptionHandlerTest` case. Backend Maven suite run via the decision-003 Docker command, human-confirmed 2026-10-20: `StyleLensControllerTest` 2/2, `GlobalExceptionHandlerTest` 6/6, full suite 34 passing, 0 failures, 0 errors, BUILD SUCCESS (up from the decision-003 baseline of 32). Reviewer verdict PASS, 0 findings.
4. The JSON shape is verified by a plain unit test of the handler, not end-to-end over HTTP. A MockMvc/`@WebMvcTest` check is an option if a task asks for it (coding standard 6).
5. Response DTOs (Option B considered for gap 1, 2026-10-20): would fully decouple the wire format from the `User`/`Review`/`StyleLens` JPA entities instead of relying on per-field `@JsonProperty` annotations. Deferred by explicit human decision; not started.

**Alternatives rejected:** Folding gap 1 into item 2 (it's a different endpoint and finding, and would blur the item's scope, against coding standard 8). Leaving item 2 "in progress" until all gaps close (would make decision-001's next step wrong and invite redoing finished work).

**Next recommended action:** Gaps 1 and 3 are resolved. Ask the human which to take next: the open `POST /api/users` question (decision-008), gap 4 (MockMvc end-to-end HTTP check), gap 5 (response DTOs), or gap 2 / FEATURES.md item 1 (JWT + BCrypt, large).
