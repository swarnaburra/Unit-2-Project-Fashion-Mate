# Memory Architecture

## What this workflow needs to remember

This agent helps maintain the FashionMate Spring Boot backend (and, less often, the React frontend) by implementing backlog items from `FEATURES.md` in small, staged steps and reviewing the code for quality and security issues. Across sessions, it needs to remember the decisions made in earlier sessions that the code does not explain (which approach was chosen, which was rejected and why), what is deliberately deferred, the current step of any in-progress feature, the project-wide conventions for error handling and tests, and known environment limitations such as the container having no JDK. It does not need to store which files changed, what the tests assert, or the feature acceptance criteria, because Git, the test classes, and `FEATURES.md` already record those. It must not store credentials or tokens (database password, Gemini API key, Slack token, Google credentials), any user data from the database, raw session transcripts, or notes about a feature step after that step is merged.

The activation exercise showed the split clearly: a fresh session without notes correctly reconstructed the code state from Git and the tests, but it planned deferred work first, proposed a different test style, and re-asked a settled 404-vs-403 question. Those decisions are what memory must carry.

### Classification of items from the activation exercise

| Item | Category | Destination |
|---|---|---|
| UserController error mapping (409/401/400) is deferred until the ReviewController tests pass | Evolving project context | Project memory |
| `checkUserExist` deliberately still throws a raw `RuntimeException` (-> 500) for now | Evolving project context | Project memory |
| Container has no JDK/Maven, and `.mvn/wrapper/maven-wrapper.properties` is missing, so `./mvnw test` fails inside it | Known limitation | Project memory |
| Error-handling convention: custom unchecked exceptions in `fashionmate_backend.exceptions` + one `@RestControllerAdvice`, body `{"message": "..."}`, no `ResponseStatusException` | Stable standard | Knowledge file |
| Ownership failures return 404, never 403, so callers can't probe IDs | Stable standard (security) | Knowledge file |
| How to run the backend tests and where | Repeatable procedure | Skill |
| Which files changed for feature item 2; the failing `deleteReviewDeletesWhenUserExists` test | Existing artifact (Git, tests) | Not stored |
| `./mvnw` error text, the `/tmp/notes.md` file, permission prompts during the run | Temporary detail | Context window only |

## Layer 1: Project memory directory

**Location:** `.memory/project/`

**Belongs here**
- In-progress feature state: "FEATURES.md item 2: steps 1-4 done (ownership check + `ReviewNotFoundException` + `GlobalExceptionHandler`); next step is ReviewControllerTest updates" (2026-10-17).
- Deferred work and why: "UserController 409/401/400 mapping deferred until the ownership tests pass; `checkUserExist` left as raw RuntimeException on purpose."
- Rejected approaches for in-progress work: "Rejected `findById` + compare in controller; rejected `ResponseStatusException` (breaks `hasMessage` assertions, wrong body shape)."
- Known environment limitations: "No JDK/Maven in the `agentic_engineer_2` container; Maven wrapper properties missing. CRLF line-ending noise makes `git diff` show ~50 files modified; use `--ignore-cr-at-eol`."
- Known review findings that turned out wrong: "F2 in `docs/context-run/final-report.md` is incorrect: Spring Data JPA 3.x `deleteById` ignores missing IDs."

**Does not belong here**
- The list of changed files or the diff (Git already records it; duplicating it goes stale after the next edit).
- Test assertions or what a test checks (the test classes are the source of truth).
- How to run tests or write a handoff (procedures belong in skills).
- Secrets, tokens, or database contents (must never be written to any memory layer).
- A chronological log of every decision (that is an audit log; `docs/iteration-log.md` already serves that purpose).

**Scope:** Project-scoped to this repository. Feature-specific entries live in one file per feature (e.g. `.memory/project/feature-2-review-ownership.md`); project-wide state lives in `.memory/project/current-state.md`.

**Write permissions:** Agent-writable. The agent adds or updates an entry when a decision is made, work is deferred, or a phase/step ends. The human reviews entries in the diff before committing.

**Pruning policy:**
- Feature files are deleted when the feature's final commit merges to `main`; any decision that should outlive the feature is promoted to a knowledge file by the human first.
- An entry is replaced (not appended to) when a newer entry supersedes it, e.g. "next step" is overwritten each step.
- Environment-limitation entries are re-checked when the Dockerfile or base image changes.
- Everything in the directory is reviewed every 30 days; entries with no change in 60 days are archived or deleted.

## Layer 2: Knowledge files

**Location:** `.memory/knowledge/`

**Belongs here**
- `error-handling.md`: custom unchecked exceptions in `fashionmate_backend.exceptions`, one `@RestControllerAdvice` (`GlobalExceptionHandler`), consistent `{"message": "..."}` error body; don't throw raw `Exception`/`RuntimeException` in new code; don't use `ResponseStatusException`.
- `security.md`: ownership/authorization failures return 404, not 403; never return the `User` entity (with password) in a response; secrets come only from environment variables (`DB_PASSWORD`, `GEMINI_API_KEY`), never from `application.properties` or committed files; never commit `google_cred.json` or `.env`.
- `testing-conventions.md`: controller unit tests use plain Mockito (`@ExtendWith(MockitoExtension.class)`, `@InjectMocks`, `@Mock`) and AssertJ `assertThatThrownBy(...).hasMessage(...)`; MockMvc tests are added only for end-to-end HTTP checks and only when the task asks for them.
- `scope-rules.md`: work one `FEATURES.md` item at a time; don't change files outside the item's stated scope without asking.

**Does not belong here**
- The current step of a feature or what is deferred right now (changes every session; belongs in project memory).
- Procedures such as "run `./mvnw test`" (belongs in a skill).
- Rules that already exist in `CLAUDE.md` (the context boundary procedure); duplicating them risks the two copies drifting apart.

**Scope:** Project-scoped, all workflows and agents in this repository.

**Write permissions:** Read-only for the agent. Only a human edits these files, through a reviewed commit. If the agent thinks a rule is wrong or missing, it proposes a change in its reply or in a project-memory entry; it does not edit the file.

**Pruning policy:**
- Reviewed when a related decision changes (e.g. JWT auth from `FEATURES.md` item 1 lands, which changes `security.md`).
- Each file carries a `Last reviewed:` date; any file not reviewed in 90 days is reviewed before the next feature starts.
- A rule is removed only when the human records the replacing decision in the same commit.

## Layer 3: Indexed reference documents

**Location:** `.memory/reference/`, with `.memory/reference/INDEX.md` listing each document, a one-line description, and when to consult it. Most documents stay where they already are; the index points to them instead of copying them.

**Belongs here (via the index)**
- `FEATURES.md`: the backlog with acceptance criteria; consult when starting or scoping a feature.
- `docs/prd.md`, `docs/prd-backend-tests.md`: PRDs for the build-verification and test-summary agents; consult when changing those agents.
- `docs/iteration-log.md`: past runs, scores, and lessons; consult when evaluating an agent or rubric.
- `docs/context-run/final-report.md`: prioritized review findings (F1-F9, S1-S3) with test recommendations; consult when picking the next security or reliability fix.
- `docs/rubric*.md`: rubrics for each agent; consult when scoring a run.

**Does not belong here**
- Raw transcripts (`docs/transcripts/*.jsonl`, `docs/evidence/*`): too large and noisy to be useful context; they stay as evidence only, not indexed.
- Anything that must be read every session (that belongs in a knowledge file or `CLAUDE.md`).
- Documents containing secrets or personal data (e.g. email content from the email-summarize agent runs).

**Scope:** Project-scoped. The index is shared by all agents in this repository.

**Write permissions:** The human maintains `INDEX.md` and the documents. The agent may propose a new index entry when it creates a lasting document (e.g. a new final report), but the human adds it.

**Pruning policy:**
- An index entry is removed in the same commit that deletes or replaces the document.
- When a document is superseded (e.g. a newer review report), the old entry is marked "superseded by ..." for one review cycle, then removed.
- The index is reviewed at the end of each course unit or every 90 days, whichever comes first.

## Allocation decision table

| Information | Where | Reason |
|---|---|---|
| Current step and next action for feature item 2 | Project memory | Changes every step; not inferable from code |
| UserController work deferred; `checkUserExist` left alone on purpose | Project memory | A fresh session without it planned the deferred work first |
| Rejected approaches (findById + compare, ResponseStatusException) | Project memory | Prevents re-debating settled choices; expires with the feature |
| No JDK in container; CRLF diff noise | Project memory | Environment state; saves several turns of re-discovery; may change with the Dockerfile |
| Error-handling convention (`{"message": ...}`, custom exceptions + one advice) | Knowledge file | Stable, applies to all future features, should change only with human review |
| 404 not 403 for ownership; never return User with password; secrets via env vars | Knowledge file | Security rules; must not be edited by the agent |
| Mockito-only controller test style | Knowledge file | Stable convention matching the existing test suite |
| How to run backend tests (and that it needs a JDK) | Skill | Procedure, not memory |
| How to write a handoff / summarize a session | Skill (already `.claude/skills/write-handoff`, `summarize-session`) | Procedures already versioned |
| FEATURES.md, PRDs, iteration log, review report | Indexed reference | Too large to load every session; needed only for specific tasks |
| Files changed, test assertions, 404 rationale in Javadoc | Existing artifact (not stored) | Git, tests, and code already record them |
| Error output, permission prompts, `/tmp/notes.md` | Context window only | No value after the session |
| DB password, Gemini key, Slack token, Google credentials | Environment variables only | Secrets never go in any memory layer |

## Alternatives considered

The "no JDK in the container, so tests can't run here" fact could have gone in project memory or in the test-running skill. I put the limitation in project memory and the procedure in the skill, because the limitation depends on the current Docker image and should be re-checked and removed when the image changes, while the procedure ("run `./mvnw test` in `fashionmate-backend`") stays the same. Putting the limitation inside the skill would make a stable procedure carry state that goes stale.
