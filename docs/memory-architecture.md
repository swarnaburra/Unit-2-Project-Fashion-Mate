# Memory Architecture

Version: 4 (2026-10-18). v2 revised the plan to match the built system; v3 added decisions 003-005 from finishing FEATURES.md item 2; v4 reconciles the document with the safeguards added during failure-mode testing (stale memory, scope leak, sensitive data) and adds the Enforcement section.

## What this workflow needs to remember

This agent helps maintain the FashionMate Spring Boot backend (and, less often, the React frontend) by implementing `FEATURES.md` backlog items in small, staged steps and reviewing the code for quality and security issues. Across sessions, it needs to remember the decisions the code does not explain (which approach was chosen, which was rejected and why), what is deliberately deferred, the current step and next action of an in-progress feature, the project-wide conventions for errors, security, and tests, and known environment limitations (the container has no JDK). It does not need to store which files changed, what tests assert, or acceptance criteria, because Git, the test classes, and `FEATURES.md` already preserve those. It must not store credentials or tokens (database password, Gemini API key, Slack token, Google credentials), user data from the database or email, raw transcripts, or notes about a feature step after that step is merged.

### Evidence from the continuity experiment (2026-10-17)

Feature: `FEATURES.md` item 2 (review ownership + global error handling), stopped after steps 1-4 with tests not yet updated.

- **The repository preserved:** the four changed files, the new exception and handler classes, the 404 rationale (in the `ReviewNotFoundException` Javadoc), and the now-failing `deleteReviewDeletesWhenUserExists` test. A fresh session without notes found all of this by reading Git and the tests.
- **The repository did not preserve:** that UserController work was deferred, that `checkUserExist` was left alone on purpose, that tests should stay plain Mockito, that 404-vs-403 was settled, and which alternatives were rejected. Without notes, the fresh session planned the deferred work first, proposed MockMvc tests, and re-asked 404 vs 403. With notes, it went straight to the right next step.
- **Changes over time:** the current step and next action, what is deferred, environment limitations.
- **Stable unless a human changes it:** the error-handling convention, the 404-not-403 rule, no secrets or passwords in responses, the test style, the test layout.

### Classification of items

| Item | Category | Destination |
|---|---|---|
| Feature 2 approach and rejected options | Project decision | `.memory/project/decisions/decision-001.md` (current state superseded by decision-005) |
| Test layout (separate `src/test/java` tree, no `/tests` move) | Project decision | `.memory/project/decisions/decision-002.md` |
| No JDK/Maven in the container; how tests are run instead | Known limitation + tooling decision | `.memory/project/decisions/decision-003.md` |
| Error/exception convention, 404-not-403, no secrets in responses, repository naming, Mockito test style, scope discipline | Stable standards | `.memory/knowledge/coding-standards.md` |
| FEATURES.md, review report, PRDs, rubrics, iteration log | Large, on-demand documents | Pointed to by `.memory/reference/INDEX.md` |
| How to run backend tests; how to write a handoff or session summary | Procedures | Skills (`.claude/skills/write-handoff`, `summarize-session`; a run-tests skill is a future addition) |
| Changed files, test assertions, 404 rationale in Javadoc | Existing artifacts | Not stored (Git, tests, code) |
| `./mvnw` error text, permission prompts, `/tmp/notes.md` | Temporary details | Context window only |
| DB password, Gemini key, Slack token, Google credentials | Secrets | Environment variables only |

## How the system is loaded

`CLAUDE.md` (auto-loaded by Claude Code) contains the Memory Configuration section and imports three small files with `@` so they are in context at session start without any tool call:
- `@.memory/SCOPE.md` (project identity, checked before any work)
- `@.memory/project/MEMORY_INDEX.md` (the entry point to project memory)
- `@.memory/knowledge/coding-standards.md` (stable rules the agent must always apply)

Individual decision entries and reference documents are **not** imported. The agent reads the decision entries the index marks as relevant to the current task, and reads reference documents only through `.memory/reference/INDEX.md`. This keeps startup context small as memory grows.

Why imports: in the first fresh-session test, CLAUDE.md only *told* the agent to read the index at startup, and it didn't: it said it hadn't opened any memory file. An instruction does not trigger a file read before the first task; an `@` import does.

## Layer 1: Project memory directory

**Location:** `.memory/project/` with `MEMORY_INDEX.md` and one file per decision in `decisions/` (`decision-NNN.md`, template: Date, Review by, Status, Scope, Decision, Rationale, Alternatives rejected, plus optional Current state / How to apply).

**Belongs here (current entries)**
- `decision-001.md`: feature 2 approach (`findByIdAndUserId` + `delete(review)`, 404 not 403, custom exceptions + one `@RestControllerAdvice` with `{"message": ...}`) and rejected alternatives with reasons. Its current-state section now points to decision-005 (the original "next step" and deferred work are done).
- `decision-002.md`: tests live in a separate tree mirroring source packages (existing Maven `src/test/java`); moving to a top-level `/tests` was rejected because it needs a `pom.xml` change or `mvn test` silently runs nothing. Written by the agent during the write-policy test.

- `decision-003.md`: how backend tests are run (Maven Temurin-21 Docker image from PowerShell, `fashionmate-m2` cache, `contextLoads` excluded) and why the dev container can't run them. Project-scoped.
- `decision-004.md`: the status mapping for every user/review error (400/409/401/404), no catch-all handler, messages unchanged. Project-scoped; revisit when JWT auth lands.
- `decision-005.md`: status entry: FEATURES.md item 2 complete, with the gaps it deliberately leaves (F9 password exposure recommended next). Workflow-scoped, 30-day review.
- `decision-006.md`: API connection approach: service account whose key lives only in the `ANTHROPIC_API_KEY` environment variable; records *where* the credential is, never its value. Created as the corrected version of `decision-bad.md` in the sensitive-data exercise.

**Does not belong here**
- The diff or list of changed files (Git records it; a copy goes stale after the next edit).
- Test assertions (the test classes are the source of truth).
- Procedures such as how to run tests (skills).
- A chronological log of every decision (that is what `docs/iteration-log.md` is for; memory holds current state).
- Secrets, tokens, database or email contents.

**Why here:** this information changes as work progresses and is exactly what the notes-free session got wrong.

**Scope:** Project-scoped to this repository. Each entry declares whether it is workflow-scoped (decision-001, tied to FEATURES.md item 2) or project-scoped (decision-002).

**Write permissions:** Agent-writable. The agent checks `MEMORY_INDEX.md` for an existing entry first, updates it rather than duplicating, and adds new entries to the index. The human reviews every agent-written entry in the diff before committing.

**Pruning policy:**
- Workflow-scoped entries are moved to "Archived" in the index when the feature's final commit merges to `main`; any rule that should outlive the feature is promoted to `knowledge/` by the human first.
- Project-scoped entries are reviewed by their "Review by" date (90 days); if the date has passed, the agent flags the entry and asks before acting on it (CLAUDE.md stale-memory policy).
- A superseded entry is updated in place (e.g. the "next step" line is overwritten each step), not appended to.
- The environment limitation in decision-001 is re-checked whenever the Dockerfile or base image changes.

## Layer 2: Knowledge files

**Location:** `.memory/knowledge/coding-standards.md` (one file; split into topic files only if it grows beyond about one page).

**Belongs here (current content, 8 standards)**
1. Custom unchecked exceptions in `fashionmate_backend.exceptions`; no raw `Exception`/`RuntimeException` in new code; no `ResponseStatusException`.
2. One `GlobalExceptionHandler` maps exceptions to status codes; every error body is `{"message": "..."}`.
3. Ownership failures return 404, never 403.
4. Never return the `User` entity (with password); secrets only from environment variables; never commit `.env` or `google_cred.json`.
5. Repository methods follow the existing derived-query naming (`findAllByUserId`, `findByIdAndUserId`).
6. Controller tests use plain Mockito + AssertJ; MockMvc only when a task asks for an end-to-end HTTP check.
7. Every behavior change includes a test that would fail on the old code.
8. Work one backlog item or agreed step at a time; don't start deferred work without asking.

**Does not belong here**
- The current step or deferred work (changes every session; project memory).
- Procedures (skills).
- The Context Boundary Procedure already in `CLAUDE.md` (duplicating it would let two copies drift).

**Why here:** these rules should hold across every feature and must not drift because an agent edited them mid-task.

**Scope:** Project-scoped; applies to all agents and workflows in this repo.

**Write permissions:** Read-only for the agent, enforced by the container: `.memory/knowledge/` (and `.memory/reference/`) are bind-mounted with `:ro`, so writes fail with "Read-only file system" even though Claude Code runs as root. `chmod 444` was rejected because root ignores file permissions and `444` on a directory also blocks reading its files. CLAUDE.md also forbids the agent from changing permissions there and tells it to stop and ask if a write fails. A human edits the file on the host and commits it.

**Pruning policy:** The file header carries "Last reviewed" and "Review by" (90 days). It is also reviewed when a related decision changes (for example, when JWT auth from FEATURES.md item 1 lands, standards 3 and 4 need revisiting). A rule is removed only in the same commit that records what replaces it.

## Layer 3: Indexed reference documents

**Location:** `.memory/reference/INDEX.md`. The documents themselves stay where they already live; the index points to them rather than copying them, so there is one source of truth.

**Belongs here (indexed)**
- `FEATURES.md`: backlog and acceptance criteria.
- `docs/context-run/final-report.md`: prioritized review findings and test recommendations (with a note that finding F2 is wrong).
- `docs/prd.md`, `docs/prd-backend-tests.md`: agent PRDs.
- `docs/rubric*.md`: scoring rubrics.
- `docs/iteration-log.md`: run history and lessons.
- `docs/memory-architecture.md`: this document.

**Does not belong here**
- Raw transcripts and evidence (`docs/transcripts/`, `docs/evidence/`): too noisy, and some contain personal email content.
- Anything needed every session (that goes in knowledge files or CLAUDE.md).

**Why here:** these documents are long and only relevant to specific tasks; loading them every session would waste context.

**Scope:** Project-scoped, shared by all agents in the repo.

**Write permissions:** Read-only for the agent (same `:ro` mount). The agent may suggest a new index entry when it produces a lasting document; the human adds it.

**Pruning policy:** An index row is removed in the same commit that deletes or replaces the document. A superseded document is marked "superseded by ..." for one review cycle, then removed. The index is reviewed every 90 days.

## Allocation decision table

| Information | Where | Reason |
|---|---|---|
| Feature 2 current state, open gaps, next action | Project memory (decision-005; decision-001 earlier) | Changes every step; the notes-free session got this wrong |
| Rejected approaches for feature 2 | Project memory (decision-001) | Prevents re-debating; expires with the feature |
| Test layout decision | Project memory (decision-002) | A decision with a 90-day review; could be promoted to a standard later |
| How and where backend tests run; no JDK in the dev container | Project memory (decision-003) | A tooling decision with a checkable command and baseline; re-check when the image or Java version changes |
| Error status mapping (400/409/401/404) | Project memory (decision-004) | Concrete, revisable choice tied to the pre-JWT auth design; may be promoted to coding-standards.md once stable |
| Item 2 done; open gaps and recommended next task | Project memory (decision-005) | Status; short review cycle |
| Error convention, 404 rule, no secrets, test style | Knowledge file | Stable; human-owned; protected by `:ro` |
| FEATURES.md, PRDs, rubrics, review report, log | Reference index | Large; consulted only for specific tasks |
| How to run tests, write a handoff | Skills | Procedures, not memory |
| Changed files, test assertions, Javadoc rationale | Not stored | Already in Git, tests, code |
| Error output, prompts, scratch notes | Context window | No value after the session |
| Memory scope declaration (`.memory/SCOPE.md`) | Memory root, imported at startup; human-maintained, agent must not edit | Identifies which project owns the mounted memory directory; the scope check compares it with `git remote -v` before any other memory is used |
| Credential location (env var name, e.g. `ANTHROPIC_API_KEY`) | Project memory (decision-006) | Records how the credential is accessed without storing its value (env var names are Public, values are Secret) |
| Review dates (`Review by:`) | Inside each entry file (authoritative); copied in the index for scanning only | The stale-memory check reads the entry's own date; the index copy hid a stale entry in testing |
| Credential pattern scan | `.git/hooks/pre-commit` (local, unversioned) | Hard stop at commit time; not memory, an enforcement mechanism |
| Credentials and tokens (values) | Environment variables only | Never in any memory layer (Secret) |

## Allocation review (2026-10-17)

Checked every memory file before committing:
- **Procedures:** decision-002's "How to apply" is two lines of where-to-put-tests guidance tied to the decision, not a multi-step procedure, so it stays. "How to run tests" is not stored anywhere in memory.
- **Duplication:** decision-001's current-state list names the four changed files. Kept as a one-line pointer because the fresh session needed to know these were *deliberate partial steps*, which Git alone does not say; the file contents themselves are not copied.
- **Temporary details:** none stored (error output and scratch-note paths were left out).
- **Secrets:** none. coding-standards.md names the environment variable *names* only, never values.
- **Ownership and review rules:** every entry has Date, Review by, Status, and Scope; knowledge and reference files have Maintained by and Last reviewed.

## Data Classification

Before writing anything to a memory file, classify it:

- **Public** — Safe to commit to the repo and share
 broadly. Most project decisions and coding standards
 fall here.

- **Internal** — Safe within the team but not for
 public repos. Store in a non-committed volume or
 .gitignore the containing folder.

- **Confidential** — Sensitive business data. Do not
 store in agent memory. Retrieve from secure systems
 on demand.

- **Secret** — Credentials, tokens, API keys, PII.
 Must never appear in any memory file. If the agent
 encounters a secret during a run, use it for the
 immediate task only and explicitly do not write it
 to any memory layer. Reference the environment
 variable name instead.

In this project: every current memory file is Public (the GitHub repo holds only design decisions and standards). Names of environment variables (`DB_PASSWORD`, `GEMINI_API_KEY`, `ANTHROPIC_API_KEY`) are Public; their values are Secret. Email content handled by the email-summarize agent and anything from the MySQL database are Confidential and never go into memory.

### Credential handling

Credential *values* (API keys, database passwords, Slack/Google tokens) are Secret and never appear in `.memory/`, `CLAUDE.md`, code, or docs. Memory may record the *name* of the environment variable that holds a credential and how it is used, as decision-006 does with `ANTHROPIC_API_KEY`. If a secret is found in a memory file, the agent flags it and stops until a human removes it; a real leaked credential must also be revoked and replaced, because it stays in Git history and existing clones.

### Guardrails

A pre-commit hook at .git/hooks/pre-commit scans
.memory/ for common credential patterns before each
commit. If a pattern is found, the commit is blocked.
See "Enforcement" below for every guard and why it exists.

## Enforcement

Soft guards are instructions in `CLAUDE.md` that the agent is told to follow; they work only if it does. Hard stops are mechanisms that block the action regardless of what the agent decides.

| Guard | Type | Where it lives | What it stops |
|---|---|---|---|
| Scope verification | Soft guard | `CLAUDE.md` > Scope verification; identity in `.memory/SCOPE.md` | Reading or applying memory whose `SCOPE.md` names another project |
| Stale-memory policy | Soft guard | `CLAUDE.md` > Stale memory policy | Acting on an entry whose `Review by:` date has passed, or that disagrees with the index, without human confirmation |
| Classification check | Soft guard | `CLAUDE.md` > Write policy (first rule) | Writing Confidential or Secret data to memory |
| Write permissions by layer | Soft guard (project) / Hard stop (knowledge, reference) | `CLAUDE.md` memory layers + `:ro` bind mounts | project/: agent-writable after checking the index; knowledge/ and reference/: read-only |
| Read-only knowledge/reference | **Hard stop** | `:ro` bind mounts in the `docker run` command | Any write to `.memory/knowledge/` or `.memory/reference/` inside the container, even as root ("Read-only file system") |
| Pre-commit credential scan | **Hard stop** | `.git/hooks/pre-commit` (local; not versioned) | Any commit while a file under `.memory/` matches `sk-` (at a word start), `password=`, `secret=`, `token=`, `api_key=`, or `apikey=` (case-insensitive); exits 1 listing file, line, and pattern |

### Why each safeguard exists (evidence from the Iteration Log)

**Startup loading with `@` imports.**
- Problem: memory must be in context before the first task.
- Observed (run M1, 2026-10-17): with only an instruction to read the index at startup, a fresh session said it had not read any memory file.
- Change: `CLAUDE.md` imports `SCOPE.md`, `MEMORY_INDEX.md`, and `coding-standards.md` with `@`; run M2 then reported scope, entries, and standards correctly.

**Stale-memory policy.**
- Problem: an entry can be wrong while still looking current.
- Observed (run S1, 2026-10-18): decision-004 was edited to an outdated mapping (bad login -> 404) with `Review by: 2026-09-01`. The agent never opened it and declared "no memory entries are past their review date", reading dates from the index, whose copy still said 2027-01-16.
- Change: the policy now requires checking each entry's own `Review by:` line before acting, the exact flag message, and waiting for confirmation; it forbids claiming "nothing is stale" without opening every decision file, and treats an entry/index disagreement as stale. In run S2 the agent opened every entry, compared entry vs index vs last commit vs code, and refused to rely on decision-004 until confirmed. The entry was restored from Git.

**Scope verification.**
- Problem: with the wrong `.memory` mounted, the agent cannot tell another project's decisions from ours.
- Observed (run L1, 2026-10-18): with project-b's memory mounted (403 via ResponseStatusException), the original check did halt, but it offered to continue "once you confirm", and project-b's index had already entered the context through the `@` import.
- Change: the scope check is now the first action, compares `SCOPE.md` with the repo (FashionMate, confirmed with `git remote -v`), outputs a fixed `SCOPE MISMATCH:` message, disregards the imported index and standards on a mismatch, and refuses any "confirm and continue". Run L2 halted without an override; run L3 with the correct mount applied only FashionMate's memory.
- Remaining gap: the `@` imports load memory before any instruction runs, so a wrong mount still puts the other project's index in context. A hard stop would be a launch script or `SessionStart` hook that compares `.memory/SCOPE.md` with `git remote get-url origin` and refuses to start.

**Data classification, credential handling, and the pre-commit hook.**
- Problem: a secret written into memory persists across sessions and in Git history.
- Observed (2026-10-18): `decision-bad.md` containing the fake key `sk-ant-test-FAKEKEYDONOTUSE` was indexed and committed (`ca2484e`) with nothing stopping it. `grep -r "sk-"` found it; grepping bare words (`password`, `secret`) produced only false positives from rules *about* secrets.
- Change: the entry was rewritten as decision-006 pointing to `ANTHROPIC_API_KEY` (`615250e`); the CLAUDE.md write policy begins with a classification check; and the pre-commit hook matches assignment forms (`password=`) and word-initial `sk-` to avoid false positives. It blocked a staged `.memory/test-secret.txt` containing `password=test123` and let the next clean commit through.

**Read-only mounts instead of chmod.**
- Problem: knowledge files must not be edited by the agent.
- Observed (2026-10-17): Claude Code runs as root in the container, so `chmod 444` would not stop writes, and `444` on a directory also blocks reading.
- Change: `:ro` bind mounts; both bash and Claude Code got "Read-only file system" on `touch .memory/knowledge/...`.

### Limits

- Soft guards depend on the agent following `CLAUDE.md`; every one of them was missed or weaker on its first test.
- The pre-commit hook checks only `.memory/`, only the listed patterns (a bare key, a customer name, or a record count would pass), and only on machines where it is installed by hand; it does not clean history. The fake key is still in commit `ca2484e`; a real one would need revoking and a history rewrite.
- The `:ro` protection exists only when the container is started with the read-only command.

## Alternatives considered

1. **Where the "no JDK in the container" fact lives.** It could go in project memory or in a future run-tests skill. I put the limitation in project memory and will keep the procedure in a skill, because the limitation depends on the current Docker image and should be removed when the image changes, while the procedure stays the same. Mixing them would make a stable skill carry stale state.
2. **How to protect the knowledge directory.** The lesson suggests `chmod -R 444`. I used a read-only Docker bind mount instead, because Claude Code runs as root in this container (root ignores file permissions) and `444` on a directory removes the execute bit, which would block reading the files. The tradeoff is that protection depends on starting the container with the right command, so that command is recorded in the iteration log.
3. **Instruction vs import for startup loading.** Relying only on the CLAUDE.md instruction "read the index at startup" was simpler but failed the first fresh-session test. Importing the scope, index, and standards with `@` costs about 1-2k tokens per session but makes startup memory reliable; decision entries stay on-demand so that cost doesn't grow with every new entry.
4. **Where the test-run command lives.** "Run the tests with this Docker command" is procedure-like, so it could be a skill. I recorded it in project memory (decision-003) because the important content is the *choice* (which image, why `contextLoads` is excluded, why not the dev container or Git Bash) and the baseline result, and those change when the environment changes. If test runs become a frequent agent task, the command will move into a run-tests skill and decision-003 will keep only the reasoning.
5. **Soft guard vs hard stop for scope.** The scope check could be a CLAUDE.md instruction or a startup script/hook. I kept the instruction (soft guard) because it needed no extra tooling and passed after strengthening, but recorded the hook as the next step because the `@` imports load the wrong project's index before any instruction can run.
