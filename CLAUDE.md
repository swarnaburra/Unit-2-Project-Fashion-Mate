
# Agent Context Boundary Policy (v0.1.1)

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
