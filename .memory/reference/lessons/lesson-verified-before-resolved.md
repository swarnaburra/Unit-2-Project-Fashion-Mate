---
classification: internal
project: proj-lessons
doc_type: lesson
---

## Lesson: never record a fix as resolved before its tests have run

What happened: after implementing the password fix, an agent updated project memory to say the gap was "Resolved" before any test had run; it cannot run tests in its container. In another run, an implementer's report said "no retrieve errors" while the retrieval server's log showed two rejected calls.

What we learned: an agent's own summary is not evidence. Only the tester's quoted Maven output, and server-side logs such as the storage audit log, show what actually happened.

How to apply it: write "implemented, not yet verified (tests not run)" until the human pastes passing test output, then record the exact test count and date. When an agent's report and a server log disagree, trust the log and record the discrepancy.
