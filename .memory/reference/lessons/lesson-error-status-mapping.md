---
classification: internal
project: proj-lessons
doc_type: lesson
---

## Lesson: map errors to status codes in one GlobalExceptionHandler

What happened: controllers threw raw `Exception` and `RuntimeException`, so a duplicate signup, a bad login, a missing user, and a non-numeric id all came back as unstyled HTTP 500 responses.

What we learned: one unchecked exception per condition in `fashionmate_backend.exceptions`, mapped in a single `@RestControllerAdvice`, gives every error the `{"message": "..."}` body: MissingFields and InvalidUserId return 400, InvalidCredentials returns 401 with the same message for an unknown email and a wrong password, UserNotFound and ReviewNotFound return 404, EmailAlreadyInUse returns 409. There is deliberately no catch-all handler, because it would turn Spring's own 400 responses into 500s.

How to apply it: a new error case gets its own exception class and one handler method plus a `GlobalExceptionHandlerTest` case. Do not throw `ResponseStatusException` from controllers; its message format breaks the existing `hasMessage` assertions.
