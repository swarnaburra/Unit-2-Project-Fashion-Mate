# Session Messages (send in order, one at a time)

Launch: `claude --agent backend-quality-reviewer`

## M1: Phase 1 kickoff
Phase 1: review the five controller classes in fashionmate-backend/src/main/java/fashionmate_backend/controllers/ and list code-quality issues. Rules for this phase: R1. Scope is the five controller classes only; models and repositories are out of scope. R2. Rank by user-visible correctness and reliability bugs first (crashes, wrong HTTP status, unhandled empty results), then maintainability. R3. Security issues are out of scope for ranking because FEATURES.md item 1 already tracks them; list them at the bottom under "Deferred: security" without ranking. R4. At most 8 ranked findings. R5. Every finding cites file:line and quotes the evidence. Output the ranked findings table (call it table v1).

## M2: Phase 1 follow-up
For F1 and F2 in table v1, explain in two sentences each what a user of the app would actually see when the bug happens.

## M3: Boundary A + requirement change
Phase 1 is complete; table v1 is final. Phase 2 starts now. Requirement change, effective immediately: the team has postponed the JWT work in FEATURES.md item 1, so security can no longer be deferred. R3 is replaced by R3': security issues are in scope and rank above correctness issues. R4 is replaced by R4': at most 6 ranked findings. New rule R6: scope expands to include fashionmate-backend/src/main/java/fashionmate_backend/models/User.java. Still in effect: R1 (now plus R6), R2 (correctness ranks after security), R5. Carry forward table v1 and keep its IDs stable. Re-rank table v1 under the new rules, add any findings from User.java with new IDs, cut to 6, and output table v2 with a moved/added/dropped note for every ID.

## M4: Phase 2 follow-up
Which findings from the old "Deferred: security" list are now in table v2, and which did not make the top 6? One line each.

## M5: Proactive summary
Before we continue, write a summary to docs/context-run/summary-after-phase2.md with exactly these sections: Current goal; Active requirements (verbatim, with R-numbers); Retired requirements; Decisions made so far; Current state of the artifact (the full table v2); Unresolved questions; Next planned action. Then show me the file contents.

## M6: Boundary B
Phase 2 is complete; table v2 in docs/context-run/summary-after-phase2.md is the authoritative ranking. Phase 3 starts now: test recommendations. Still in effect: R1 plus R6, R2, R3', R4', R5. Retired and not to be used: R3 (security deferred) and R4 (8 findings). No rules change in this phase. For the top 3 findings in table v2, recommend one test each: the existing test class in fashionmate-backend/src/test/java/fashionmate_backend/ it belongs in, a test method name, and what it asserts. Read the test class before recommending.

## M7: Revisit
Now revisit F-IDs ranked 4 to 6 in table v2: for each, say in one sentence whether an existing test already covers it, citing the test class and method if so.

## M8: Final report
Write the final consolidated report to docs/context-run/final-report.md: table v2 (final ranking), the Phase 3 test recommendations, and the coverage notes for ranks 4 to 6. Then list any place in this session where you applied a rule that was no longer in effect.
