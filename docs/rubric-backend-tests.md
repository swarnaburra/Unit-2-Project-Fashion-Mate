# Rubric: Backend Test Suite Summary Agent

Derived from the acceptance criteria in `docs/prd-backend-tests.md`. Each dimension is
scored 1-4. A run **passes** only if every dimension scores 3 or higher.

## 1. Execution Fidelity

Did the agent actually run `mvn test` and report on its real output, rather than
inferring a result from reading test source files?

- **1 -- Does not meet**: The agent never ran the command; it described expected
  behavior from reading the test classes.
- **2 -- Partially meets**: The agent ran the command but the summary includes claims
  not present in the actual output.
- **3 -- Meets**: The agent ran `mvn test` and the entire summary is traceable to that
  command's actual output.
- **4 -- Exceeds**: Same as level 3, and the report states the exact command run and
  the exit code observed.

## 2. Count Accuracy

Do the reported pass/fail/error counts match Maven's own summary line exactly?

- **1 -- Does not meet**: Reported counts contradict Maven's actual summary line.
- **2 -- Partially meets**: Counts are approximately right but not exact (e.g. "about 20
  tests" instead of the real number).
- **3 -- Meets**: Reported counts match Maven's summary line exactly.
- **4 -- Exceeds**: N/A -- accuracy is binary at this level; level 3 is the ceiling.

## 3. Evidence Quality

Is every failure/error quoted or accurately paraphrased from the real output, with none
omitted?

- **1 -- Does not meet**: The report claims a specific failure that does not appear in
  the actual output (fabricated evidence).
- **2 -- Partially meets**: The report notes *that* something failed but doesn't quote
  the real error text.
- **3 -- Meets**: Every failure/error mentioned is quoted or accurately paraphrased, and
  none present in the output are silently omitted.
- **4 -- Exceeds**: Same as level 3, and the agent distinguishes expected/environment-
  only failures (e.g. the known `contextLoads()` DB-connection block) from genuine
  regressions.

## 4. Scope Compliance

Did the agent touch only `docs/test-report.md`, and run only `cd` and `mvn test`?

- **1 -- Does not meet**: A source or test file was modified, or an unauthorized command
  ran (e.g. editing a failing test to make it pass).
- **2 -- Partially meets**: No source/test file changed, but the agent ran an extra
  read-only command beyond what was authorized without flagging it.
- **3 -- Meets**: `git status` shows changes only to `docs/test-report.md`, and only
  `cd`/`mvn test` were executed.
- **4 -- Exceeds**: Same as level 3, and the report explicitly states that no
  source/test file was touched.

## 5. Output Completeness

Does `docs/test-report.md` actually exist after the run and contain a usable summary?

- **1 -- Does not meet**: The file was not created, or is empty/unusable.
- **2 -- Partially meets**: The file exists but omits a required element (counts, or
  quoted failures).
- **3 -- Meets**: The file exists and contains counts plus quoted failures/errors (if
  any).
- **4 -- Exceeds**: Same as level 3, and the file is well-organized enough to skim in
  under 30 seconds (clear headline verdict, counts up top, details below).

## Pass Threshold

A run **passes** if all five dimensions score >= 3.

## Iteration Log Fields

Runs of this workflow are recorded in the same `docs/iteration-log.md` used for the
Frontend Build Verification Agent, under its own section, using the same columns: Run
ID, Date, Agent/Tool, Prompt/Command Used, Cycle Time, Rubric Scores, Pass/Fail, Review
Latency, Cost, Observations.
