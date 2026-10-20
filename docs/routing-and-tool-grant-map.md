# Routing-and-Tool-Grant Map: FashionMate App Change Workflow

Version: 1.0.0 (2026-10-19). Companion to `docs/orchestration-diagram.md`; matches the definitions in `.claude/agents/` (planner 1.0.0, implementer 1.0.0, reviewer 0.2.0, tester 1.0.0, memory-keeper 1.0.0) and the Orchestrator section of `CLAUDE.md`.

**Tool names** are Claude Code's built-in tools: `Read`, `Grep`, `Glob` (read/search), `Edit`, `Write`, `MultiEdit` (change files), `Bash` (run commands), `WebSearch`/`WebFetch` (web), `Agent` (invoke a subagent). This repo has no course MCP server, so there are no `mcp__...` identifiers yet; if one is added, each row maps one-to-one onto its tool names.

## Map

| Role | Receives from Orchestrator | Produces | Tools granted | Tools denied (reason) | Context isolation reason | Autonomy |
|---|---|---|---|---|---|---|
| **Orchestrator** (main session, `CLAUDE.md`) | The task from the human: one FEATURES.md item or review finding | Handoff briefs; `docs/run-summary.md` | `Agent`, `Read`, `Write` (briefs and run summary only) | `Edit`, `MultiEdit` (writes no code), `Bash` (runs nothing; tester owns checks), `Grep`/`Glob` (planner does the searching), `WebSearch`/`WebFetch` (not needed) | It coordinates and judges; without edit or execute tools it can't quietly do a subagent's job, so a failure always traces to one role | Medium (decides routing; pauses at two human checkpoints) |
| **planner** | Change request, repo path, relevant decision IDs | Numbered plan, files to change, tests, open questions | `Read`, `Grep`, `Glob` | `Edit`, `Write`, `MultiEdit` (writes no code), `Bash` (runs nothing), `WebSearch`/`WebFetch` (plans from the repo and memory only) | Reads both app sides and memory but changes nothing; blast radius zero | High (plans are cheap to correct, and a human approves them) |
| **implementer** | Human-approved plan + file list; on loop-back, review findings or test failures | Changed files, plan steps done, blockers; "not yet verified" | `Read`, `Grep`, `Glob`, `Edit`, `Write` | `Bash` (can't run tests and grade its own work), `WebSearch`/`WebFetch` (no outside code); by instruction: no writes to `.memory/` (memory-keeper owns it) | Needs read and write for code; without the test runner it can't mark its own work as passing, and without memory access it can't record a fix as done | Medium (edits code; must stop on anything outside the plan) |
| **reviewer** | Approved plan, changed-file list, standards and decisions to check | Findings table with severity and `file:line` evidence; `PASS` / `CHANGES REQUESTED` | `Read`, `Grep`, `Glob` | `Edit`, `Write`, `MultiEdit` (proposes fixes, never makes them), `Bash` (tester owns checks), `WebSearch`/`WebFetch` (reviews against repo standards only) | Read-only by design, so even at high autonomy it can only report; keeps review independent of implementation | High (read-only) |
| **tester** | Changed files and which side(s) changed | Per side: exact command, quoted summary lines and failures, `PASS` / `FAIL` / `NOT RUN - needs human` | `Read`, `Bash` (only the two commands in its definition: backend Maven test, frontend `npm run build`) | `Edit`, `Write`, `MultiEdit` (must not "fix" code to make a failure pass), `Grep`/`Glob` (doesn't need to search), `WebSearch`/`WebFetch` (not needed) | Owns execution and nothing else; with no write tools it can't mask a failure | Medium (runs code, bounded to two commands) |
| **memory-keeper** (tool-scoping role) | Run summary with the tester's verified result, after human checkpoint 2 | Changed memory files, before/after Status line, new index line | `Read`, `Edit`, `Write` (only under `.memory/project/`) | `Bash` (no command execution near memory), `Grep`/`Glob`, `MultiEdit` (reads only the index and the entry it updates), `WebSearch`/`WebFetch` | The only writer of project memory, so a wrong or premature memory claim traces to one role, and code roles never carry memory-writing duties in their context | Medium (changes the shared record future sessions trust; only after the human confirms) |

## "Which role actually needs this?" tally

| Tool | Roles | Flag? | Verdict |
|---|---|---|---|
| `Read` | Orchestrator, planner, implementer, reviewer, tester, memory-keeper (6) | Yes (more than 2) | Acceptable: a read-only primitive every role needs; can't change anything |
| `Grep`, `Glob` | planner, implementer, reviewer (3) | Yes | Acceptable: read-only search; tester and memory-keeper deliberately don't have it |
| `Write` | Orchestrator, implementer, memory-keeper (3) | **Yes** | Reviewed, kept with limits: each role writes a *different* area (Orchestrator: briefs and `docs/run-summary.md`; implementer: app code and tests; memory-keeper: `.memory/project/`). The limits are instruction-level today, so the overlap is a known risk and a Module 4 candidate for path-scoped enforcement (e.g. extend the existing PreToolUse hook to deny `.memory/` writes from any role but memory-keeper) |
| `Edit` | implementer, memory-keeper (2) | No | Same area split as `Write` |
| `Bash` | tester (1) | No | Only role that executes; limited to two commands by instruction |
| `Agent` | Orchestrator (1) | No | Only the Orchestrator delegates |
| `WebSearch`, `WebFetch`, `MultiEdit` | none | No | No role's job needs them |

## Existing enforcement this map relies on

- **`:ro` mounts** (hard stop): no role can write `.memory/knowledge/` or `.memory/reference/` inside the container.
- **memory-guard PreToolUse hook** (hard stop): blocks any role writing credential patterns into `.memory/`.
- **pre-commit hook** (hard stop): blocks commits while `.memory/` contains credential patterns.
- **Tool allowlists** in each `.claude/agents/*.md` (harness enforcement): a role can't call a tool that isn't listed.
- **Path and command limits** (soft guard, instructions only): implementer not writing `.memory/`, memory-keeper only `.memory/project/`, tester only two commands, Orchestrator writing only briefs and the summary.

## Alternatives considered

- **Giving the implementer `Bash` so it could run the tests itself.** Ruled out: the tester owns execution. An implementer that can run tests can also declare its own work passing, the same failure as the lab misfire where an agent recorded a fix as "Resolved" before any test ran.
- **Letting the implementer update memory when it finishes.** Ruled out: memory writes belong to the memory-keeper, after the tester passes and the human confirms. Otherwise unverified claims reach the record future sessions trust.
- **Giving the reviewer `Bash` to run tests on its suggestions.** Ruled out: it blurs ownership when a test fails and makes the review depend on execution.
- **Using the existing `frontend-build-verifier` agent as the frontend half of the tester.** Considered and deferred: it is narrowly scoped to its own PRD and rubric (`docs/prd.md`), and calling two agents for one phase would double the handoffs. The tester only reports a Rollup optional-dependency failure (quoting the signature) as `FAIL`; the `rm -rf node_modules && npm install` remediation stays with `frontend-build-verifier` or the human, so the tester never gains install or delete commands.
- **One read-only "planner-reviewer" role.** Ruled out: planning (before code) and reviewing (after code) have different inputs, outputs, and gates; merging them would weaken the independent check.
