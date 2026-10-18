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
| `UserNotFoundException` | `ReviewController.checkUserExist` (GET/POST/DELETE reviews) | 404 | "User does not exist" |
| `ReviewNotFoundException` | `ReviewController.deleteReview` (missing or not yours) | 404 | "Review not found" |

`signUp`/`signIn` no longer declare `throws Exception`. There is deliberately **no** catch-all `@ExceptionHandler(Exception.class)`.

**Rationale:** FEATURES.md item 2 requires 409 for duplicate signup and a consistent error shape. Bad login is 401 (not 404) because the request is an authentication failure, and it uses one exception and one message for both "unknown email" and "wrong password" so callers can't discover which emails are registered. Keeping the old messages meant existing `hasMessage(...)` assertions stayed valid; the tests now also assert the specific exception types, and `GlobalExceptionHandlerTest` checks every status and body (verified 2026-10-18, 29 tests passing). No catch-all handler, because it would turn Spring's own 400s (for example malformed JSON) into 500s.

**Alternatives rejected:**
- 404 for bad login: suggests a missing resource rather than an auth failure, and misleads the frontend.
- One generic `ApiException(status, message)` class: fewer files, but the status moves back into controller code, which `coding-standards.md` keeps in the handler only.
- A catch-all `Exception` handler returning 500 with `{"message": ...}`: hides framework 4xx errors.
- Changing the message texts (e.g. "Invalid email or password"): clearer, but would change what the frontend displays; left for a separate frontend-coordinated change.

**Not covered:** `StyleLensController` still throws raw `Exception("Wrong user")` (500); out of scope for item 2.
