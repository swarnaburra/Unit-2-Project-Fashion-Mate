# FashionMate — Target Codebase Feature Backlog

This file tracks the candidate features/improvements for FashionMate to be used as the
**Target Codebase** for the sandboxed multi-agent engineering environment course.
These are intentionally *not* implemented yet — they are the realistic, well-scoped work
items the agentic system will attempt, evaluate, and iterate on over the course.

Each item lists scope, why it's well-suited for agent work, and rough acceptance criteria.

---

## 1. Replace ad-hoc "userId" auth with real JWT-based authentication

**Area:** Backend (Spring Boot) + Frontend (React)

**Problem:** `UserController.signIn`/`signUp` return a raw numeric user ID, which the
frontend stores in `localStorage` as its only proof of identity. Any request to any
`/api/**` endpoint can pass any user ID with no verification.

**Scope:**
- Add Spring Security + JWT issuing/validation on the backend.
- Hash passwords with BCrypt instead of storing/comparing plaintext.
- Update `UserContext.jsx` to store and attach a bearer token instead of a raw ID.
- Protect existing endpoints so they resolve "current user" from the token, not from a
  path variable the client can forge.

**Acceptance criteria:**
- Signup/login return a signed JWT.
- Endpoints reject requests with missing/invalid/expired tokens (401).
- A user can no longer read/modify another user's reviews or StyleLens results by
  changing the `userId` in the URL.
- Passwords are never stored or compared in plaintext.

**Why it's well-scoped for agents:** self-contained, touches a known set of files, has a
crisp before/after behavior an agent (and a reviewer) can verify with a request/response.

---

## 2. Fix the review-ownership authorization gap + add global error handling

**Area:** Backend (Spring Boot)

**Problem:** `ReviewController.deleteReview(userId, reviewId)` checks that `userId`
exists, but never checks that `reviewId` actually belongs to that user before deleting
it — any authenticated (or even unauthenticated, per #1) caller can delete any review by
guessing its ID. Separately, `UserController`/`ReviewController` throw raw `Exception`/
`RuntimeException`, which Spring turns into unstyled 500 responses with no
`@ControllerAdvice` to translate them into proper 400/401/404 responses.

**Scope:**
- Add an ownership check in `deleteReview` (404/403 if the review doesn't belong to the
  user).
- Add a `@RestControllerAdvice` that maps known exceptions to appropriate HTTP status
  codes and a consistent JSON error body.

**Acceptance criteria:**
- Deleting a review that doesn't belong to the caller returns 403/404, not 200.
- Signup with a duplicate email returns 409, not a raw 500 stack trace.
- Error responses have a consistent shape (`{ "message": "..." }` or similar).

**Why it's well-scoped for agents:** small diff, testable with a couple of MockMvc
tests, good candidate for the "governance/evaluation" part of the course (did the agent
actually close the security gap, or just move it?).

---

## 3. Externalize the frontend API base URL

**Area:** Frontend (React/Vite)

**Problem:** `http://localhost:8080` is hardcoded in five files (`UserContext.jsx`,
`About.jsx`, `GlamUp.jsx`, `OutfitTip.jsx`, `StyleLensUpload.jsx`), so the app cannot
work once deployed (the README says it's deployed to Netlify).

**Scope:**
- Add a single `src/config.js` (or `src/api/client.js`) exporting `API_BASE_URL` from
  `import.meta.env.VITE_API_BASE_URL`, defaulting to `http://localhost:8080` for local
  dev.
- Replace all hardcoded fetch URLs with the shared constant.
- Add `.env.example` documenting `VITE_API_BASE_URL`.

**Acceptance criteria:**
- No component contains a hardcoded `http://localhost:8080` string.
- Setting `VITE_API_BASE_URL` in `.env` changes where the app points without a code
  change.

**Why it's well-scoped for agents:** mechanical, repeatable across files — a great test
of whether an agent can make a consistent, DRY change across a whole codebase rather
than fixing one call site and missing the other five.

---

## 4. Add a real backend test suite

**Area:** Backend (Spring Boot)

**Problem:** The only backend test is the default `contextLoads()` stub. There's no
coverage of signup/login validation, review ownership, or the StyleLens flow.

**Scope:**
- Add `@WebMvcTest`/`@DataJpaTest` tests for `UserController` (duplicate email, missing
  fields, wrong password) and `ReviewController` (create/list/delete, including the
  ownership fix from #2).
- Wire `./mvnw test` into the eventual CI workflow (Module 4).

**Acceptance criteria:**
- `./mvnw test` runs and passes with at least 5-8 new test cases covering the above.
- Tests fail (red) against the current/unfixed code and pass (green) once #1/#2 land —
  useful for demonstrating the agentic system's before/after impact.

**Why it's well-scoped for agents:** directly supports Course Requirement 3 (testing
strategy) and gives the CI/CD module something real to run.

---

## 5. Fix the header's static "Welcome, User" + expose the real profile

**Area:** Frontend (React) + Backend (Spring Boot)

**Problem:** `Header.jsx` always renders "Welcome, User" regardless of who is logged
in — the user's name is never fetched after login, only the numeric ID.

**Scope:**
- Add a `GET /api/users/{id}` endpoint (currently missing) that returns the user's
  name/email (no password).
- Update `UserContext` to fetch and cache the current user's profile after login.
- Render the actual name in the header.

**Acceptance criteria:**
- After logging in, the header shows the real user's name.
- The new endpoint never returns the password field.

**Why it's well-scoped for agents:** small full-stack slice (one new endpoint + one
frontend consumer), good for testing whether the agentic system coordinates a backend
change with its frontend consumer correctly.

---

## Notes for course use

- Pick **3 of the 5** above to commit to for the course (Requirement 2 only asks for
  three). #1 and #2 are the highest-value/most security-relevant; #3 is the cheapest
  "quick win" to calibrate the workflow on before tackling #1/#2.
- None of these are implemented yet — they are the backlog the multi-agent system will
  attempt, and the iteration log should record what was tried, what passed evaluation,
  and what had to be redone.
