---
name: write-handoff
description: Produces a complete, self-contained handoff document for ending a session and briefing a fresh one. Use when a session is ending and its work needs to continue in a new session with no access to prior context.
---

# Skill: Write Handoff Document

When this skill is invoked, produce a structured handoff document using exactly the format below. This document will be the only context given to the next session. It must be complete enough that the next session can continue the work correctly without access to any prior conversation.

Do not summarize or paraphrase rules, acceptance criteria, or file contents. Copy them verbatim. The next session has no way to check your work against the original, so accuracy here is critical.

## HANDOFF DOCUMENT

### Overall Task
(One paragraph describing what this workflow is trying to achieve and why. Written so that someone with no prior context understands the goal.)

### Phase One Summary
(What was accomplished in the session that produced this handoff. Include what was produced, what decisions were made, and what the current state of each artifact is. Be specific.)

### Current State of All Artifacts
(For each file or document section that was created or modified, provide the section name and the full current text. Do not summarize the text -- include it verbatim so the next session has the exact current state to work from.)

### Rules and Constraints in Effect
(The full, current set of rules and constraints the next session must follow. Copy them verbatim. If rules changed during phase one, state the current rules only -- do not include the old versions.)

### Phase Two Instructions
(Numbered list of exactly what the next session needs to do, in the order it should do it. Be specific enough that the next session could complete the work without asking any clarifying questions.)

### Acceptance Criteria for Phase Two
(Verbatim from the original task specification. What does a correct, complete phase two output look like?)

### Known Constraints and Gotchas
(Anything the next session should be aware of that is not obvious from the instructions above. Edge cases, prior decisions that constrain future choices, things that were tried and did not work.)

After producing the handoff document, ask the user to review it for completeness and accuracy before closing the session. The user may edit it before using it to start the next session.
