
# Agent Context Boundary Policy (v0.2.0)

## Purpose
This file defines how context boundaries are managed in this project.
At the start of each new phase of work, the agent must follow the
procedure below before taking any action.

## Context Boundary Procedure
At the start of each new phase, before doing any editing or analysis:

1. Restate the current task goal in one sentence.
2. List the rules currently in effect (verbatim, not paraphrased).
3. State explicitly which prior rules are no longer in effect, if any.
4. Identify the specific artifact being worked on in this phase.
5. Then proceed with the requested work.

## Why This Matters
Rules and requirements change during long sessions. This procedure ensures the agent is always operating from the current version of the rules, not a prior version buried in conversation history.

## Lessons from validation (v0.1.1)
Validated against the 8-message blog editing task (baseline 11/12, with boundaries 11/12; violations dropped from 5 to 1).
6. Restate rules from the most recent context boundary block only, not from earlier messages.
7. Drop change labels such as (NEW) or (CHANGED) after the phase in which they were introduced.
8. Before claiming a section complies, re-check any newly written text (for example, a new opening question) against every rule, including passive voice.
9. The procedure applies to editing and analysis phases, not to read-only questions.

## Compaction Policy

Compaction is a last resort. Proactive summarization (see .claude/skills/summarize-session/SKILL.md) should be triggered before the context window exceeds 60% capacity to avoid relying on compaction. With a 1M-token window, phase changes (such as a rule change) are the more practical trigger, because this task never exceeded about 10%.

Observations from testing:
- Compaction reliably preserves: the style guide rules verbatim, the current edited text of finished sections, and the fact that no rules had changed yet.
- Compaction may lose or distort: the user's original task wording (paraphrased), numeric details (the summary reported wrong sentence word counts), and the work-remaining list (it added items that were never planned).
- Manual compaction should be triggered at: a phase boundary, after running /summarize-session, or at about 60% context if a session runs long.

# Agent Instructions

## Memory Configuration

The scope declaration and memory index are imported below so they are
loaded automatically at session start:

@.memory/SCOPE.md
@.memory/project/MEMORY_INDEX.md
@.memory/knowledge/coding-standards.md

Individual decision entries are not imported; read the ones the index
marks as relevant to the current task.

At the start of every session, read .memory/project/MEMORY_INDEX.md
to orient yourself. Then read any active entries listed there that
are relevant to the current task.

Before making any significant decision or observing something worth
remembering across sessions, check the index for an existing entry
on the same topic. Update existing entries rather than creating
duplicates.

### Memory layers

- .memory/project/ — Read on startup via MEMORY_INDEX.md. You may
 write new entries here when a significant decision is made or
 project state changes.

- .memory/knowledge/ — Read-only. Consult before making any decision
 that touches coding standards or architectural constraints. Never
 attempt to write to this directory.

- .memory/reference/ — Read-only. Start from .memory/reference/INDEX.md,
 which points to large documents elsewhere in the repo and says when to
 consult each. Read only the relevant document and section. Do not read
 the entire directory or every indexed document.

### Write policy

Before writing any content to a memory file,
classify it:
- If it is Public or Internal: proceed with writing
- If it is Confidential: do not write it to memory.
 Note in your response that the information was
 not stored and explain where it should be
 retrieved from instead.
- If it is Secret (credential, token, API key, PII):
 do not write it anywhere. Use it for the immediate
 task only. If you find a secret already written in
 a memory file, flag it immediately and do not
 proceed until a human removes it.

(Classification levels are defined in docs/memory-architecture.md,
"Data Classification". Environment variable *names* are Public;
their values are Secret, including fake, test, or sandbox values
and including when the user asks you to store them.)

Two hard stops back this up; do not rely on them instead of
classifying:
- A Claude Code PreToolUse hook (scripts/hooks/memory-guard.js,
 configured in .claude/settings.json) blocks any Write, Edit,
 MultiEdit, or Bash write into .memory/ whose content matches
 scripts/hooks/secret-patterns.txt.
- A pre-commit hook (scripts/hooks/pre-commit, installed into
 .git/hooks/) blocks commits while .memory/ matches the same patterns.
If memory-guard blocks a write, do not retry with the value split,
encoded, reworded, or moved to another file. Remove the value, keep
only the environment variable name, and tell the user it was not stored.

Record verification status honestly. Do not mark work as
"resolved", "done", or "verified" in memory until its tests have
actually run and passed. Until then write "implemented, not yet
verified (tests not run)", and update the entry with the test
count and date once a human reports the result. Tests cannot run
inside this container (no JDK; see decision-003).

Before writing a new memory entry, check MEMORY_INDEX.md for an
existing entry on the same topic. Update existing entries rather
than creating new ones. Never write anything classified as
Confidential or Secret to any memory layer.

### Stale memory policy

Before acting on any memory entry, check its review date.
If the review date has passed:
1. Do not act on that entry until a human confirms it
  is still accurate
2. State clearly in your response: "Memory entry
  [filename] has a review date of [date], which has
  passed. Please confirm this is still current before
  I proceed."
3. Wait for confirmation before using the entry

This applies to all entries in .memory/project/.
Knowledge files are maintained by humans directly; flag
them only if their own "Review by" date has passed.

The authoritative review date is the "Review by:" line
inside the entry file itself, not the date shown in
MEMORY_INDEX.md (the index copy can be out of date).
Never state that no entries are stale unless you have
opened every file in .memory/project/decisions/ and
checked its "Review by:" line. If the entry and the
index disagree on anything (date, status, or content),
report the mismatch and treat the entry as stale.

### Scope verification

The very first thing you must do at the start of
every session — before reading any memory entries
or doing any work — is:

1. Read .memory/SCOPE.md (/workspace/.memory/SCOPE.md inside
  the container)
2. Check that the Project name in that file matches
  the name of the repo you are currently working in.
  This repo is FashionMate (Unit-2-Project-Fashion-Mate);
  confirm with `git remote -v` if unsure.
3. If they do not match, stop immediately and output:
  "SCOPE MISMATCH: Memory directory belongs to
  [name in SCOPE.md] but current project is
  [current project]. Do not proceed until the
  correct memory directory is mounted."
4. Do not read any other memory files until the
  scope check passes

On a mismatch, also disregard the memory index and coding
standards imported above: they were loaded from the same
wrong directory. Do not offer to continue with that memory
if the user "confirms"; the only fix is mounting the
correct .memory directory and starting a new session.

### Knowledge directory protection

The container mounts .memory/knowledge/ and .memory/reference/ read-only
(`:ro`), so writes fail with "Read-only file system" even as root.

Never modify file permissions in .memory/knowledge/ or .memory/reference/
without explicit human instruction. If a write there fails (permission
denied or read-only file system), stop and ask the human; do not try to
work around it.

## Orchestrator workflow instructions

These instructions make the main session the Orchestrator for the FashionMate
App Change Workflow (`docs/orchestration-diagram.md`,
`docs/routing-and-tool-grant-map.md`). They apply only when the human asks for an
orchestrated run, e.g. "Run the change workflow on FEATURES.md item 5". For
ordinary questions or single-file help, work normally under the sections above.

### Role
You are the Orchestrator. You decompose the task, delegate each piece to the
right subagent with the Agent tool, judge each result, and assemble the final
output. You do not write or edit application code, run tests or builds, or
write project memory. If you are about to do a subagent's work, stop and invoke
that subagent instead. Use Write only for handoff briefs and
`docs/run-summary.md`.

### How this fits the rules above
- Context Boundary Procedure: run it at the start of the orchestrated run and
  at each loop-back; restate the gates below as the rules in effect.
- Scope verification and stale-memory policy: you check scope at startup; pass
  the relevant decision IDs to each subagent, and treat a subagent's stale-entry
  report as a blocker.
- Write policy: during an orchestrated run, project-memory writes go only
  through the memory-keeper (after checkpoint 2), not the main session or any
  other subagent. Outside an orchestrated run, the write policy above applies
  unchanged.
- Verification-status rule: memory may say "verified" only with the tester's
  quoted result.

### Workflow goal and acceptance criteria
Goal: deliver one FEATURES.md item or review finding, as described in the task
and FEATURES.md (via `.memory/reference/INDEX.md`). The run is accepted only
when all of the following hold:
- the change does what the approved plan and FEATURES.md acceptance criteria say,
- the reviewer's verdict is PASS (no high-severity findings, at most three findings),
- the tester's verdict is PASS for every changed side (backend Maven tests,
  frontend `npm run build`), with quoted output,
- the memory-keeper has recorded the verified outcome, after human confirmation.

### MCP tools (coursetools)
The planner (v1.1.0) and reviewer (v0.3.0) use the `coursetools` MCP server
(`.mcp.json`, `mcp/coursetools_server.py`) instead of built-in tools. Every
coursetools call carries a `role` argument checked against
`mcp/roles.allowlist.json`. When you brief the planner or reviewer, remind them
to pass their own role name. Treat an "Authorization error" from the server as
a blocker to report, never as something to work around with another tool or
role name. Never call coursetools yourself with another role's name.

### Standard sequence
Use `.memory/knowledge/handoff-orchestrator-to-subagent.md` for every brief and
expect `.memory/knowledge/handoff-subagent-to-orchestrator.md` back.
1. planner        receives: task text, repo path, relevant decision IDs
2. implementer    receives: approved plan + file list
3. reviewer       receives: approved plan, changed files, standards/decisions to check
4. tester         receives: changed files + which side(s) changed
5. memory-keeper  receives: run summary with the tester's quoted result

### Evaluation gate
After each subagent returns, check its result against that phase's criteria
and its required output format BEFORE invoking the next role. Pass forward only
a result that meets them; otherwise apply the branching logic.
- planner: every step names files; tests listed; open questions resolved by the human.
- implementer: only planned files changed; says "not yet verified"; no blockers.
- reviewer: verdict PASS.
- tester: verdict PASS for every changed side.
- memory-keeper: existing entry updated (no duplicate), Status quotes the test count and date.

### Branching logic
- Loop: reviewer verdict CHANGES REQUESTED (more than three findings or any
  high-severity) -> implementer with the review report, then reviewer again.
- Loop: tester verdict FAIL -> implementer with the quoted failures, then tester again.
- Wait: tester verdict NOT RUN - needs human -> show the human the host command
  (decision-003) and wait for the pasted output; give it to the tester to report.
- Skip: if the plan says "no code change required", skip the implementer and the
  tester; go to the reviewer, then the memory-keeper.
- Halt and escalate: if the same gate fails twice in a row, or a subagent
  reports a blocker (stale memory, a file outside the plan, a memory-guard
  block), stop and report to the human with a short summary of what failed.

### Human-in-the-loop checkpoints
- Checkpoint 1, after the planner: show the plan and open questions; write no
  code until the human approves.
- Checkpoint 2, before the memory-keeper: show the run summary and the tester's
  quoted result; update memory only after the human confirms.

### Output
Write `docs/run-summary.md`: task, run ID, each phase's result and attempts,
review verdict, test results (quoted), memory entries changed, and anything
deferred or escalated.
