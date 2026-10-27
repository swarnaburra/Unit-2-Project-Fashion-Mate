# Decision 004 - HTTP status mapping for user and review errors

**Date:** 2026-10-18
**Review by:** 2027-01-16
**Status:** Active
**Scope:** Project-scoped. Revisit when JWT authentication (FEATURES.md item 1) lands, because login and ownership errors will change.

**Decision:** Each user-facing error has its own unchecked exception in `fashionmate_backend.exceptions`, mapped in `GlobalExceptionHandler` to a fixed status with body `{"message": "..."}`. Existing message texts were kept unchanged:

| Exception | Thrown by | Status | Message |
|---|---|---|---|
| `MissingFieldsException` | `UserController.signUp` (missing name/email/password) | 400 | "Add the missing fields" |
| `MissingFieldsException` | `UserController.signIn` (missing email/password) | 400 | "Please enter your correct email and password" |
| `EmailAlreadyInUseException` | `UserController.signUp` | 409 | "Email already in use" |
| `InvalidCredentialsException` | `UserController.signIn` (no user for email+password) | 401 | "User not found" |
| `UserNotFoundException` | `ReviewController.checkUserExist` (GET/POST/DELETE reviews); also `StyleLensController.processImage` (missing user, since 2026-10-20) | 404 | "User does not exist" |
| `ReviewNotFoundException` | `ReviewController.deleteReview` (missing or not yours) | 404 | "Review not found" |
| `InvalidUserIdException` | `StyleLensController.processImage` (non-numeric `userId`) | 400 | "Invalid user id" |
| `GlamUpNotFoundException` | `GlamUpController.getGlamUpBy` (empty GlamUp collection) | 404 | "No glam up content found" |

`signUp`/`signIn` no longer declare `throws Exception`. There is deliberately **no** catch-all `@ExceptionHandler(Exception.class)`.

**Rationale:** FEATURES.md item 2 requires 409 for duplicate signup and a consistent error shape. Bad login is 401 (not 404) because the request is an authentication failure, and it uses one exception and one message for both "unknown email" and "wrong password" so callers can't discover which emails are registered. Keeping the old messages meant existing `hasMessage(...)` assertions stayed valid; the tests now also assert the specific exception types, and `GlobalExceptionHandlerTest` checks every status and body (verified 2026-10-18, 29 tests passing). No catch-all handler, because it would turn Spring's own 400s (for example malformed JSON) into 500s.

**Alternatives rejected:**
- 404 for bad login: suggests a missing resource rather than an auth failure, and misleads the frontend.
- One generic `ApiException(status, message)` class: fewer files, but the status moves back into controller code, which `coding-standards.md` keeps in the handler only.
- A catch-all `Exception` handler returning 500 with `{"message": ...}`: hides framework 4xx errors.
- Changing the message texts (e.g. "Invalid email or password"): clearer, but would change what the frontend displays; left for a separate frontend-coordinated change.

**Update 2026-10-20:** The `StyleLensController` gap noted below is resolved (decision-005 gap 3). `StyleLensController.processImage` no longer throws raw `Exception("Wrong user")` (500) or lets `NumberFormatException` propagate (500). It now reuses `UserNotFoundException` (404, table row above) for a missing user and throws the new `InvalidUserIdException` (400, table row above) for a non-numeric `userId`. Verified 2026-10-20, 34 tests passing (see decision-005).

**Update 2026-10-27 (run-glamup-404):** Review finding — `GlamUpController.getGlamUpBy` threw `NoSuchElementException` (HTTP 500) when the GlamUp table is empty. Fixed with a new `GlamUpNotFoundException` mapped in `GlobalExceptionHandler` to 404 with message "No glam up content found" (table row above); `GlamUpController` now throws it instead of calling `.getFirst()` unconditionally. Reviewer verdict PASS (1 low-severity, non-blocking finding: the new test's assertion literal, "GlamUp not found", doesn't exactly match the production message above — cosmetic only, not corrected in this run). Verified 2026-10-27: `GlamUpControllerTest` 2/2, `GlobalExceptionHandlerTest` 7/7, full suite 35 passing, 0 failures, 0 errors, BUILD SUCCESS (up from the 34-test baseline of 2026-10-20), host-run via decision-003's Docker command.
