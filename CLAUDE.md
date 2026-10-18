
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
their values are Secret. A pre-commit hook blocks commits when
.memory/ contains credential patterns, but do not rely on it.)

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
