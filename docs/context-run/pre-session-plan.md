# Pre-Session Plan: Managed Context Run on FashionMate Backend

**Date:** 2026-10-16
**Agent:** `backend-quality-reviewer` (`.claude/agents/backend-quality-reviewer.md`, v0.1.0), launched with `claude --agent backend-quality-reviewer` inside the `agentic_engineer_2` container.
**Rubric:** Accuracy, Task Adherence, Coherence, scored 1-4 each (same scale as the project rubrics; 12 max).

## Real project task
Produce a prioritized, evidence-backed list of code-quality and security issues in the FashionMate backend controllers (`fashionmate-backend/src/main/java/fashionmate_backend/controllers/`), then recommend the tests that should be added for the top findings. The output feeds the existing backlog in `FEATURES.md` (items 1 and 2: auth and error handling) and the test suite under `src/test/java/.../controllers/`.

## Phases

### Phase 1: Inventory and correctness ranking
- **Goal:** Review all five controllers and list issues.
- **Rules in effect (Phase 1):**
  - R1. Scope: the five controller classes only. Models and repositories are out of scope.
  - R2. Rank by user-visible correctness and reliability bugs first (crashes, wrong HTTP status, unhandled empty results), then maintainability.
  - R3. Security issues are out of scope for ranking, because they are already tracked as FEATURES.md item 1. List them at the bottom under "Deferred: security" without ranking them.
  - R4. At most 8 ranked findings.
  - R5. Every finding cites `file:line` and quotes the evidence.
- **Output:** Ranked findings table v1.

### Requirement change (start of Phase 2)
The team decides the JWT work in FEATURES.md item 1 is postponed, so security issues can no longer be deferred:
- R3 is **replaced** by R3': Security issues are in scope and rank **above** correctness issues.
- R4 is **replaced** by R4': At most **6** ranked findings.
- R6 (new): Scope expands to include `models/User.java`, because plaintext password handling lives there.
- R1, R2, R5 stay in effect (R2 now applies after security).

### Phase 2: Re-prioritization (revisits the Phase 1 artifact)
- **Goal:** Re-rank the Phase 1 list under the new rules, add any new findings from `User.java`, and drop to 6.
- **Output:** Ranked findings table v2, with a "moved / added / dropped" note per ID.

### Phase 3: Test recommendations
- **Goal:** For the **top 3 findings in table v2**, recommend one test each: the existing test class it belongs in, the test method name, and what it asserts.
- **Rules:** R1 (with R6), R2, R3', R4', R5. Tests must target existing classes in `src/test/java/fashionmate_backend/`.
- **Output:** Test recommendation table, plus a final consolidated report.

## Artifact revisited later
The ranked findings table. It is created in Phase 1, re-ranked in Phase 2, and its top 3 drive Phase 3. Phase 3 is the drift check: it must use table v2's top 3, not table v1's.

## Evidence for evaluation
- **Accuracy:** spot-check 3 cited `file:line` references against the source; confirm no invented test classes.
- **Task adherence:** table v2 has at most 6 rows, security ranks above correctness, and `User.java` was reviewed; Phase 3 uses table v2's top 3.
- **Coherence:** final report's ranking, IDs, and test targets all match table v2; no leftover "Deferred: security" section after Phase 2.
- **Drift signals to record:** reapplying R3 or R4 after the change, ID reuse, stale line numbers, re-reviewing files already covered, or locally reasonable tests aimed at Phase 1 priorities.
- **Saved artifacts:** `docs/context-run/summary-after-phase2.md` (proactive summary) and `docs/context-run/final-report.md`.
