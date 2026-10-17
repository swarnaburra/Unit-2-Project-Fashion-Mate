# Summary After Phase 2

## Current goal
Checkpoint the backend code-quality review session after completing Phase 2 (re-ranking the controller findings under revised rules, with scope expanded to `User.java`), before continuing to the next phase.

## Active requirements (verbatim, with R-numbers)
- R1: "Scope is the five controller classes only; models and repositories are out of scope." (amended by R6 below)
- R2: "Rank by user-visible correctness and reliability bugs first (crashes, wrong HTTP status, unhandled empty results), then maintainability." (now applied after security, per R3')
- R3' (replaces R3): "security issues are in scope and rank above correctness issues."
- R4' (replaces R4): "at most 6 ranked findings."
- R5: "Every finding cites file:line and quotes the evidence."
- R6 (new): "scope expands to include fashionmate-backend/src/main/java/fashionmate_backend/models/User.java."

## Retired requirements
- Original R3: "Security issues are out of scope for ranking because FEATURES.md item 1 already tracks them; list them at the bottom under 'Deferred: security' without ranking." — replaced by R3'.
- Original R4: "At most 8 ranked findings." — replaced by R4'.

## Decisions made so far
- Phase 1: Reviewed the five controller classes and produced table v1 — 8 ranked correctness/reliability findings (F1–F8) plus a separate, unranked "Deferred: security" list (S1–S3), per the original R1–R5.
- On request, explained the user-visible symptoms of F1 (GlamUpController crash on empty table) and F2 (ReviewController crash on deleting a nonexistent review) in two sentences each.
- Phase 2: Applied the Context Boundary Procedure, read `User.java` under the new R6 scope, and identified a new finding (F9: plaintext password exposed in the `POST /api/users` response body, via `User.getPassword()` + `UserController.createUser`).
- Re-ranked all candidates (new security-first ordering per R3'): promoted S1, S2, S3 from the deferred list into the ranked table; placed F9 at rank 1; retained F1 and F2 as the only correctness findings that fit under the new R4' cap of 6; dropped F3–F8 from the ranked list (not retracted, just cut by the cap) — producing table v2.
- Confirmed for the user that all three former "Deferred: security" findings (S1, S2, S3) made it into table v2, and that the items that did not make the top 6 were all correctness findings (F3–F8), not security findings.

## Current state of the artifact (table v2)

| Rank | ID | Category | File:line | Issue (one sentence) | Evidence (quoted) |
|---|---|---|---|---|---|
| 1 | F9 | Security | `UserController.java:31-33` + `User.java:43-45` | `POST /api/users` returns the full `User` entity in its JSON response, which includes the plaintext password because `User.getPassword()` has no serialization guard. | `public User createUser(@RequestBody User user) {` / `return userRepository.save(user);` (UserController.java:31-32) — exposed via `public String getPassword(){` / `return password;` (User.java:43-44) |
| 2 | S1 | Security | `ReviewController.java:70-74` | `deleteReview` checks only that `userId` exists, never that `reviewId` belongs to that user, so any caller can delete any review by guessing its ID. | `public void deleteReview(@PathVariable Long userId, @PathVariable Long reviewId) {` / `checkUserExist(userId);` / `reviewRepository.deleteById(reviewId);` |
| 3 | S2 | Security | `UserController.java:79` | Login compares the submitted password against the stored value in plaintext, with no hashing. | `final User user = userRepository.findByEmailAndPassword(request.getEmail(), request.getPassword());` |
| 4 | S3 | Security | `GlamUpController.java:17`, `OutfitTipController.java:16`, `ReviewController.java:18`, `StyleLensController.java:23`, `UserController.java:13` | All five controllers allow cross-origin requests from any site with no allowed-origins restriction. | `@CrossOrigin()` (GlamUp/OutfitTip), `@CrossOrigin` (Review/StyleLens/User) |
| 5 | F1 | Correctness/Reliability | `GlamUpController.java:34-35` | `GET /api/glamup` crashes (→500) whenever the `glam_up` table is empty, instead of handling the empty result. | `final var glamUps = glamUpRepository.findAll();` / `return glamUps.getFirst();` |
| 6 | F2 | Correctness/Reliability | `ReviewController.java:73` | `DELETE /api/reviews/{userId}/review/{reviewId}` crashes (→500) if `reviewId` doesn't exist, instead of returning 404. | `reviewRepository.deleteById(reviewId);` |

Dropped from the ranked list by the R4' cap (still valid, not retracted): F3, F4, F5, F6, F7, F8.

## Unresolved questions
- Should F3–F8 (dropped correctness findings) be tracked anywhere going forward, or treated as closed for this session?
- Does the R4' cap of 6 persist for future phases, or was it specific to Phase 2?
- Should S1/S2/S3 eventually be renumbered into the main F-sequence, or keep their own ID namespace permanently?
- Is a "next 6" follow-up list (covering F3–F8 and the previously-noted maintainability items, e.g. `StyleLensController.java:95` client-per-request, `UserController.createUser` bypassing `signUp` validation) wanted as a separate artifact?

## Next planned action
Await the user's direction for Phase 3 — no next phase, scope change, or rule change has been specified yet.
