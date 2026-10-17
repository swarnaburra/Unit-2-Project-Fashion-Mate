# Decision 001 - Review ownership check and error handling

**Date:** 2026-10-17
**Review by:** 2027-01-15
**Status:** Active
**Scope:** Workflow-scoped (FEATURES.md item 2). Archive when item 2 is fully merged; promote lasting rules to `.memory/knowledge/`.

**Decision:** Fix the review-ownership gap with a derived repository method `findByIdAndUserId` followed by `delete(review)`, returning 404 (never 403) when the review is missing or belongs to another user, and handle errors with custom unchecked exceptions in `fashionmate_backend.exceptions` mapped by a single `@RestControllerAdvice` (`GlobalExceptionHandler`) to a `{"message": "..."}` body.

**Rationale:** `findByIdAndUserId` follows the existing `findAllByUserId` convention and checks ownership and loads the entity in one query, and `delete(review)` avoids the extra lookup `deleteById` performs. Returning the same 404 for "not found" and "not yours" stops callers probing which review IDs exist. One advice class gives every error the consistent shape FEATURES.md asks for while keeping exception messages clean, so existing `hasMessage(...)` test assertions keep working.

**Alternatives rejected:** Loading with `findById` and comparing `review.getUser().getId()` in the controller (more code, null-safety concerns, same result). Throwing `ResponseStatusException` directly (Spring's default error body isn't `{"message": ...}`, and its `getMessage()` becomes `404 NOT_FOUND "..."`, breaking the existing assertions).

**Current state and next step (as of 2026-10-17):**
- Done, uncommitted: `ReviewRepository.findByIdAndUserId`, `ReviewNotFoundException`, `GlobalExceptionHandler` (handles only `ReviewNotFoundException`), new `ReviewController.deleteReview`.
- Next: update `ReviewControllerTest` only: fix `deleteReviewDeletesWhenUserExists` (stub `findByIdAndUserId`, verify `delete(review)`), add other-user and nonexistent-review 404 tests, add `never().delete(any())` to the user-missing test. Then run the tests.
- Deferred, do not start yet: UserController mapping (409 duplicate email, 401 bad login, 400 missing fields) and `UserControllerTest`; `checkUserExist` deliberately still throws a raw `RuntimeException` (500) for now.
- Environment: the `agentic_engineer_2` container has no JDK/Maven and the Maven wrapper properties are missing, so tests must be run outside the container for now.
