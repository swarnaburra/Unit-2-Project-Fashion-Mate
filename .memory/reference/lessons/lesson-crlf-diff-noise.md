---
classification: internal
project: proj-lessons
doc_type: lesson
---

## Lesson: most "modified" files in git status are only line endings

What happened: `git status` in FashionMate routinely shows around fifty modified files that nobody edited. During an orchestrated run, an agent saw a 252-line diff stat for StyleLensController.java and nearly treated it as scope creep, and a reviewer once misread the overlay of a mounted memory directory as committed changes.

What we learned: the repository stores LF line endings while the Windows working copy has CRLF, so files touched on Windows look changed. Git prints "CRLF will be replaced by LF the next time Git touches it"; that warning is harmless.

How to apply it: before judging the size of a change, run `git diff --ignore-cr-at-eol --stat` (or `--ignore-all-space`). Only the files that still differ are real changes. Stage files by name rather than with `git add -A`, so line-ending noise never ends up in a feature commit.
