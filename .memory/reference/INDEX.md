# Reference Index

Last reviewed: 2026-10-17
Maintained by: swarna (human-maintained, read-only for the agent)

These documents are too large or too rarely needed to load every session.
They stay where they already live in the repo; this index only points to them.
Read an entry only when the current task matches its "Consult when" column,
and read the relevant section, not the whole file.

| Document | What it contains | Consult when |
|---|---|---|
| `FEATURES.md` | Backlog of FashionMate improvements with scope and acceptance criteria (item 1 JWT auth, item 2 review ownership + error handling, item 3 frontend API base URL, ...) | Starting, scoping, or checking acceptance criteria for a backlog item |
| `docs/context-run/final-report.md` | Prioritized review findings for the backend controllers (F1-F9, S1-S3) with file:line evidence and test recommendations. Note: F2's "deleteById crashes with 500" claim is wrong for Spring Data JPA 3.x | Choosing the next security or reliability fix, or writing tests for a known finding |
| `docs/prd.md`, `docs/prd-backend-tests.md` | PRDs for the frontend build-verification agent and the backend test-summary agent | Changing or re-running those agents |
| `docs/rubric.md`, `docs/rubric-backend-tests.md`, `docs/rubric-email-summarize.md` | Scoring rubrics for each agent | Scoring an agent run |
| `docs/iteration-log.md` | History of agent runs, scores, and lessons learned | Evaluating an agent or comparing with earlier runs |
| `docs/memory-architecture.md` | Design of this memory system: layers, ownership, exclusions, pruning | Adding, moving, or pruning memory entries |

Not indexed on purpose: `docs/transcripts/`, `docs/evidence/` (raw run logs, too noisy for context and may contain personal email content).
