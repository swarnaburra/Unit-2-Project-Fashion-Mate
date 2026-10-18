# Decision 005 - FEATURES.md item 2 complete; known gaps it leaves

**Date:** 2026-10-18
**Review by:** 2026-11-17 (30 days; status entry)
**Status:** Active
**Scope:** Workflow-scoped. Archive once item 2's commit is on `main` and each gap below is either picked up or explicitly dropped.

**Decision:** Treat FEATURES.md item 2 as done once committed: ownership check, global error handling, user/review status mapping (decision-004), and tests (29 passing, decision-003). decision-001 is superseded for current state and next step; its approach and rejected alternatives still apply.

**Rationale:** Both acceptance-criteria bullets in FEATURES.md item 2 are met and verified by unit tests: deleting someone else's review returns 404 (not 200), duplicate signup returns 409, and every mapped error has the `{"message": ...}` shape. A future session should not reopen this item or redo its tests.

**Known gaps deliberately left open (not part of item 2):**
1. `POST /api/users` (`UserController.createUser`) still returns the full `User` entity, including the plaintext password (finding F9 in `docs/context-run/final-report.md`), and bypasses `signUp`'s validation. Highest-priority follow-up; violates coding standard 4.
2. Passwords are stored and compared in plaintext (S2); belongs to FEATURES.md item 1 (JWT + BCrypt).
3. `StyleLensController` still throws raw `Exception` (500) for a missing user and non-numeric `userId`.
4. The JSON shape is verified by a plain unit test of the handler, not end-to-end over HTTP. A MockMvc/`@WebMvcTest` check is an option if a task asks for it (coding standard 6).

**Alternatives rejected:** Folding gap 1 into item 2 (it's a different endpoint and finding, and would blur the item's scope, against coding standard 8). Leaving item 2 "in progress" until all gaps close (would make decision-001's next step wrong and invite redoing finished work).

**Next recommended action:** Ask the human which follow-up to take next; the default recommendation is gap 1 (stop `createUser` returning the password).
