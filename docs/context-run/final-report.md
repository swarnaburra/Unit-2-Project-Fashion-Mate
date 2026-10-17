# Final Report — FashionMate Backend Controller Review

## Table v2 (Final Ranking)

| Rank | ID | Category | File:line | Issue (one sentence) | Evidence (quoted) |
|---|---|---|---|---|---|
| 1 | F9 | Security | `UserController.java:31-33` + `User.java:43-45` | `POST /api/users` returns the full `User` entity in its JSON response, which includes the plaintext password because `User.getPassword()` has no serialization guard. | `public User createUser(@RequestBody User user) {` / `return userRepository.save(user);` (UserController.java:31-32) — exposed via `public String getPassword(){` / `return password;` (User.java:43-44) |
| 2 | S1 | Security | `ReviewController.java:70-74` | `deleteReview` checks only that `userId` exists, never that `reviewId` actually belongs to that user, so any caller can delete any review by guessing its ID. | `public void deleteReview(@PathVariable Long userId, @PathVariable Long reviewId) {` / `checkUserExist(userId);` / `reviewRepository.deleteById(reviewId);` |
| 3 | S2 | Security | `UserController.java:79` | Login compares the submitted password against the stored value in plaintext, with no hashing. | `final User user = userRepository.findByEmailAndPassword(request.getEmail(), request.getPassword());` |
| 4 | S3 | Security | `GlamUpController.java:17`, `OutfitTipController.java:16`, `ReviewController.java:18`, `StyleLensController.java:23`, `UserController.java:13` | All five controllers allow cross-origin requests from any site with no allowed-origins restriction. | `@CrossOrigin()` (GlamUp/OutfitTip), `@CrossOrigin` (Review/StyleLens/User) |
| 5 | F1 | Correctness/Reliability | `GlamUpController.java:34-35` | `GET /api/glamup` crashes (→500) whenever the `glam_up` table is empty, instead of handling the empty result. | `final var glamUps = glamUpRepository.findAll();` / `return glamUps.getFirst();` |
| 6 | F2 | Correctness/Reliability | `ReviewController.java:73` | `DELETE /api/reviews/{userId}/review/{reviewId}` crashes (→500) if `reviewId` doesn't exist, instead of returning 404. | `reviewRepository.deleteById(reviewId);` |

Dropped by the R4' cap (still valid, not retracted): F3, F4, F5, F6, F7, F8 (see table v1 / `summary-after-phase2.md` for details).

## Phase 3 Test Recommendations (Top 3 Findings)

| Finding | Test class | New test method | What it asserts |
|---|---|---|---|
| **F9** — `UserController.java:31-33` + `User.java:43-45` | `fashionmate_backend/controllers/UserControllerTest.java` | `createUserResponseJsonDoesNotContainPassword` | Following the pattern of `createUserSavesAndReturnsUser` (lines 25-33), call `userController.createUser(user)`, serialize the returned `User` with a Jackson `ObjectMapper`, and assert the resulting JSON string does not contain `"password"` or the raw password value. Requires adding an `ObjectMapper` to the test (not currently imported). |
| **S1** — `ReviewController.java:70-74` | `fashionmate_backend/controllers/ReviewControllerTest.java` | `deleteReviewThrowsWhenReviewBelongsToAnotherUser` | Following the pattern of `deleteReviewThrowsWhenUserMissingAndDoesNotDelete` (lines 92-100), mock a valid user but a `reviewId` owned by someone else, then assert `deleteReview(userId, reviewId)` throws and `verify(reviewRepository, never()).deleteById(any())`. Would fail (red) against current code, since `deleteReviewDeletesWhenUserExists` (lines 84-90) shows `deleteById` is called unconditionally once the user exists, with no ownership check. |
| **S2** — `UserController.java:79` | `fashionmate_backend/controllers/UserControllerTest.java` | `signInForwardsRawPasswordToRepositoryUnhashed` | Following the pattern of `signInReturnsIdWhenCredentialsMatch` (lines 100-114), supply a password like `"secret"` and `verify(userRepository).findByEmailAndPassword(eq("jamie@example.com"), eq("secret"))` to confirm the exact, unmodified string reaches the repository — characterizing that no hashing occurs before the credential check. |

## Coverage Notes (Ranks 4–6)

- **S3** (rank 4, `GlamUpController.java:17` / `OutfitTipController.java:16` / `ReviewController.java:18` / `StyleLensController.java:23` / `UserController.java:13`): Not covered — none of the five controller test classes use MockMvc or a Spring web context that would exercise CORS behavior; e.g. `OutfitTipControllerTest.java:21` (`private final OutfitTipController controller = new OutfitTipController();`) and `UserControllerTest.java:15` (`@ExtendWith(MockitoExtension.class)`) both call controller methods directly in-process, so no test in the suite can observe `@CrossOrigin` headers at all.
- **F1** (rank 5, `GlamUpController.java:34-35`): Covered — `GlamUpControllerTest.getGlamUpByThrowsWhenNoGlamUpsExist` (`GlamUpControllerTest.java:40-46`) already asserts `glamUpController.getGlamUpBy()` throws `NoSuchElementException` when the repository returns an empty list, pinning the crash as current behavior rather than testing a fix for it.
- **F2** (rank 6, `ReviewController.java:73`): Not covered — `ReviewControllerTest.java` only has `deleteReviewDeletesWhenUserExists` (lines 84-90) and `deleteReviewThrowsWhenUserMissingAndDoesNotDelete` (lines 92-100); neither simulates a nonexistent `reviewId` causing `reviewRepository.deleteById` to throw.
