# Iteration Log

Records each run of every single-agent workflow in this repo, scored against its
corresponding rubric.

## Frontend Build Verification Agent

Workflow defined in `docs/prd.md`, scored against `docs/rubric.md`.

| Run ID | Date | Agent/Tool | Prompt/Command Used | Cycle Time | Rubric Scores (1-4 each) | Pass/Fail | Review Latency | Cost | Observations |
|--------|------|------------|----------------------|------------|--------------------------|-----------|-----------------|------|--------------|
| 001 | 2026-08-25 | Claude Code (`claude -p --allowedTools="Bash" --output-format json`) in container `quality-agent`, image `agent-sandbox:fashionmate` | Prompt 001 (see below) | 16.03s | Execution Fidelity: 3, Verdict Accuracy: 3, Evidence Quality: 3, Scope Compliance: 4, Recommendation Quality: 3 | **PASS** (all dimensions >= 3) | Reviewed immediately after run completion; not separately instrumented | $0.0273 (4 input / 713 output / 69,024 cache-read tokens) | Build genuinely fails in this environment: `Cannot find module '@rollup/rollup-linux-x64-gnu'`, a known npm optional-dependency bug (npm/cli#4828), not a source defect. Agent quoted the real error, correctly diagnosed the cause, ran only the authorized command, and proactively asked before running the standard `rm -rf node_modules && npm install` remediation instead of just doing it or silently giving up. `git status` confirmed zero files touched. |
| 002 | 2026-08-25 | Claude Code (`claude -p --allowedTools="Bash" --output-format json`) in container `quality-agent`, image `agent-sandbox:fashionmate` | Prompt 002 (see below) | 15.97s | Execution Fidelity: 4, Verdict Accuracy: 3, Evidence Quality: 3, Scope Compliance: 4, Recommendation Quality: 3 | **PASS** (all dimensions >= 3) | Reviewed immediately after run completion; not separately instrumented | $0.0619 (4 input / 517 output / 59,423 cache-read tokens) | Same underlying build failure as Run 001 (env-level Rollup optional-dependency bug, not a code defect) -- result content is stable across runs, confirming the workflow is deterministic on this repo's current state. The one prompt change (explicitly require the exact command and exit code) raised Execution Fidelity from 3 to 4 exactly as intended: the report now states `Command run: npm run build ... Exit code: 1` verbatim instead of just a prose verdict. `git status` confirmed zero files touched. Cost per run roughly doubled (Run 001 $0.0273 -> Run 002 $0.0619) despite a near-identical cycle time -- flagged for a follow-up check (see Run 003). |
| 003 | 2026-08-26 | Claude Code (`claude -p --allowedTools="Bash" --output-format json`) in container `quality-agent`, image `agent-sandbox:fashionmate` | Prompt 002 (identical to Run 002 -- rerun to isolate the Run 001/002 cost anomaly) | 15.22s | Execution Fidelity: 4, Verdict Accuracy: 3, Evidence Quality: 3, Scope Compliance: 4, Recommendation Quality: 3 | **PASS** (all dimensions >= 3) | Reviewed immediately after run completion; not separately instrumented | $0.0391 (4 input / 666 output / 65,862 cache-read tokens) | Same prompt as Run 002, but cost landed between Runs 001 and 002 ($0.0391 vs $0.0273 / $0.0619), and cache-read tokens moved again (65,862, between 69,024 and 59,423). Since Runs 002 and 003 used the byte-identical prompt yet cost differs by ~58%, this confirms the cost variance is **not** caused by the one-sentence prompt change -- it's cache-hit variability between separate invocations (likely prompt-cache TTL/eviction between calls spaced apart in time). Cost tracks inversely with cache-read tokens across all three runs. Content, verdict, and all rubric scores were identical to Run 002, confirming the workflow itself is deterministic; only the cache-driven cost is noisy run-to-run. Also note: the `claude` CLI's background self-updater broke the container's binary again before this run (installed a Windows `claude.exe` inside the Linux container, same failure mode as an earlier session) -- fixed by reinstalling via `npm install -g @anthropic-ai/claude-code`, and `DISABLE_AUTOUPDATER=1` was added to the `Dockerfile` to prevent recurrence going forward. |
| 004 | 2026-08-26 | Claude Code (`claude -p --allowedTools="Bash" --output-format json`) in container `lab-frontend`, worktree `../fashionmate-lab-frontend` (branch `lab-frontend-build`), image `agent-sandbox:fashionmate` | Prompt 002 (unchanged) | 24.69s | Execution Fidelity: 4, Verdict Accuracy: 3, Evidence Quality: 3, Scope Compliance: 4, Recommendation Quality: 3 | **PASS** (all dimensions >= 3) | Reviewed immediately after run completion; not separately instrumented | $0.0302 (4 input / 498 output / 65,122 cache-read tokens) | Module 1 Lab: run from a fresh Git worktree, not the original repo folder. `node_modules` didn't exist yet (gitignored, never copied by `git worktree add`) -- first attempt correctly reported `sh: 1: vite: not found` (exit 127) without touching any file or installing anything itself, consistent with Scope Compliance. I ran `npm install` myself as an orchestrator setup step (not scored), then re-ran: this time the build **succeeded** (exit 0, 66 modules, no errors/warnings) -- a fresh install in this worktree resolved the `@rollup/rollup-linux-x64-gnu` optional-dependency bug that has affected every run against the original repo folder (Runs 001-003). Confirms the failure in Runs 001-003 was specific to that folder's stale `node_modules`, not a repo-wide or code-level defect. `git status` in the worktree showed zero changes (node_modules/dist are gitignored). |

## Prompt 001 (baseline)

> Run this repo's frontend production build. From fashionmate-frontend/fashion-app, run
> npm run build. Report whether the build succeeds, summarize any errors or warnings in
> the output, and recommend whether the frontend is ready for the next step. Do not
> modify any file, do not run npm install with --save or --force, and do not push,
> publish, or deploy anything.

## Prompt 002 (one change: require exact command + exit code)

> Run this repo's frontend production build. From fashionmate-frontend/fashion-app, run
> npm run build. Report whether the build succeeds, summarize any errors or warnings in
> the output, and recommend whether the frontend is ready for the next step. **State the
> exact command you ran and its exact exit code in your report.** Do not modify any
> file, do not run npm install with --save or --force, and do not push, publish, or
> deploy anything.

## Backend Test Suite Summary Agent

Workflow defined in `docs/prd-backend-tests.md`, scored against
`docs/rubric-backend-tests.md`. Added as the second task for Module 1's parallel-agent
lab.

| Run ID | Date | Agent/Tool | Prompt/Command Used | Cycle Time | Rubric Scores (1-4 each) | Pass/Fail | Review Latency | Cost | Observations |
|--------|------|------------|----------------------|------------|--------------------------|-----------|-----------------|------|--------------|
| 001 | 2026-08-26 | Claude Code (`claude -p --allowedTools="Bash,Write" --output-format json`) in container `lab-backend`, worktree `../fashionmate-lab-backend-tests` (branch `lab-backend-tests`), image `agent-sandbox:fashionmate` | Prompt 001 (see below) | 146.75s | Execution Fidelity: 3, Count Accuracy: 3, Evidence Quality: 4, Scope Compliance: 4, Output Completeness: 4 | **PASS** (all dimensions >= 3) | Reviewed immediately after run completion; not separately instrumented | $0.2461 (14 input / 2,579 output / 278,391 cache-read tokens) | Ran in a fresh worktree with no Maven local-repo cache, so most of the 146.75s cycle time is dependency download, not agent reasoning. Result: 23 tests, 22 passed, 1 errored (`FashionmateBackendApplicationTests.contextLoads`, blocked by no live MySQL connection -- an environment issue, same known condition documented in `setup.md`, not a regression). `docs/test-report.md` includes a per-class table and the full quoted Hibernate/JDBC exception chain, and explicitly distinguishes this as environment-only rather than a code defect (Evidence Quality level 4). `git status` showed only the new `docs/test-report.md` -- no source or test file touched (Scope Compliance level 4). Execution Fidelity held at 3, not 4: the report states the command and Maven's textual result (`BUILD FAILURE`) but not a literal numeric shell exit code -- same gap Prompt 002 fixed for the frontend workflow; worth carrying that same instruction into a future prompt revision for this task. |

## Prompt 001 (backend tests, baseline)

> Run the backend test suite. From fashionmate-backend, run mvn test. Report the total
> number of tests run, how many passed, failed, and errored, and quote the actual
> failure/error messages for any that did not pass. Save the summary to
> docs/test-report.md. Do not fix any failing tests. Do not modify any source or test
> files.
