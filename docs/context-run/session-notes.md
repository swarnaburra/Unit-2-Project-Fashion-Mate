# Session Notes: backend-quality-reviewer managed run (2026-10-16)

Observations recorded during the run, message by message. Used as evidence for the iteration log.

## M1: Phase 1 kickoff
- Agent ran the Context Boundary Procedure first: restated the goal, listed R1-R5 verbatim, said no prior rules, named "table v1".
- Read the five controllers. Table v1: 8 ranked findings (F1-F8), all Correctness/Reliability:
  - F1 GlamUpController.java:34-35, `getFirst()` on empty table -> 500
  - F2 ReviewController.java:73, `deleteById` on missing review -> 500
  - F3 StyleLensController.java:56, `Long.valueOf(userId)` NumberFormatException -> 500
  - F4 StyleLensController.java:72,116, null Gemini text -> NPE
  - F5 ReviewController.java:78-79, missing user -> RuntimeException -> 500 (3 endpoints)
  - F6 StyleLensController.java:58-59, missing user -> Exception -> 500
  - F7 UserController.java:53-54, duplicate email -> 500 instead of 409
  - F8 UserController.java:79-83, bad login -> 500 instead of 401
- Deferred: security (unranked, per R3): S1 review ownership on delete (ReviewController.java:70-74), S2 plaintext password compare (UserController.java:79), S3 unrestricted @CrossOrigin in all five controllers.
- R4 respected (8). Maintainability items (new Gemini Client per request, `createUser` bypasses validation) noted as over the cap, not ranked.
- **Accuracy spot-check:** line references for F1, F2, F3, F4, F7, and S2 verified against the source; all match.
- **Good catch:** agent flagged that S1 is tracked by FEATURES.md item 2, not item 1, so R3's stated reason doesn't quite fit it. It still followed R3.
- Note: S-IDs use a separate prefix from F-IDs. Watch whether Phase 2 keeps them stable.

## M2: Phase 1 follow-up (read-only question)
- Agent correctly skipped the boundary procedure, citing CLAUDE.md item 9 (read-only questions).
- Explained F1 (GlamUp panel fails to load, HTTP 500) and F2 (delete shows an error instead of removing the review).
- **Accuracy issue (F2):** the project uses Spring Boot 3.4.3 (`pom.xml`), so Spring Data JPA 3.x. Since Spring Data 3.0, `CrudRepository.deleteById` silently ignores a missing ID instead of throwing `EmptyResultDataAccessException`. F2's claimed 500 is based on outdated framework behavior; the real effect is a silent 200 for a nonexistent review. The finding is stale knowledge, not context drift, but it lowers Accuracy. Not corrected in-session (per the exercise rules); watch whether F2 keeps rank 2 later.

## /context at Boundary A (before M3)
- 26.9k/967k tokens (3%). Well below the 60% compaction threshold; no compaction.

## M3: Boundary A + requirement change -> table v2
- Agent ran the boundary procedure: listed R1+R6, R2, R3', R4', R5 and explicitly retired old R3 ("Deferred: security bucket no longer exists"), old R4 (8), and R1's "models out of scope" clause.
- Read only User.java (no unnecessary re-reading of the controllers it had already reviewed).
- Table v2 (6 rows): 1 F9 (new, Security: `createUser` returns the full User entity including the plaintext password; UserController.java:31-33 + User.java:43-45), 2 S1, 3 S2, 4 S3, 5 F1, 6 F2.
- Moved/added/dropped note given for all 12 IDs. F3-F8 dropped by R4' and explicitly marked "still valid, not retracted". IDs stayed stable; the new finding got a new ID (F9), no reuse.
- **R3' applied correctly:** all four security items rank above correctness. **R4' applied:** exactly 6 rows. **No "Deferred: security" section remains.**
- **Accuracy spot-check:** F9 evidence verified (User.java:43-45 `getPassword()` with no `@JsonIgnore`; UserController.java:31-33 returns the saved entity). Correct and the most important new finding.
- **Carried-forward error:** F2 kept rank 6 with the same incorrect "crashes (->500)" claim from Phase 1 (Spring Data 3.x `deleteById` ignores missing IDs). It now takes a top-6 slot that F3 (a real NumberFormatException -> 500) would otherwise hold. Stale framework knowledge carried through the boundary unchanged.
- **Minor inconsistency:** S1's note says "previously deferred per old R3 (FEATURES item 1)", although in Phase 1 the agent itself pointed out S1 is tracked by item 2. Small loss of an earlier observation.
- F9 uses the F- prefix although it is a security finding (S- prefix was used for the others). Not a rule violation, but the ID scheme is now mixed.

## M4: Phase 2 follow-up (read-only question)
- Correctly skipped the boundary procedure (read-only).
- Correct answer: S1, S2, S3 all in table v2 (ranks 2-4); none cut.
- **Minor slip:** "the two findings dropped by the R4' cap were correctness findings (F3-F8)". Six were dropped, not two. Small numeric inconsistency inside one sentence.

## M5: Proactive summary -> docs/context-run/summary-after-phase2.md
- File written (45 lines) with all seven requested sections.
- **Preserved well:** all six active rules quoted verbatim with R-numbers (R6 included); both retired rules quoted verbatim with what replaced them; the full table v2 with file:line and quoted evidence; the dropped list F3-F8 marked "still valid, not retracted"; the maintainability items (StyleLensController.java:95 client-per-request, createUser bypassing signUp validation) carried into Unresolved questions.
- **Omitted or weak:**
  - "Current goal" describes the checkpoint itself, not the overall task (a prioritized, evidence-backed review that will feed test recommendations).
  - "Next planned action" is only "await the user's direction"; the agent was never told Phase 3 would be test recommendations, so the summary cannot carry it. This is a plan gap on my side, not an agent error.
  - The Phase 1 observation that S1 is tracked by FEATURES.md item 2 (not item 1) is gone.
  - F2's inaccurate "crash (->500)" claim is preserved as fact; a summary can only carry forward what the session believes.
- Unresolved questions are relevant (does the 6 cap persist? should S-IDs merge into the F-sequence?).

## /context at Boundary B (before M6)
- 43.6k/967k tokens (5%). Still far below 60%; no compaction.

## M6: Boundary B -> Phase 3 test recommendations
- Boundary procedure run: listed R1+R6, R2, R3', R4', R5; retired R3 and R4 by name; named the artifact.
- **Drift check passed:** picked F9, S1, S2 (table v2's top 3), not F1/F2 (table v1's top 2).
- Read UserControllerTest.java and ReviewControllerTest.java before recommending (as instructed); noted they are plain Mockito unit tests with no MockMvc/JSON layer.
- Recommendations:
  - F9 -> UserControllerTest.createUserResponseJsonDoesNotContainPassword: serialize the returned User with an ObjectMapper and assert no "password". Correctly notes the existing tests can't catch the leak because they never look at JSON.
  - S1 -> ReviewControllerTest.deleteReviewThrowsWhenReviewBelongsToAnotherUser: assert a throw and `verify(reviewRepository, never()).deleteById(any())`; correctly predicts it fails today.
  - S2 -> UserControllerTest.signInForwardsRawPasswordToRepositoryUnhashed: a characterization test pinning current plaintext behavior so a future hashing fix must change it deliberately.
- **Accuracy spot-check:** cited test methods and lines verified: createUserSavesAndReturnsUser (line 25), deleteReviewThrowsWhenUserMissingAndDoesNotDelete (line 93; agent said 92-100, off by one for the annotation line), signInReturnsIdWhenCredentialsMatch (line 101; agent said 100-114, same), `verify(reviewRepository).deleteById(10L)` at line 89 (exact). No invented test classes.
- **Respected tool scope:** didn't write to src/test, offered to log to docs/context-run/ instead.
- **Design note:** the S1 test needs a "review lookup" that ReviewController doesn't do today; the agent says "mock a review lookup" without naming the repository method. Slightly vague, not wrong.
- **S2 test design question:** it asserts the current insecure behavior rather than the desired one. Defensible as a characterization test, and the agent said so, but it doesn't fail today the way the F9 and S1 tests do.

## Operator error after M6 (not part of the plan)
- I accidentally pasted M6 a second time, then three fragments of the agent's own previous output ("Context Boundary Procedure — Phase 3", its item 1, and "2. Rules in effect (verbatim):") as separate messages.
- **Agent handled it well:** it recognized the duplicate M6, said nothing had changed, and re-presented the same deliverable without re-reading files or redoing the analysis (no unnecessary repeated work). For the fragments, it said my message looked cut off, restated the Phase 3 boundary with the same rules and the same F9/S1/S2 table, and asked what I wanted.
- No drift introduced: the repeated output kept identical rules, retired-rule list, and recommendations.
- Effect on the run: about 3 extra turns of context; useful as an unplanned robustness check.

## M7: Revisit ranks 4-6 (coverage check)
- The reply text wasn't captured in my notes; its content is in the "Coverage Notes (Ranks 4-6)" section of final-report.md.
- **Drift check passed:** despite the wording "F-IDs ranked 4 to 6", the agent used table v2's actual ranks 4-6 (S3, F1, F2), not F-numbered items and not table v1's ranks 4-6 (F4, F5, F6).
- Read OutfitTipControllerTest and StyleLensControllerTest for the first time (new files, not repeated work).
- S3: not covered (no MockMvc / web context in any controller test). Correct.
- F1: covered by `GlamUpControllerTest.getGlamUpByThrowsWhenNoGlamUpsExist` (lines 40-46), which pins the crash as current behavior. **Verified** (method at line 41, asserts `NoSuchElementException`). Useful nuance: the existing test locks in the bug rather than the fix.
- F2: "not covered; neither test simulates deleteById throwing". The stale Spring Data assumption persists to the end of the session.

## M8: Final report -> docs/context-run/final-report.md
- Report contains table v2 (6 rows, security first), the three Phase 3 test recommendations, and coverage notes for ranks 4-6. All IDs, ranks, and file:line references match table v2 and the summary file. No "Deferred: security" section, no 8-item list.
- Self-audit: "none" for retired-rule use, with specific evidence (no deferred bucket, 6 rows not 8, R3'/R4' restated in Phase 3). This matches my observations.
- Agent flagged its own judgment call: it skipped the boundary procedure for the report-writing step under CLAUDE.md item 9, and asked whether compiling a file counts as read-only. Reasonable to flag; arguably the procedure should have run because M8 writes a new artifact.
- The self-audit only checks rule application, so it cannot catch F2's factual error, which came from outdated framework knowledge rather than from a retired rule.

## Final context
- Not re-measured after M8; last reading 43.6k/967k (5%) at Boundary B, plus roughly 3 duplicate turns and M7/M8. Compaction was never needed.
