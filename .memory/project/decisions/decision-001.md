# Decision 001 - Review ownership check and error handling

**Date:** 2026-10-17
**Review by:** 2027-01-15
**Status:** Superseded for current state by decision-005 (2026-10-18); approach and rejected alternatives still apply
**Scope:** Workflow-scoped (FEATURES.md item 2). Archive when item 2 is fully merged; promote lasting rules to `.memory/knowledge/`.

**Decision:** Fix the review-ownership gap with a derived repository method `findByIdAndUserId` followed by `delete(review)`, returning 404 (never 403) when the review is missing or belongs to another user, and handle errors with custom unchecked exceptions in `fashionmate_backend.exceptions` mapped by a single `@RestControllerAdvice` (`GlobalExceptionHandler`) to a `{"message": "..."}` body.

**Rationale:** `findByIdAndUserId` follows the existing `findAllByUserId` convention and checks ownership and loads the entity in one query, and `delete(review)` avoids the extra lookup `deleteById` performs. Returning the same 404 for "not found" and "not yours" stops callers probing which review IDs exist. One advice class gives every error the consistent shape FEATURES.md asks for while keeping exception messages clean, so existing `hasMessage(...)` test assertions keep working.

**Alternatives rejected:** Loading with `findById` and comparing `review.getUser().getId()` in the controller (more code, null-safety concerns, same result). Throwing `ResponseStatusException` directly (Spring's default error body isn't `{"message": ...}`, and its `getMessage()` becomes `404 NOT_FOUND "..."`, breaking the existing assertions).

**Current state:** Implemented and tested on 2026-10-18 (see decision-005 for status and remaining gaps, decision-004 for the full status mapping, decision-003 for how tests are run). The 2026-10-17 "next step" (ReviewControllerTest) and the deferred UserController/`checkUserExist` work are both done.
