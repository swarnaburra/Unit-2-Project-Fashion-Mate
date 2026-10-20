# Decision 007 - Hide User.password from all JSON responses with WRITE_ONLY

**Date:** 2026-10-19
**Review by:** 2027-01-17
**Status:** Active (verified: 32 tests passing, 2026-10-19)
**Scope:** Project-scoped. Revisit when JWT/BCrypt (FEATURES.md item 1) lands or when response DTOs are introduced.

**Decision:** `User.password` carries `@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)` (`models/User.java`). Jackson still reads `password` from request bodies, so `signUp`/`signIn` (which take `@RequestBody User`) keep working, but never writes it into any response. This covers every place `User` is serialized: `POST /api/users` (F9), and `Review`/`StyleLens`, which embed `User`, so `GET`/`POST /api/reviews/{userId}` no longer return reviewers' passwords.

**Rules that follow from this:**
- Any new field on `User` (or any entity) that holds a secret must get the same `WRITE_ONLY` annotation, or be excluded from serialization, in the same change.
- Any new endpoint that returns `User`, `Review`, or `StyleLens` is covered automatically; don't add a separate workaround.
- Tests that pin this: `UserTest.serializingUserOmitsPassword`, `UserTest.deserializingUserStillSetsPassword`, `ReviewTest.serializingReviewOmitsReviewersPassword` (plain JUnit + Jackson `ObjectMapper`). Don't delete them.

**Rationale:** One annotation on the field named in finding F9 fixes all three controllers at once and doesn't touch request handling. The review leak was found while planning this fix (2026-10-19) and was not in the original review report. The frontend only reads `id`, `name`, `rating`, `comment` from reviews (`About.jsx`), so nothing it uses changes (see decision-008).

**Alternatives rejected:**
- Response DTOs (`UserResponse`, `ReviewResponse`): the more robust long-term shape (no future `User` field can leak by accident), but a multi-file change across three controllers. Deferred by explicit human decision, not rejected; tracked in decision-005.
- `@JsonIgnore` on `password`: also blocks *reading* the field, which would break signup and signin.
- Removing `POST /api/users`: the frontend doesn't call it, but removal changes the API and doesn't fix the review leak. Left as an open question (decision-008).
