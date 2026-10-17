# Coding Standards

Last reviewed: 2026-10-17
Review by: 2027-01-15
Maintained by: swarna

These standards apply to all code in this project. The
agent should consult this file before writing or reviewing
any code. These rules are set by humans and the agent
should never modify this file.

## 1. Use custom exceptions, not raw Exception or RuntimeException
New error cases throw a specific unchecked exception from `fashionmate_backend.exceptions` (for example `ReviewNotFoundException`). Don't throw raw `Exception`/`RuntimeException` in new code, and don't use `ResponseStatusException`.

## 2. Map errors in one place with a consistent body
`GlobalExceptionHandler` (`@RestControllerAdvice`) is the only place that turns exceptions into HTTP status codes. Every error response body has the shape `{"message": "..."}`.

## 3. Ownership failures return 404, not 403
When a resource is missing or belongs to another user, return the same 404 and message for both cases, so callers can't probe which IDs exist.

## 4. Never expose passwords or secrets in responses or files
Never return the `User` entity (which includes the password) from an endpoint. Secrets come only from environment variables (`DB_USERNAME`, `DB_PASSWORD`, `GEMINI_API_KEY`); never hardcode them in `application.properties` or commit `.env`, `google_cred.json`, or similar files.

## 5. Follow existing repository naming conventions
Use Spring Data derived query methods that match the existing style (`findAllByUserId`, `findByIdAndUserId`, `existsByEmail`) instead of custom queries or controller-side filtering.

## 6. Controller unit tests use plain Mockito and AssertJ
Controller tests use `@ExtendWith(MockitoExtension.class)` with `@InjectMocks`/`@Mock`, and assert errors with `assertThatThrownBy(...).isInstanceOf(...).hasMessage(...)`. Add MockMvc tests only when a task explicitly asks for an end-to-end HTTP check.

## 7. Every behavior change comes with a test that proves it
A change that fixes a bug or closes a gap includes a test that would fail on the old code (for example, a delete-by-another-user test that asserts no delete happens).

## 8. Keep changes inside the task's scope
Work on one `FEATURES.md` item or one agreed step at a time. Don't change files outside that scope, or start deferred work, without asking first.
