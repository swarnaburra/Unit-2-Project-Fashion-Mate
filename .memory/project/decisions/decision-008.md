# Decision 008 - Frontend API contract the backend must keep

**Date:** 2026-10-19
**Review by:** 2027-01-17
**Status:** Active
**Scope:** Project-scoped. Re-check whenever a controller's request/response shape changes or the frontend adds a `fetch` call. Source of truth: `fashionmate-frontend/fashion-app/src` (`context/UserContext.jsx`, `components/*.jsx`).

**Decision:** Treat the following as the backend's public contract with the React frontend (as read on 2026-10-19). Backend changes may alter anything else, but changing these needs a matching frontend change in the same task:

| Frontend file | Call | What it relies on |
|---|---|---|
| `context/UserContext.jsx` | `POST /api/users/signup`, `PUT /api/users/signin` | Success: JSON body is the numeric user id (stored in `localStorage.userId`). Failure: any non-OK status becomes a generic "Signup failed"/"Login failed"; the status code and `{"message"}` body are not read. |
| `components/About.jsx` | `GET`/`POST /api/reviews/{userId}`, `DELETE /api/reviews/{userId}/review/{reviewId}` | Reads only `id`, `name`, `rating`, `comment` from each review; never reads the embedded `user`. Checks `response.ok` only. |
| `components/GlamUp.jsx`, `OutfitTip.jsx`, `StyleLensUpload.jsx` | `GET /api/glamup`, `GET /api/outfit-tip`, `POST /api/stylelens/processImage/{userId}` | Unchanged by item 2 or decision-007. |

All calls use the hardcoded base URL `http://localhost:8080` (FEATURES.md item 3 would externalize it).

**Consequences:**
- The 400/409/401/404 mapping in decision-004 is safe for the current UI, which ignores status codes; better per-status messages would be a frontend change.
- Hiding `password` (decision-007), or later dropping `user` from review responses entirely, doesn't break the UI.
- `POST /api/users` (`createUser`) is **not called by the frontend**. It still bypasses signup validation (duplicate email, missing fields).

**Open question (needs a human decision):** remove `POST /api/users`, or keep it and route it through `signUp`'s validation? Not decided; don't change it without asking.

**Alternatives rejected:** Re-reading the frontend on every backend change (slow, and a fresh session wouldn't know which files matter); relying on the PRD (it doesn't describe the actual fetch calls).
