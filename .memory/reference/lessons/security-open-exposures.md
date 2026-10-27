---
classification: confidential
project: proj-lessons
doc_type: lesson
---

## Lesson: authentication weaknesses that are still open

What happened: reviews of the FashionMate backend found exposures that remain unpatched because they depend on the JWT and BCrypt work in FEATURES.md item 1.

What we learned: passwords are still stored and compared in plaintext in the users table, so anyone with database access can read every user's password. Endpoints still accept any numeric user id in the path with no proof that the caller is that user, so a caller who changes the id can read another user's reviews and StyleLens results. `POST /api/users` still bypasses signup validation. All controllers allow cross-origin requests from any site.

How to apply it: treat these as known open security weaknesses when planning authentication work. Do not describe how to exploit them outside the security review, and do not quote this document in public material.
