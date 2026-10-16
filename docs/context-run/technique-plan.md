# Context-Management Technique Plan

Companion to `docs/context-run/pre-session-plan.md`. Session: `backend-quality-reviewer`, one interactive Claude Code session, three phases.

## 1. Explicit context boundaries

### Boundary A: between Phase 1 and Phase 2 (the requirement change)
Message B-A states:
- **Previous phase complete:** Phase 1 inventory is done; table v1 is final.
- **Next focus:** re-rank table v1 under new rules and add `User.java` findings.
- **Still active:** R1 (controllers, now plus R6), R2 (correctness after security), R5 (cite `file:line` with quotes).
- **Changed:** R3 replaced by R3' (security in scope, ranks first); R4 replaced by R4' (max 6).
- **Carries forward:** table v1 and its IDs (F1...), which must stay stable.

**Why here:** this is where the rules change, and the main drift risk is the agent keeping "security deferred" or the 8-item limit from earlier in the conversation. A boundary that names the replaced rules lets me see whether old rules leak through.

### Boundary B: between Phase 2 and Phase 3
Message B-B states:
- **Previous phase complete:** table v2 is final.
- **Next focus:** test recommendations for the top 3 of table v2.
- **Still active:** R1+R6, R2, R3', R4', R5.
- **Changed:** none in this phase; R3 and R4 remain retired.
- **Carries forward:** table v2 (the version from the summary), not table v1.

**Why here:** Phase 3 changes the type of work from finding issues to designing tests. Two versions of the ranked table exist in context, so I name which one is authoritative.

## 2. Proactive summarization

**When:** at the end of Phase 2, before Boundary B.
**How:** ask the agent to write a summary to `docs/context-run/summary-after-phase2.md` with these sections: current goal; active requirements (verbatim R-numbers); retired requirements; decisions made so far; current state of the artifact (full table v2); unresolved questions; next planned action.
I will not invoke `/summarize-session` by name, because the container has a built-in skill with that name that loaded instead of the project skill in an earlier run (see the Summarization Run in `docs/iteration-log.md`). I will instead name the sections explicitly in the request.

**Why here:** it is the point of maximum risk: the session holds two conflicting rule sets and two versions of the table. Writing the current state to a file anchors Phase 3 to table v2, and I can check the file against the conversation before continuing.

## 3. Compaction

**Not planned.** Earlier runs used about 5% of a 967k-token window, and this task reads about 750 lines of Java. I will check `/context` at each boundary and run `/compact` only if usage passes 60%, per the Compaction Policy in `CLAUDE.md`. If it is not used, the iteration log will say so and give the readings.

## Recording during the run
- Note `/context` % at Boundary A, Boundary B, and the end.
- Copy the agent's tables from each phase into `docs/context-run/session-notes.md`, or have the agent save them.
- Do not correct or restart the agent if it makes a mistake; record it and continue.
