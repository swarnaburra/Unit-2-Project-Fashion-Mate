# Lessons Learned Workflow — Final Report (FashionMate, `proj-lessons`)

Date: 2026-10-27. Repo: `swarnaburra/Unit-2-Project-Fashion-Mate`.
Commits: `b17358a` (corpus + ground truth, before any evaluation) → `089376d` (servers, grants, blocks) → `85e3c9f` (validation Run 1) → `3b4c916` (fix 1 + Run 2) → `a3aaf64` (orchestrated run).

## 1. Corpus (curated, classified)

`.memory/reference/lessons/` — 9 real lessons from earlier FashionMate work, each with `classification`, `project: proj-lessons`, `doc_type: lesson` front matter. Mounted `:ro` in the container; indexed only by the retrieval server.

| Document | Classification |
|---|---|
| lesson-backend-test-runner, lesson-crlf-diff-noise, lesson-password-serialization, lesson-deleteById-silent, lesson-error-status-mapping, lesson-ownership-not-found, lesson-frontend-contract, lesson-verified-before-resolved | internal |
| security-open-exposures | **confidential** |

## 2. Ground truth and validation

`docs/lessons-retrieval-ground-truth.md` (L1–L8: 3 direct, 2 near-miss with decoys, 1 literal keyword, 2 classification boundary) was committed in `b17358a` before the first run and never edited. Bar: 80% (7/8), threshold 0.65, top_k 3.

| Run | Config | Result |
|---|---|---|
| 1 | paragraph, min_words 50 | 6/8 (75%) — L2, L6 keyword-only |
| 2 | paragraph, `--min-words 20` (one change) | **7/8 (88%)** — L2 fixed (0.632 → 0.702), no regressions |

Diagnosis used a read-only probe (`mcp/retrieval/probe_lesson_scores.py`) that measured scores under three chunkings before any change. L6 stays a documented known gap (≈0.33 under every chunking: a wording gap, not a chunk-size gap). Full tables and fix log: `docs/lessons-retrieval-validation.md`.

## 3. Role access (narrow, enforced by the system)

| Role | Storage | Retrieval | Enforced by |
|---|---|---|---|
| planner | none | `retrieve` (before planning) | agent `tools` / `disallowedTools` |
| implementer | `write_entry`, `list_entries` only | none | agent allowlist; storage rejects confidential/secret and any project ≠ `proj-lessons` |
| reviewer | none | `retrieve` (while reviewing) | agent allowlist |
| tester, memory-keeper | none (explicitly denied) | none | `disallowedTools` |
| all roles | — | ceiling capped at `internal` | `RETRIEVAL_MAX_CEILING=internal` in the server: `min(requested, cap)` |

No direct path to the data: memory-guard PreToolUse hook + `permissions.deny` in `.claude/settings.json` + coursetools deny list block the corpus and `.memory/runtime/` for every Claude tool. Details: `docs/routing-and-tool-grant-map.md` (v1.2.0).

## 4. Orchestrated run (`run-glamup-404`)

Finding: `GlamUpController.getGlamUpBy` threw `NoSuchElementException` (500) on an empty table. Full record: `docs/run-summary.md`.

| Phase | Result |
|---|---|
| planner | 1 `retrieve` call (role planner, ceiling internal); cited `lesson-error-status-mapping.md` #0/#1 and `lesson-ownership-not-found.md` #1/#2 (all keyword, internal). Plan: new `GlamUpNotFoundException` → 404. Checkpoint 1 approved. |
| implementer | 5 planned files changed; `list_entries` then **one** `write_entry` (entry `4e2c9f12-c0f0-403e-827e-8a3380f6c97e`, internal, proj-lessons); "not yet verified". Interrupted once by a terminal crash and resumed with a scoped brief. |
| reviewer | `retrieve` while reviewing; **PASS**, 1 low finding (test message literal differs from production message). |
| tester | NOT RUN (no JDK, decision-003) → human ran Maven: **Tests run: 35, Failures: 0, Errors: 0, BUILD SUCCESS** (2026-10-27T22:01:02Z) → **PASS**. |
| memory-keeper | After Checkpoint 2: `decision-004.md` (+ `GlamUpNotFoundException → 404`) and `MEMORY_INDEX.md`. |

## 5. Verification

| Check | Evidence | Result |
|---|---|---|
| Citations present | planner's `retrieve` hits recorded with `source_document`, `chunk_index`, method, score, classification (`run-summary.md`) | PASS |
| Lesson written through storage | audit log: `{"calling_role":"implementer","classification":"internal","entry_id":"4e2c9f12-…","operation":"write_entry","project_id":"proj-lessons","timestamp":"2026-10-27T21:45:07Z"}` | PASS |
| Persists across restart / new conversation | storage restarted; then a brand-new container ran `mcp/verify_lessons.py` (MCP client, not `--continue`): `list_entries` → 1 entry, `entry_id=4e2c9f12-…`, internal, title "Empty-collection lookups need a custom not-found exception, not a raw 500" | PASS |
| Audit log records it | exactly one line, the write above; no other writes | PASS |
| Internal-ceiling role cannot get the confidential doc | L7 and L8 in both validation runs; re-checked after the run with requested ceiling `confidential`: only internal docs returned, `security-open-exposures.md returned: no` | PASS |
| No direct bypass | new Claude session asked to Read `security-open-exposures.md`: blocked by the `permissions.deny` rule before the file opened; Claude tried no other route. During the run, the Orchestrator's attempt to write a `.memory/runtime` reference into `run-summary.md` was blocked by memory-guard and it did not work around it. | PASS |

## 6. Findings and limits

- Real agent queries are jargon-heavy ("NoSuchElementException 404 vs 500 custom exception mapping"), so every hit in the run came from the **keyword fallback**, not vectors ≥ 0.65. The fallback found the right lessons, but they are lower-confidence; the ground truth's natural-language questions over-estimate vector performance in practice.
- Storage writes are not retrievable until a human promotes them into the curated corpus (the reviewer confirmed the new lesson was not yet visible). This is by design: storage = candidates, corpus = curated.
- `calling_role` is self-reported; only the implementer holds `write_entry`, which limits who can claim it.
- memory-guard matches protected path strings anywhere in tool input, so it also blocks harmless mentions in docs (over-blocking, safe direction).
- Operational: an interactive terminal froze twice (Windows console QuickEdit "Select" mode); the run was resumed without redoing approved work.

## 7. Answers

**Q1 — Corpus vs storage.** The corpus (`.memory/reference/lessons/`) is curated, human-reviewed, classified source, mounted read-only and served only by retrieval, which is what agents cite. Storage (`proj-lessons` in SQLite) is the agents' write path for *new candidate* lessons: validated, project-scoped, audited, but not retrievable until a human promotes an entry into the corpus. Separating them stops an agent from writing something and then citing its own unreviewed text as authority. The final persistence check ran in a new container without the `claude-auth` volume, through the storage MCP server, so persistence comes from storage, not from Claude authentication.

**Q2 — Most important boundary, with evidence.** The server-side classification cap (`RETRIEVAL_MAX_CEILING=internal`). It is enforced by the server, not by the caller's choice: L8 and the post-run check requested `confidential` and still got only internal documents, while the confidential `security-open-exposures.md` (the best semantic match) never appeared. Backed by the direct-access block: a Read of that file was refused by `permissions.deny` before it opened.

**Q3 — Ground-truth findings.** Run 1 scored 6/8; the probe showed L2 was a chunking problem (0.632 → 0.702 at min_words 20) and L6 was not (≈0.33 everywhere). One change (`--min-words 20`) gave 7/8 with no regressions and matched the probe exactly; the answer key was never edited. Boundary cases L7/L8 held in every run, near-miss decoys never outranked the expected doc, and L5 proved the keyword fallback. In the live run, agent queries hit only the keyword fallback, so the set should gain jargon-style queries in a future version.
