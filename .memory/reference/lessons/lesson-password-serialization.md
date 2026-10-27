---
classification: internal
project: proj-lessons
doc_type: lesson
---

## Lesson: hide the password with WRITE_ONLY, not JsonIgnore

What happened: `POST /api/users` returned the full User entity, including the plaintext password. While fixing it we found a bigger leak: Review and StyleLens embed User, so every review response also returned the reviewer's password.

What we learned: annotating `User.password` with `@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)` hides it from every serialized response in one place while still letting signUp and signIn read it from their request bodies. `@JsonIgnore` was rejected because it blocks deserialization too and would break login. Response DTOs are the more robust long-term option but were deferred.

How to apply it: any new field holding a secret gets the same WRITE_ONLY annotation in the same change. Keep the serialization tests (`UserTest.serializingUserOmitsPassword`, `ReviewTest.serializingReviewOmitsReviewersPassword`); they fail if the annotation is ever removed.
