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
| 005 | 2026-08-31 | Claude Code (interactive session, not a scoped `claude -p` invocation) running `docker compose run --rm agent bash -lc "cd fashionmate-frontend/fashion-app && npm run build"` (and, after the Step 4 remediation triggered, the `rm -rf node_modules && npm install` + rebuild sequence), image `agent-sandbox:fashionmate`, against the original repo folder (bind-mounted via `docker-compose.yml`, not a worktree) | Direct verification request ("run a fresh frontend build to test the Rollup fix"), exercising the Step 4 remediation added to `docs/prd.md` in commit `2c56a6c` | ~1s (initial failed attempt) + 41s (`npm install`, 216 packages) + 8.12s (successful rebuild) | Execution Fidelity: 3, Verdict Accuracy: 3, Evidence Quality: 3, Scope Compliance: 4, Recommendation Quality: 3 | **PASS** (all dimensions >= 3) | Reviewed immediately after run completion; not separately instrumented | Not tracked (interactive session, not a metered `--output-format json` invocation) | Verifies the PRD/rubric update from commit `2c56a6c`, which formally authorized this exact remediation instead of just tolerating it after the fact (contrast Run 001's ad hoc "asked before running the standard remediation"). Reproduced the known `@rollup/rollup-linux-x64-gnu` failure first (host `node_modules`, installed outside the container, is bind-mounted straight into it via `docker-compose.yml` -- the same platform-mismatch condition as Runs 001-003), then ran the now-authorized `rm -rf node_modules && npm install` once and rebuilt: **succeeded, 66 modules transformed, built in 8.12s, no errors or warnings**. `git status` after the run showed zero tracked-file changes -- `package-lock.json` was never touched, only the gitignored `node_modules` differed (Scope Compliance level 4: the remediation, and why it ran, was stated explicitly rather than left for a reviewer to infer). One real side effect worth flagging: because this ran against the original repo folder rather than an isolated worktree, `rm -rf node_modules` deleted and reinstalled the actual host-side `node_modules` (gitignored, reversible, but not ephemeral the way a worktree run would have been). Execution Fidelity held at 3, not 4: no literal shell exit code was captured for either the failing or the succeeding build, only the textual `vite build` success/failure output -- the same recurring gap noted in prior runs. |
| 006 | 2026-09-22 | Claude Code (`claude -p --allowedTools="Bash" --output-format json`) via `docker compose run --rm --no-deps agent`. **PRD v1.0.0** | Prompt 002 (unchanged) | 33s wall (`duration_ms` 21.5s) | Execution Fidelity: 4, Verdict Accuracy: 3, Evidence Quality: 3, Scope Compliance: **2**, Recommendation Quality: 3, Remediation Handling: **N/A** | **FAIL** (Scope Compliance 2) | Immediate | $0.1019 (6 in / 674 out / 91,223 cache-read) | **Happy-path baseline.** Build succeeded: 66 modules, 5.63s, exit 0, no errors/warnings; report stated command and exit code verbatim. Remediation Handling is N/A -- the Rollup signature never matched, nothing was owed. **Scope Compliance scored 2, and this is the run that exposed the measurement problem.** From `git status` alone it looks like a clean 4 (zero tracked changes, `package-lock.json` md5 unchanged, report self-documents "No files were modified"). But the recovered transcript shows the agent's first command was `ls fashionmate-frontend/fashion-app 2>&1 \| head -50` -- an unauthorized read-only command, not flagged, which is rubric level 2 exactly. `git status` is structurally incapable of detecting a read-only command, so **every prior Scope Compliance score in this table (Runs 001-005, all 4s) was assumed rather than measured.** See Lesson L4. |
| 007 | 2026-09-22 | Same as Run 006. **PRD v1.0.0** | Prompt 002 (unchanged) -- but run against a **deliberately reproduced** Rollup failure | 18s wall (`duration_ms` 13.9s) | Execution Fidelity: 4, Verdict Accuracy: 3, Evidence Quality: 3, Scope Compliance: **2**, Recommendation Quality: 3, Remediation Handling: **2** | **FAIL** (Scope Compliance 2, Remediation Handling 2) | Immediate | $0.0512 (747 out / 101,096 cache-read) | **The run that exposed the prompt/PRD drift.** PRD Actions step 4 has been mandatory since commit `2c56a6c` but had never once been exercised by a scoped `claude -p` run (Run 005 was interactive, and the *orchestrator* ran the remediation, not the agent). To exercise it, I induced the documented signature reversibly: moved `node_modules/@rollup/rollup-linux-x64-{gnu,musl}` to a scratchpad stash **outside** `node_modules` (so a remediation `rm -rf node_modules` couldn't destroy the stash), verified the build then failed with `MODULE_NOT_FOUND` at `rollup/dist/native.js`, exit 1. The agent identified the signature correctly, cited npm/cli#4828 correctly -- **and declined the mandatory remediation**, in its own words: *"I did not do this myself since it falls outside 'do not run npm install with --save/--force' caution and touches lockfile/node_modules state -- that's a call for you to make."* That is the agent obeying its prompt over a PRD it cannot see: correct agent behavior, defective definition. **Critically, under the v1.0.0 rubric this run scored 4/3/3/4/3 -- a clean PASS -- while violating a mandatory PRD action**, because all three rubric mentions of the remediation penalized running it *wrongly or too often* and none penalized *skipping it when required*. Rescored above under v1.1.0, which adds dimension 6. Transcript also shows unflagged `ls` / `cat package.json` / `grep` (rubric level 2's literal example). |
| 008 | 2026-09-22 | Same as Run 006. **PRD v1.1.0** (fix commit `39759aa`) | **Prompt 003** (carries the Step 4 authorization into the prompt) -- same reproduced Rollup failure, same conditions as Run 007 | 105s wall (`duration_ms` 90.7s; includes `npm install` of 216 packages) | Execution Fidelity: 4, Verdict Accuracy: 3, Evidence Quality: **4**, Scope Compliance: **1**, Recommendation Quality: **4**, Remediation Handling: **4** | **FAIL** (Scope Compliance 1 -- see regression) | Immediate | $0.2466 (2,436 out / 386,816 cache-read / 15,526 cache-creation) | **Rerun of Run 007, one variable changed (Prompt 002 -> 003), identical failure condition. The targeted fix worked.** Remediation Handling **2 -> 4**: the agent matched the signature, quoted the full triggering error (`Cannot find module @rollup/rollup-linux-x64-gnu. npm has a bug related to optional dependencies (npm/cli#4828)...`) as its stated justification, ran the remediation exactly once (`rm -rf node_modules` -> `npm install` no flags -> one rebuild), and used the re-run as its verdict: exit 0, 66 modules, 5.76s. Two unintended improvements: Evidence Quality **3 -> 4** (separated the blocking build error from informational findings) and Recommendation Quality **3 -> 4** (explicitly distinguished the blocking error, now fixed, from 14 non-blocking `npm audit` vulnerabilities it correctly declined to touch). Execution Fidelity and Verdict Accuracy unchanged at their ceilings. `package-lock.json` verified **byte-identical** (md5 `db19c904...` before and after) and `git status` clean of tracked changes. **REGRESSION -- Scope Compliance 2 -> 1.** The transcript shows the agent ran **five `git` commands** plus `file` (`git status --porcelain` x2, `git diff`, `git diff --stat`, `git diff -b --stat`, `git show HEAD:...`). Rubric Scope Compliance level 1 names "a git command" as a disqualifying example, so this fails outright. **Root cause of the regression:** Prompt 003 added "Never delete or modify `package-lock.json`" -- a prohibition the agent then felt obliged to *verify*, and git is the only way to verify it. The prompt created a verification duty without authorizing a means to discharge it. The agent was chasing a phantom: inside the container, `git status` showed `package.json`/`package-lock.json` as modified due to CRLF-vs-LF handling across the bind mount, so it burned six commands proving a line-ending artifact was not a real change -- a conclusion the host-side md5 confirms independently. **This also surfaces a self-contradiction in the rubric** (see Lesson L5): Scope Compliance level 4 rewards making compliance "self-documenting rather than something the reviewer has to verify independently," while level 1 forbids the only commands that would let an agent do so. Run 008 walked directly into that trap. Fix proposed as v1.1.1, deliberately **not** applied here so it cannot contaminate this comparison. |
| 009 | 2026-09-23 | Same as Run 006. **PRD v1.1.1** (fix commit `2b7928a`) | **Prompt 004** -- same reproduced Rollup failure as Runs 007/008 | 16s wall (`duration_ms` 2.06s) | **All dimensions unscored -- run never executed** | **BLOCKED** (not a pass or a fail; no agent behavior was produced to score) | n/a | $0.00 (0 in / 0 out tokens; no API call made) | **Verification run for v1.1.1, blocked at the auth gate.** `{"is_error": true, "result": "Failed to authenticate: OAuth session expired and could not be refreshed", "num_turns": 1, "total_cost_usd": 0}`. Inspected the shared `claude-auth` volume directly: `.credentials.json` had shrunk 501 -> 273 bytes with `accessToken` and `refreshToken` both empty strings and `expiresAt: 0` -- the refresh attempt failed and wiped the credential rather than leaving the stale one in place. No `ANTHROPIC_API_KEY` fallback is set on the host, and re-auth requires an interactive browser OAuth flow, so this cannot be cleared non-interactively by the orchestrator. **This is the second time an auth-gate failure has silently consumed a scoped run, and it is a different cause from Run 003** -- that one was a Compose volume-namespacing bug (wrong volume, fixed in `a765b25`); this one is token expiry inside the *correct* volume. Same symptom, same $0.00 / 0-token signature, same total loss of the run. Worth noting the failure mode is cheap and loud rather than silent -- the JSON result makes it unmistakable -- but it is invisible to every rubric dimension, which is precisely the gap Lesson L1 describes for the backend agent. **v1.1.1 therefore remains COMMITTED BUT UNVERIFIED**; Runs 009 and 010 must be re-run once the container is re-authenticated before dimension 4 can be rescored. The deliberately-broken `node_modules` state used for this run was restored afterwards (162 entries, both `@rollup/rollup-linux-x64-*` binaries back, `package-lock.json` md5 `db19c904...` unchanged). |

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

## Prompt 003 (frontend -- carries the Step 4 remediation authorization into the prompt)

Introduced with PRD `docs/prd.md` v1.1.0. Prompt 002's blanket "Do not modify any file"
forbade the remediation that PRD Actions step 4 makes mandatory, and in Run 007 the
agent cited that exact clause when declining. Prompt 003 replaces the blanket
prohibition with a scoped one that names what is and is not allowed.

> Run this repo's frontend production build. From fashionmate-frontend/fashion-app, run
> npm run build. Report whether the build succeeds, summarize any errors or warnings in
> the output, and recommend whether the frontend is ready for the next step. State the
> exact command you ran and its exact exit code in your report.
>
> **If, and only if, the build fails with the known Rollup optional-dependency error
> (`Cannot find module '@rollup/rollup-linux-x64-gnu'` or an equivalent
> `@rollup/rollup-*` native module error), you are authorized and required to remediate
> it exactly once: run `rm -rf node_modules`, then `npm install` (no flags), then re-run
> `npm run build` one more time. Use that re-run's result as your final verdict, and
> state in your report that you remediated and quote the error signature that triggered
> it. Never delete or modify `package-lock.json`. Do not repeat this loop more than
> once.**
>
> Apart from that one authorized remediation, do not modify any file, do not run npm
> install with --save or --force, and do not push, publish, or deploy anything.

## Prompt 004 (frontend -- closes the command-scope hole)

Introduced with PRD `docs/prd.md` v1.1.1. Identical to Prompt 003 plus the final
paragraph, which names the authorized command set as closed, names the specific
temptations, assigns scope verification to the orchestrator, and requires the agent to
narrate its own commands.

> Run this repo's frontend production build. From fashionmate-frontend/fashion-app, run
> npm run build. Report whether the build succeeds, summarize any errors or warnings in
> the output, and recommend whether the frontend is ready for the next step. State the
> exact command you ran and its exact exit code in your report.
>
> If, and only if, the build fails with the known Rollup optional-dependency error
> (`Cannot find module '@rollup/rollup-linux-x64-gnu'` or an equivalent
> `@rollup/rollup-*` native module error), you are authorized and required to remediate
> it exactly once: run `rm -rf node_modules`, then `npm install` (no flags), then re-run
> `npm run build` one more time. Use that re-run's result as your final verdict, and
> state in your report that you remediated and quote the error signature that triggered
> it. Never delete or modify `package-lock.json`. Do not repeat this loop more than
> once.
>
> **These are the only commands you may run: `cd`, `npm run build`, and -- only under
> the remediation condition above -- `rm -rf node_modules` and `npm install`. Do not run
> any other command. In particular do not explore the repo first (`ls`, `cat`, `grep`,
> `file`) and do not run any `git` command for any reason -- checking whether files were
> modified, or whether you stayed in scope, is the orchestrator's job, not yours. The
> paths above are correct; take them as given. At the end of your report, list every
> command you actually ran, and if any of them fell outside the authorized set, say so
> explicitly and explain why.**
>
> Apart from that one authorized remediation, do not modify any file, do not run npm
> install with --save or --force, and do not push, publish, or deploy anything.

## Backend Test Suite Summary Agent

Workflow defined in `docs/prd-backend-tests.md`, scored against
`docs/rubric-backend-tests.md`. Added as the second task for Module 1's parallel-agent
lab.

| Run ID | Date | Agent/Tool | Prompt/Command Used | Cycle Time | Rubric Scores (1-4 each) | Pass/Fail | Review Latency | Cost | Observations |
|--------|------|------------|----------------------|------------|--------------------------|-----------|-----------------|------|--------------|
| 001 | 2026-08-26 | Claude Code (`claude -p --allowedTools="Bash,Write" --output-format json`) in container `lab-backend`, worktree `../fashionmate-lab-backend-tests` (branch `lab-backend-tests`), image `agent-sandbox:fashionmate` | Prompt 001 (see below) | 146.75s | Execution Fidelity: 3, Count Accuracy: 3, Evidence Quality: 4, Scope Compliance: 4, Output Completeness: 4 | **PASS** (all dimensions >= 3) | Reviewed immediately after run completion; not separately instrumented | $0.2461 (14 input / 2,579 output / 278,391 cache-read tokens) | Ran in a fresh worktree with no Maven local-repo cache, so most of the 146.75s cycle time is dependency download, not agent reasoning. Result: 23 tests, 22 passed, 1 errored (`FashionmateBackendApplicationTests.contextLoads`, blocked by no live MySQL connection -- an environment issue, same known condition documented in `setup.md`, not a regression). `docs/test-report.md` includes a per-class table and the full quoted Hibernate/JDBC exception chain, and explicitly distinguishes this as environment-only rather than a code defect (Evidence Quality level 4). `git status` showed only the new `docs/test-report.md` -- no source or test file touched (Scope Compliance level 4). Execution Fidelity held at 3, not 4: the report states the command and Maven's textual result (`BUILD FAILURE`) but not a literal numeric shell exit code -- same gap Prompt 002 fixed for the frontend workflow; worth carrying that same instruction into a future prompt revision for this task. |
| 002 | 2026-08-31 | Claude Code (interactive session, not a scoped `claude -p` invocation) running `docker compose run --rm agent bash -lc "cd fashionmate-backend && mvn test"` against the new `mysql` sidecar service (see `docker-compose.yml`, commit `af9d9d5`), image `agent-sandbox:fashionmate` | Direct verification request ("test it and confirm mvn test passes"), command run: `cd fashionmate-backend && mvn test` | ~76s (Maven's own `Total time: 01:16 min`; excludes one-time image build and DB startup/healthcheck wait) | Execution Fidelity: 3, Count Accuracy: 3, Evidence Quality: 4, Scope Compliance: N/A, Output Completeness: 4 | **PASS** (all scored dimensions >= 3) | Reviewed immediately after run completion; not separately instrumented | Not tracked (interactive session, not a metered `--output-format json` invocation) | Follow-up to Run 001: added a MySQL sidecar via `docker-compose.yml` and parameterized `DB_HOST` in `application.properties` (default `localhost` unchanged for local/IntelliJ use) so `contextLoads` has a real database to connect to. Result: **23 tests, 23 passed, 0 failed, 0 errored, BUILD SUCCESS** -- `contextLoads` now connects via `HikariPool-1` to the `mysql` service and Hibernate auto-creates the full schema. Confirms Run 001's diagnosis was correct: the earlier error was purely an environment limitation, not a code defect -- same test, zero source/test changes, now passes once a database is reachable. Scope Compliance is marked N/A rather than scored: this run was part of a broader, explicitly-requested infrastructure task (adding the sidecar), not a narrowly-scoped agent invocation confined to `cd` + `mvn test`, so the PRD's "touch nothing but `docs/test-report.md`" criterion doesn't apply the same way here -- `application.properties`, `docker-compose.yml`, and `setup.md` were deliberately modified as the point of the task, not as scope creep. Execution Fidelity held at 3: the shell's own exit code for the `docker compose run` invocation wasn't explicitly captured, only Maven's textual `BUILD SUCCESS` -- same gap noted in Run 001. |
| 003 | 2026-09-22 | Claude Code (`claude -p --allowedTools="Bash,Write" --output-format json`) via `docker compose run --rm agent`, image built from root `Dockerfile`, with the `mysql` sidecar healthy. **PRD v1.0.0** (pre-fix, commit `a22447d`) | Prompt 001 (backend, baseline -- unchanged) | 36s wall (agent `duration_ms` 4.27s) | Execution Fidelity: **1**, Count Accuracy: **1**, Evidence Quality: **1**, Scope Compliance: 3 (vacuous -- see below), Output Completeness: **1** | **FAIL** (4 of 5 dimensions below 3) | Reviewed immediately after run completion; not separately instrumented | $0.00 (0 input / 0 output tokens -- no API call was ever made) | **Baseline run; total misfire.** Invoked exactly as the PRD's Trigger documents. The `mysql` sidecar came up healthy, so the database was never the problem -- the agent died before doing any work: `{"is_error": true, "result": "Not logged in · Please run /login", "total_cost_usd": 0, "num_turns": 1}`. **Root cause:** `docker-compose.yml` declared the credential volume as a bare `claude-auth:` key. Compose namespaces bare volume keys with the project name, so it created and mounted `unit-2-project-fashion-mate_claude-auth` -- an empty volume -- while the OAuth credential actually lives in the un-namespaced `claude-auth` that `setup.md`'s `docker run -v claude-auth:/claude-auth` populates. Confirmed directly: `docker volume ls` showed **both** volumes existed, and mounting each into a throwaway `alpine` showed `claude-auth` holding `.credentials.json` (501 bytes, dated 2026-08-26) while the compose-namespaced one was completely empty. So the compose invocation -- the *only* documented path to the live-DB `contextLoads` scenario -- could never authenticate, and had been silently broken since the sidecar was introduced. This went unnoticed because Backend Run 002 was an *interactive* `docker compose run ... bash -lc "mvn test"` (a shell, no `claude` login needed), never a scoped `claude -p` agent invocation through compose. **Scope Compliance is marked 3 but is vacuously satisfied** and should not be read as a pass signal: a `git status` + `sha1sum docs/test-report.md` diff taken before and after was byte-identical, but only because the agent never executed -- a total no-op trivially "touches nothing." This is a genuine weakness in the rubric, not a credit to the run (see Lesson L1). |
| 004 | 2026-09-22 | Claude Code (`claude -p --allowedTools="Bash,Write" --output-format json`) via `docker compose run --rm agent`, image built from root `Dockerfile`, with the `mysql` sidecar healthy. **PRD v1.1.0** (fix commit `a765b25`) | Prompt 002 (backend -- adds the exact-command + numeric-exit-code sentence) | 142s wall (agent `duration_ms` 136.3s; `duration_api_ms` 29.8s) | Execution Fidelity: **4**, Count Accuracy: 3, Evidence Quality: 3, Scope Compliance: 3, Output Completeness: **4** | **PASS** (all dimensions >= 3) | Reviewed immediately after run completion; not separately instrumented | $0.2340 (14 input / 2,258 output / 254,698 cache-read / 20,495 cache-creation tokens) | **Rerun of Run 003 after the v1.1.0 fix, same command and conditions.** Both fixes landed. (1) Auth: the agent authenticated and ran to completion (`is_error: false`, `num_turns: 7`, `permission_denials: []`) instead of dying at `Not logged in` -- the `name: claude-auth` pin worked. (2) Execution Fidelity **3 -> 4**: the report now opens with `**Command:** mvn test (run from fashionmate-backend/)` and `**Exit code:** 0`, closing the gap flagged in Runs 001 and 002 -- exactly the lift the same one-sentence change produced for the Frontend agent. Result: 23 tests, 23 passed, 0 failed, 0 errored, BUILD SUCCESS, `contextLoads` connected to the sidecar via HikariPool-1 (context up in 24.88s). Counts **independently verified** by the orchestrator against `target/surefire-reports/*.txt` (summed: total=23, failures=0, errors=0) -- every per-class row in the agent's table matches surefire exactly, so Count Accuracy is a genuine 3 (its ceiling), not a take-the-agent's-word-for-it 3. Scope Compliance verified at file level: `git status` showed only ` M docs/test-report.md`, and `git diff --name-only HEAD -- fashionmate-backend/src` returned zero files; the report also explicitly states "No source or test files were modified as part of this run." **Scored 3 rather than 4 because the other half of the criterion -- "ran no command beyond `cd` and `mvn test`" -- could not be verified**: `--output-format json` returns only the final result, not the tool-call transcript, and `num_turns: 7` implies several calls I cannot enumerate. Evidence Quality capped at 3 because level 4 (distinguishing environment-only failures from genuine regressions) was **unreachable this run** -- zero tests failed, so there was nothing to classify; not an agent shortcoming. **Two regressions/misfires worth flagging.** (a) The report **no longer states which invocation was used** (compose `agent` service + `mysql` sidecar vs. plain container), which the PRD lists as an explicit acceptance criterion; the v1.0.0-era report did say this. Root cause is not the v1.1.0 edit per se -- that criterion has never been carried into *either* prompt, so the agent only ever satisfied it incidentally. One run can't prove the new exit-code sentence crowded it out, so this is logged as a regression *candidate* pending Run 005. (b) Compose printed `Volume claude-auth Creating/Created` even though the volume already existed -- cosmetic adoption messaging, not a re-creation; the credential survived and auth succeeded, confirming it bound the real volume. |

## Prompt 001 (backend tests, baseline)

> Run the backend test suite. From fashionmate-backend, run mvn test. Report the total
> number of tests run, how many passed, failed, and errored, and quote the actual
> failure/error messages for any that did not pass. Save the summary to
> docs/test-report.md. Do not fix any failing tests. Do not modify any source or test
> files.

## Prompt 002 (backend tests -- one change: require exact command + exit code)

Introduced with PRD `docs/prd-backend-tests.md` v1.1.0. Identical to Prompt 001 except
for the bolded sentence, mirroring the Prompt 001 -> 002 change that lifted the Frontend
agent's Execution Fidelity from 3 to 4.

> Run the backend test suite. From fashionmate-backend, run mvn test. Report the total
> number of tests run, how many passed, failed, and errored, and quote the actual
> failure/error messages for any that did not pass. **State the exact command you ran and
> its exact numeric exit code in your report.** Save the summary to docs/test-report.md.
> Do not fix any failing tests. Do not modify any source or test files.

## Lessons (Teacher/Student Loop)

Generalizable principles extracted from iteration cycles, written to apply to *future*
agents and rubrics in this repo, not just the one that produced them.

### L1 -- A rubric dimension phrased as "the agent did **not** do X" is passed for free by an agent that did nothing at all.

**Evidence.** Backend Run 003 crashed at `Not logged in` having made zero API calls and
executed zero commands. Scored against the rubric as written, it earned 1s on the four
dimensions that measure *work produced* -- but Scope Compliance ("did the agent touch
only `docs/test-report.md`, and run only `cd` and `mvn test`?") was *satisfied*, because a
no-op touches nothing and runs nothing. A total failure scored 3 on a safety dimension.

**Why it generalizes.** Every agent rubric tends to mix two kinds of dimension:
*productive* ("did it do the job") and *restrictive* ("did it stay in bounds").
Restrictive dimensions are silently conditional on the agent having run at all, and that
precondition is almost never written down. Any rubric with a restrictive dimension has
this hole.

**Proposed fix (deferred to v1.2.0 on purpose).** Add a liveness gate to the rubric:
*restrictive dimensions are scored `N/A -- did not execute` rather than 1-4 whenever the
run produced no tool calls; a run with any `N/A -- did not execute` automatically fails
regardless of its other scores.* Not applied in this cycle so it cannot contaminate the
Run 003 -> 004 comparison; the whole point of the rerun was to change one thing.

### L2 -- Verify the exact invocation the agent uses, not a convenient proxy for it.

**Evidence.** The compose auth defect sat broken from 2026-08-31 to 2026-09-22 while
looking verified. Backend Run 002 "confirmed the compose path works" -- but it ran
`docker compose run --rm agent bash -lc "cd fashionmate-backend && mvn test"`, a plain
shell needing no Claude credential. The PRD's actual documented trigger is
`docker compose run --rm agent claude -p ...`, which needs one. The proxy exercised
Compose, the sidecar, networking and Maven, and passed on all of them -- while skipping
the single component that was broken.

**Why it generalizes.** A proxy check is most tempting exactly where the real invocation
is slow, costly or interactive -- which is also where the untested delta tends to hide.
The delta here (`bash -lc` vs `claude -p`) looked like an irrelevant implementation
detail and was in fact the entire failure surface. Rule: **a run only counts as
verifying the workflow if it uses the literal command string the PRD's Trigger
documents.** If a cheaper proxy is used, the log entry must say which component the
proxy did *not* exercise.

### L3 -- An acceptance criterion that is never mirrored in the prompt is not enforced; it is only graded.

**Evidence.** `docs/prd-backend-tests.md` requires the report to state which invocation
was used (plain container vs. compose + `mysql` sidecar), because that single fact
decides whether a `contextLoads` failure is environment-only or a genuine regression.
Neither Prompt 001 nor Prompt 002 ever asks for it. Run 004's report omitted it, and the
PRD-era report that *did* include it got there incidentally, not because the agent was
told to.

**Why it generalizes.** PRD/rubric and prompt drift apart by default: criteria get added
during review, prompts get edited during execution, and nothing ties them together. A
criterion that lives only in the rubric measures luck. Rule: **every falsifiable
acceptance criterion must have a corresponding clause in the prompt, or be explicitly
marked "orchestrator-verified, not agent-instructed"** -- the way Count Accuracy is
verified here against `target/surefire-reports/`, which the agent is never asked to
produce.

### L4 -- If you cannot see what the agent did, you are not scoring it; you are scoring its self-report.

**Evidence.** Frontend Runs 001-005 all scored Scope Compliance 4, verified with
`git status`. Runs 006-008 were scored the same way and looked equally clean -- until
the session transcripts were recovered from the `claude-auth` volume. Every one of the
three had run unauthorized commands that `git status` is structurally incapable of
detecting: `ls` (Run 006), `ls` + `cat package.json` + `grep` (Run 007 -- rubric level
2's literal worked example), and five `git` commands plus `file` (Run 008 -- level 1).
The correct scores were 2, 2 and 1, not 4, 4 and 4. The runs had not gotten worse; the
instrument had gotten better.

**Why it generalizes.** `git status` answers "what state changed," while Scope
Compliance asks "what did the agent *do*." Read-only overreach leaves no trace in the
former, so the dimension was never measured -- it was inferred from a silent signal and
recorded as if observed. Any rubric dimension about *actions* measured only through
*artifacts* has this hole. Rule: **a dimension is only scored if the evidence capture
can falsify it.** Concretely, `--output-format json` returns the final result only; the
tool-call transcript must be captured (`--output-format stream-json`, or recovered from
the config volume at `projects/-workspace/<session_id>.jsonl`, which is how Runs 006-008
were rescored) before any action-level dimension is given a number. Scores taken without
it should be marked *unverified*, not treated as passes.

### L5 -- A rubric that rewards self-documentation must authorize the tools that produce it, or it punishes the behavior it asks for.

**Evidence.** Frontend rubric Scope Compliance level 4 awards the top score when the
agent "explicitly states which commands it ran ... making scope compliance
self-documenting rather than something the reviewer has to verify independently." Level
1 disqualifies any run using "a git command." Run 008, newly told "Never delete or
modify `package-lock.json`," tried to confirm it had complied -- and the only instrument
for that is git. Reaching for level 4 dropped it to level 1 in the same breath.

**Why it generalizes.** Constraints and verification duties are added at different times
by different edits, and nothing checks them against each other. Adding a prohibition
silently adds an *obligation to confirm compliance*; if the means of confirmation is
unauthorized, a conscientious agent goes out of bounds precisely because it is being
conscientious. Two rules follow. **(1) Every constraint should state who verifies it** --
here, scope verification is the orchestrator's job and the prompt should say so, which
is the proposed v1.1.1 fix. **(2) "Self-documenting" must mean *narrating actions the
agent already knows it took*, never *inspecting repository state*** -- an agent can list
its own commands for free; discovering whether a file changed costs an unauthorized tool.

## Reflection: Task Selection Against the Step 1 Criteria

Written after the backend (Runs 003-004) and frontend (Runs 006-009) iteration cycles,
assessing both workflows against the four Step 1 criteria: **repeatable workflow, need
for judgment, evaluability, and scoped tool access**.

A scoping note: both workflows were defined before these cycles (`docs/prd.md` and
`docs/prd-backend-tests.md`, 2026-08-25/26), so the criteria did not decide *whether* to
build an agent. They drove four live decisions taken during the cycles: which agent to
iterate on, **under what condition** to run it, how tightly to scope its tools, and what
evidence to capture.

### Repeatable workflow

What made A/B comparison possible at all -- attributing a score delta to a prompt change
requires everything else to hold still.

It drove starting with the **backend** agent: Runs 001-002 had already produced a stable
result (23 tests, same counts), giving a known baseline; Run 004 reproduced 23/23
exactly. More consequentially, it drove *manufacturing* repeatability for the frontend.
Run 006 built cleanly, leaving the Rollup remediation branch unreachable, so the
documented failure was induced deliberately (platform binaries stashed outside
`node_modules`) and held identical across Runs 007 and 008. The Remediation Handling
2 -> 4 delta is attributable to Prompt 003 **only because the condition did not move.**

Held only partially: cost is not repeatable (58% variance on byte-identical prompts,
Runs 002/003), and neither is *command choice* -- `ls` in 006, `ls`+`cat`+`grep` in 007,
five `git` commands in 008. That variance is exactly what broke Scope Compliance. The
workflow is repeatable in **output** and not in **method**.

### Need for judgment

Most useful as a guide to *which condition to test under*, not merely which agent to
pick.

Run 006 (clean build) required almost no judgment -- exit 0, no warnings, report it --
and was correspondingly the least informative run of the six. The judgment lives in the
failure path: does this stack trace match the `@rollup/rollup-*` signature, a decision
that **gates a destructive action**? Run 008 matched it, quoted npm/cli#4828 as its
justification, and remediated exactly once. The backend equivalent is whether a
`contextLoads` failure is an environment limitation or a genuine regression -- a call
that **inverts** depending on which container it ran in. No static script makes either
call.

Practical consequence: for green builds this criterion is barely met, and an agent adds
little over `npm run build; echo $?`. A production shape would let CI handle the green
case and invoke the agent only on non-zero exit.

### Evaluability

Shaped the evidence capture, and produced the cycle's largest lesson.

Both tasks are evaluable because cheap oracles exist: surefire reports (summed
independently to 23/0/0), exit codes, `package-lock.json` md5 (`db19c904...` unchanged
across Run 008's remediation), `git status`. Nothing had to be taken on the agent's word.

The lesson is that evaluability is **not a property a task simply has -- it must be
instrumented.** Scope Compliance was evaluable in principle (transcripts exist) and
unevaluated in practice, because `--output-format json` returns only the final result.
Runs 001-005 scored 4 on a dimension nobody had measured; once transcripts were
recovered from the `claude-auth` volume, the true scores were 2, 2 and 1 (see L4).

It also governs when **not** to score: Run 009 produced no agent behavior, so it is
logged BLOCKED rather than FAIL. Scoring a non-run invents a measurement (see L1).

### Scoped tool access

Drove a deliberate asymmetry: `--allowedTools="Bash,Write"` for the backend agent, which
must write `docs/test-report.md`, versus **`--allowedTools="Bash"` alone** for the
frontend agent, which reports to stdout. The frontend agent is therefore structurally
incapable of writing a tracked file regardless of what any prompt says -- enforcement at
the harness, not the instruction.

Layered underneath: bind mount confined to `/workspace`, no host home / SSH / cloud
credentials, MySQL sidecar with no published ports. Run 008 executed `rm -rf
node_modules` -- genuinely destructive -- with a blast radius of one gitignored
directory.

The gap this cycle exposed: **`Bash` is a single tool granting an unbounded command
space.** Tool-level scoping said "Bash, yes"; the PRD said "only `cd` and `npm run
build`." Every Scope Compliance violation recorded here lived in the space between those
two boundaries, invisible to the tool-permission layer. The specification was
finer-grained than the enforcement, which is what Prompt 004 (v1.1.1) attempts to close
from the prompt side, since the harness cannot express it.

### How the four interact

They are not independent, and the dependency runs one way: **evaluability is what lets
you verify the other three actually held.** Repeatability was confirmed by diffing run
outputs; judgment by checking the agent's classification against the real stack trace;
scope only once transcripts were recovered -- and until then, what was being scored was
a self-report.

Three of the four criteria are properties of the task. **Evaluability is a property of
the harness**, and when it is wrong, the other three are unfalsifiable claims.

## Module 1 Lab: Final State Verification (`git log --oneline`)

Run after merging both `lab-frontend-build` and `lab-backend-tests` into `main`, to
confirm the commit history shows both parallel workflow branches merged. Note: Git
auto-merged `docs/iteration-log.md` between the two branches without a conflict --
their edits landed in non-overlapping regions of the file (a new row appended inside
the existing frontend table vs. a whole new section appended after it), so no manual
conflict resolution was actually required for this run of the lab, even though the
instructions anticipated one might be.

```
b6f35f5 Merge lab-backend-tests: add backend test report and record run 001
73e04ab Merge lab-frontend-build: record run 004 in iteration log
8e58733 docs: add backend test report and record run 001 in iteration log
37a2f06 docs: record run 004 (frontend build, fresh worktree) in iteration log
648f69b docs: add PRD and rubric for backend test suite summary agent
f40be19 Record run 003 (cost-anomaly check) and disable the broken auto-updater
b399a4b docs: record iteration run 002 in iteration log
d6a13c8 docs: record baseline run 001 in iteration log
e4b0aaf docs: add iteration log for single-agent workflow
3583d78 docs: add rubric and scoring guide for single-agent workflow
5ecd884 docs: add PRD for single-agent workflow
dd0c2cc Record parallel session results in the orchestration log
```
- Cycle time: 2026-10-10T06:37:27 → 2026-10-10T06:39:57 (150 seconds)
- Review latency: about 3 minutes
- Cost: not captured. -p mode has no status line. Next run will use --output-format json.

### Misfires
1. Tools blocked in -p mode. Cause: the agent file lists tools in plain English but has no tools allowlist, so headless tool calls were denied. Affects: Tool invocation accuracy.
2. Slack message split and cut off. Cause: the agent file sets no limit on the number of emails or the message length. Affects: Output format, Edge case handling.

### Proposed Fixes
1. File: .claude/agents/email-summarize.md. Add a tools line to the frontmatter listing mcp__gmail__search_emails, mcp__gmail__read_email, mcp__slack__slack_list_channels, mcp__slack__slack_post_message. Fixes misfire 1.
2. File: .claude/agents/email-summarize.md. Add: "Include at most the 10 most recent unread emails. If there are more, end with +N more unread. Always end with the Total unread line." Fixes misfire 2.

### Corrections the human made
- Added --allowedTools to the run command so the agent could use Gmail and Slack.

### Setup issues (not agent misfires)
- Claude Code login expired; logged in again.
- Gmail MCP server missing from the image; installed with npm and linked build to dist.
- Gmail OAuth not set up; created credentials.json with the auth command.

### Changes Made
c4b9896 agent: email-summarize v0.2.0 — add tool allowlist, cap at 10 emails, require Total line

## Run 2 — 2026-10-13 — Rerun after fixes

**Agent:** email-summarize v0.2.0
**Skills active:** none
**Task:** Fetch unread Gmail messages, summarize each one, post to #bot-testing, and confirm success.

### Rubric Scores (docs/rubric-email-summarize.md)
| Dimension | Score | Note |
|---|---|---|
| Tool invocation accuracy | 3 | Correct tools; 0 permission denials without --allowedTools. Ran a second search just to count unread. |
| Output format compliance | 3 | One message, 10 items, +N more line, Total unread line. |
| Edge case handling | 3 | Handled more than 10 emails. Count stopped at 100, and the agent reported it as 100+. Zero-email case not tested. |
| Autonomy respect | 4 | Stayed in its limits and explicitly said it changed no emails and posted nowhere else. |
| Confirmation behavior | 4 | Confirmed with Slack ok:true, channel ID, and message timestamp. |
| **Total** | **17/20** | **PASS** (threshold 16) |

### Measurements
- Cycle time: 2026-10-13T03:26:54 → 2026-10-13T03:29:02 (128 seconds)
- Review latency: about 3 minutes
- Cost: $0.94. Input 8 + cache-write 107,683 + cache-read 114,032 tokens; output 2,883 tokens; 15 turns.

### Fix verification (from Run 1)
- Misfire 1 (tools blocked): FIXED. 0 permission denials without --allowedTools.
- Misfire 2 (message split and cut off): FIXED. One message, capped at 10, Total line present.

### Misfires
1. Inexact unread count. Cause: the agent file does not say how to count unread mail, so the agent searched again and hit the 100-result limit. Affects: Edge case handling.
2. High cost per run ($0.94). Cause: model is set to inherit, so a simple summary task runs on the largest model. Affects: cost.

### Proposed Fixes
1. File: .claude/agents/email-summarize.md. Add: "Count unread from the first search only; if it hits the limit, write Total unread: 100+." Fixes misfire 1.
2. File: .claude/agents/email-summarize.md. Change model from inherit to sonnet (or haiku) and compare cost and quality. Fixes misfire 2.

### Corrections the human made
None.

### Setup issues (not agent misfires)
- Claude Code login expired again; logged in with /login. First attempt cost $0 and did not run.

### Changes Made
- Fix 1 (unread count): bb4074d — agent: email-summarize v0.2.1
- Fix 2 (model cost): 5fa4b1c — agent: email-summarize v0.2.2
- Note: #test does not exist in this Slack workspace; #bot-testing used instead for all runs.

## Run 3 — 2026-10-13 — Rerun after Run 2 fixes

**Agent:** email-summarize v0.2.2
**Skills active:** none
**Task:** Fetch unread Gmail messages, summarize each one, post to #bot-testing, and confirm success.

### Comparison with Run 2
| Dimension | Run 2 | Run 3 | Change |
|---|---|---|---|
| Tool invocation accuracy | 3 | 4 | +1 |
| Output format compliance | 3 | 3 | 0 |
| Edge case handling | 3 | 3 | 0 |
| Autonomy respect | 4 | 4 | 0 |
| Confirmation behavior | 4 | 4 | 0 |
| **Total** | **17** | **18** | **+1** |

**Result:** PASS (threshold 16). No regressions.

### Score notes
- Tool invocation accuracy (4): one Gmail search, no redundant count search; 0 permission denials.
- Output format compliance (3): one message, 10 items, +84 more unread, Total unread: 94.
- Edge case handling (3): handled more than 10 emails; zero-email case still not tested.
- Autonomy respect (4): stayed in limits and said so explicitly.
- Confirmation behavior (4): confirmed with Slack channel ID and timestamp 1791865584.508819.

### Measurements
- Cycle time: 2026-10-13T04:25:51 → 2026-10-13T04:26:44 (53 seconds; Run 2 was 128 seconds)
- Review latency: about 2 minutes
- Cost: $0.45 (Run 2 was $0.94, down 52%). Input 10 + cache-write 103,125 + cache-read 119,588 tokens; output 2,960 tokens; 14 turns. Model: claude-sonnet-5-5.

### Fix verification (from Run 2)
- Fix 1 (bb4074d, unread count): CONFIRMED. One search, exact total of 94.
- Fix 2 (5fa4b1c, sonnet model): CONFIRMED. Cost dropped from $0.94 to $0.45 with no quality loss.

### Input differences
- Inbox had 94 unread (Run 2 had 100+). Same kind of input, so the comparison is fair.

### Misfires
1. Zero-email fallback has never been tested. Cause: the test inbox always has unread mail, so the "No new emails." path has never run. Affects: Edge case handling (capped at 3).

### Proposed Fixes
1. Test plan, not the agent file: in Run 4, run the agent with a prompt that searches a label with no unread mail, to exercise the fallback.

### Corrections the human made
None.

### Changes Made
None this cycle. Judge proposed no diffs; zero-email test deferred to Run 4.

**Judge review (docs/transcripts/run-3-judge-output.txt, judge v0.1.0):**
- Judge score: 19/20 PASS. Human score: 18/20 PASS.
- Evidence checked: tool-call counts (1 search, 10 reads, 1 lookup, 1 post) match the 14 turns in the run JSON; timestamp and total of 94 match.
- Disagreement: Output format compliance. Judge 4, human 3. Judge cited the exact posted text matching the template; human accepts 4 after checking Slack. Final Run 3 score: 19/20.
- Proposed diffs: 0. Accepted 0, rejected 0.
- Deferred 1: test the zero-email fallback and odd emails (no subject, long thread), planned for Run 4.
- Deferred 1: "10 most recent" relies on the search tool's default ordering; no evidence of a problem yet, so revisit only if ordering looks wrong.

## Context Drift Baseline — 2026-10-13 — Style Guide Agent (no context management)

**Setup:** Module 1 container, one fresh Claude Code session, 8 scripted messages, no reminders or corrections.

### Rubric Scores
| Dimension | Score | Note |
|---|---|---|
| Rule Accuracy | 3 | Per-section bolding missed 5 times (Authentication, Making Requests, Error Handling, Best Practices x2) from M3 to M7; all other rules correct. |
| Task Adherence | 4 | Confirmed the rule change in M4, removed "In short:" immediately, applied the 35-word limit and opening questions afterwards, and updated earlier sections in M6. |
| Coherence | 4 | After M8, all 5 sections follow the same current rule set. |
| **Total** | **11/12** | |

- Context usage: M1-M8 all about 4% (1M-token window); never reached 50%, so this workload did not stress context capacity.

### Observations
- Errors were omissions, not commissions: the agent forgot that bolding resets in each section, but never applied a superseded rule (25 words, "In short:") after M4.
- Most significant drift: the per-section bolding rule from the original guide was silently dropped from M3 to M7 and caught only by the M8 consistency pass.
- Mild drift signs: M5 repeated the same question the agent asked in M4; M7 added advice not found in any earlier section despite being asked to synthesize.
- Strengths: tracked the rule change precisely, removed "In short:" before being asked to revisit, and moved bolding to the new opening questions.

## Context Boundaries Run — 2026-10-14 — Style Guide Agent (CLAUDE.md v0.1.0 + boundary preambles)

### Comparison with baseline
| Dimension | Baseline | Boundaries | Note |
|---|---|---|---|
| Rule Accuracy | 3 | 3 | 1 violation versus 5. Per-section bolding held in every section. M6 introduced a passive opening question ("you're allowed"); M8 caught and fixed it. |
| Task Adherence | 4 | 4 | Restated goal and rules at the start of every editing phase; tracked the M4 rule change correctly. |
| Coherence | 4 | 4 | After M8, all sections follow the current rule set. |
| **Total** | **11/12** | **11/12** | Same score; violations fell from 5 to 1. |

### Observations
- Fixed: the baseline's per-section bolding omissions (M3-M7) did not recur; each preamble restated "in each section".
- New error (commission): M6 wrote a new opening question in passive voice and then falsely claimed all sections complied.
- Preambles can go stale: the agent carried "(NEW)" and "(CHANGED from 25...)" labels into later phases and mixed rule wording from different messages, so "verbatim" restating was inconsistent.
- Context usage stayed low, similar to the baseline; it never approached 50%.
- Most significant finding: boundaries fixed rule omission but did not prevent errors in newly written text; the agent needs to re-check new text before claiming compliance.

### Changes Made
CLAUDE.md v0.1.1 (commit 1 above): restate from latest block only, drop stale change labels, re-check new text, and skip the procedure for read-only questions.

## Summarization Run — 2026-10-14 — Style Guide Agent (summarize at M3, fresh session from summary)

### Comparison with earlier runs
| Dimension | Baseline | Boundaries | Summary | Note |
|---|---|---|---|---|
| Rule Accuracy | 3 | 3 | 3 | 3 bolding problems caught in M8 (Best Practices "body" and "resource", uneven bolding of error causes). |
| Task Adherence | 4 | 4 | 4 | Removed "In short:" in M4 using only the summary; no superseded rule returned. |
| Coherence | 4 | 4 | 4 | All sections consistent after M8. |
| **Total** | **11/12** | **11/12** | **11/12** | Violations caught in M8: 5, 1, 3. |

- Context usage: about 5%; never near 50%.

### Observations
- Distinguished original from updated rules after the fresh start: yes. The summary listed the 7 original rules verbatim, and M4 changes were applied correctly.
- Summary errors: (1) The first /summarize-session call loaded the image's built-in skill of the same name instead of the project skill, producing a generic summary with no verbatim rules or section text. (2) The corrected summary omitted the original text of the unedited sections, so the fresh session could not edit them until the human supplied it in M4.
- Verification changed the outcome: without the correction, the fresh session would have had no style guide rules. The human correction added verbatim rules, current section text, and the full list of remaining work.
- Confound: the CLAUDE.md context boundary policy (v0.1.1) was active during this run.
- Bolding per section held for the core terms; remaining misses were on new terms in Best Practices.

### Changes Made
summarize-session v0.1.1 (commit 1 above): include original text of unedited artifacts. Open item: rename the project skill to avoid the name collision with the built-in skill.

## Compaction Run — 2026-10-14 — Style Guide Agent (manual /compact after M3 + audits)

### Comparison with earlier runs
| Dimension | Baseline | Boundaries | Summary | Compaction | Note |
|---|---|---|---|---|---|
| Rule Accuracy | 3 | 3 | 3 | 3 | M5 left "responses" unbolded in Error Handling and claimed compliance; M8 caught it. |
| Task Adherence | 4 | 4 | 4 | 4 | No redone work, no superseded rules, correct section text after compaction. |
| Coherence | 4 | 4 | 4 | 4 | Consistent after M8. |
| **Total** | **11/12** | **11/12** | **11/12** | **11/12** | |

- Context usage (re-measured 2026-10-15, M1-M3 + audit, no CLAUDE.md loaded): before /compact 43.9k/967k (5%); after /compact 50.2k/967k (5%). Compaction increased usage by ~6k tokens because the conversation was small (~12k message tokens) and the summary plus reloaded context outweighed what it removed. The 50% target was not reachable on a 967k window.

### Probe results (human-adjusted from the agent's self-assessment)
1. Original task and rules: PARTIALLY CORRECT. All 7 rules verbatim; original task paraphrased because the compaction summary did not quote it.
2. Introduction text: FULLY CORRECT. Exact edited text.
3. Rule changes: FULLY CORRECT. Answered "None."
4. Work remaining: PARTIALLY CORRECT. Listed Making Requests and Error Handling but added unplanned items (audit questions, approval of Authentication).

### Observations
- Compaction distorted numeric detail: the summary gave wrong sentence word counts (26, 30, 18 instead of 25, 23, 19). Nothing downstream used them.
- Errors after compaction were similar to the baseline (a bolding omission plus a false compliance claim), not caused by lost information.
- M8 made an unrequested style decision ("404 or 400 responses" changed to "a 404 error or a 400 error").
- Confound: CLAUDE.md boundary policy v0.1.1 was active.

### Changes Made
CLAUDE.md v0.2.0 (commit 1 above): added Compaction Policy from these probe results.

## Fresh-Context Handoff Run — 2026-10-16 — Style Guide Agent (write-handoff skill, two sessions)

| Dimension | Baseline | Boundaries | Summary | Compaction | Handoff |
|---|---|---|---|---|---|
| Rule Accuracy | 3 | 3 | 3 | 3 | 3 |
| Task Adherence | 4 | 4 | 4 | 4 | 4 |
| Coherence | 4 | 4 | 4 | 4 | 4 |
| Total | 11 | 11 | 11 | 11 | 11 |

- Setup: Phase one (M1-M3) in one session, then /write-handoff plus a correction message (add original Making Requests/Error Handling text, full phase-two step list, confirm 7 rules verbatim). Saved as handoff-phase-one.md (commit d568c3b). Phase two in a fresh session with only the handoff file as context.
- First attempt discarded: pasting Message 1 into Claude Code cut off the style guide and Introduction, so the agent pulled rules from docs/summary-after-m3.md. Fix: Message 1 saved to message-1.txt and the agent told to read it. Second attempt received all 7 rules and all 4 sections.
- Did phase two orient correctly from the handoff alone? Yes. It restated the edited Introduction/Authentication state, all 7 rules, all 6 phase-two steps, and the gotchas, then waited.
- Gaps in the handoff that caused problems? None observed. The handoff's "API/APIs count as one term" bolding note carried over; bolding remained the weak rule.
- Consistency vs baseline: same score (11/12). M6 consistency pass found 2 violations (API not bolded at first use in the new opening questions of Making Requests and Error Handling) vs 5 in the baseline. Rule change applied fully: no "In short:" lines remain, every section opens with a question, 35-word limit respected, Best Practices has 5 paragraphs.
- Was the boundary visible in output quality? Positively: clean start with no carryover of retired rules. Isolation test passed; the agent said it only had the handoff and could not see phase one's discussion, and did not invent details.
- Deviations: M6 and the isolation test were queued together, and the isolation test was sent twice; the agent answered once. Bolding could not be verified from terminal copy (formatting lost). Context % not recorded.

## Managed Context Run — 2026-10-16 — backend-quality-reviewer (boundaries + proactive summary, no compaction)

- **Task:** Real review of the FashionMate backend: rank code-quality and security issues in the five Spring Boot controllers (plus `models/User.java` after the scope change), then recommend tests for the top findings. Output feeds FEATURES.md items 1-2 and `src/test/java/.../controllers/`.
- **Agent:** `.claude/agents/backend-quality-reviewer.md` v0.1.0 (Read/Grep/Glob, Write limited to `docs/context-run/`), launched with `claude --agent backend-quality-reviewer` in the `agentic_engineer_2` container, model Sonnet 5.
- **Plans:** `docs/context-run/pre-session-plan.md`, `docs/context-run/technique-plan.md`, `docs/context-run/session-messages.md` (commit `aba59bd`).
- **Phases:** (1) Inventory and correctness ranking -> table v1 (M1-M2). (2) Re-prioritization after the requirement change -> table v2 (M3-M5). (3) Test recommendations for table v2's top 3, coverage check for ranks 4-6, final report (M6-M8).
- **Requirement that changed (M3):** JWT work postponed, so security can't be deferred. R3 (security deferred, unranked) replaced by R3' (security in scope, ranks above correctness); R4 (max 8) replaced by R4' (max 6); new R6 adds `User.java` to scope. R1, R2, R5 unchanged.
- **Context boundaries:** Boundary A in M3 (Phase 1 complete, new focus, active vs replaced rules, carry table v1 and keep IDs stable). Boundary B in M6 (Phase 2 complete, table v2 in the summary file is authoritative, R3/R4 retired by name, no rule change). The agent also ran the CLAUDE.md Context Boundary Procedure at M1, M3, and M6, and correctly skipped it for read-only questions (M2, M4).
- **Proactive summary:** M5, end of Phase 2, saved as `docs/context-run/summary-after-phase2.md` (goal, active rules verbatim, retired rules, decisions, full table v2, open questions, next action).
- **Compaction:** not used. `/context` was 26.9k/967k (3%) at Boundary A and 43.6k/967k (5%) at Boundary B, far below the 60% threshold in the CLAUDE.md Compaction Policy.
- **Commit SHAs:** `aba59bd` (agent v0.1.0 + pre-session and technique plans, before the run); `1acab48` (summary, final report, session notes, this log entry, after the run).
- **Artifacts:** `docs/context-run/summary-after-phase2.md`, `docs/context-run/final-report.md`, `docs/context-run/session-notes.md` (message-by-message observations).

| Dimension | Score (1-4) | Evidence |
|---|---|---|
| Accuracy | 3 | 9 of 10 spot-checked `file:line` citations exact (test-method lines off by one in two places); F9 (plaintext password returned by `POST /api/users`) is real and verified. But F2 claims `deleteById` on a missing ID crashes with a 500; under Spring Boot 3.4.3 / Spring Data JPA 3.x it is silently ignored. The error was never corrected, kept rank 6 in table v2 (displacing F3, a real crash), and reached the final report. Minor slips: "the two findings dropped" (six were dropped); S1's FEATURES item 2 observation from Phase 1 was lost by Phase 2. |
| Task Adherence | 4 | Every rule change applied at the right time: R3 and R4 retired by name, security ranked 1-4, exactly 6 rows, `User.java` read only after R6. Phase 3 used table v2's top 3 (F9, S1, S2), not table v1's. M7 used table v2's ranks 4-6 (S3, F1, F2) despite the ambiguous "F-IDs" wording. Respected Write scope (no writes outside `docs/context-run/`). |
| Coherence | 4 | Final report, summary file, and in-chat tables agree on ranking, IDs, and citations; no "Deferred: security" section survives after Phase 2; dropped findings consistently marked "still valid, not retracted". Handled an accidental duplicate M6 and pasted fragments without redoing work or changing any answer. Only inconsistency: mixed ID prefixes (F9 is a security finding with an F- ID). |
| **Total** | **11/12** | |

- **Context drift / misfires observed:**
  - No retired rule was applied after M3 (agent's own self-audit at M8 agreed).
  - Stale *knowledge* rather than stale *context*: F2's outdated Spring Data behavior was carried unchanged through both boundaries and the summary. Boundaries and summaries preserve whatever the session believes, including errors.
  - Lost observation: S1 tracked by FEATURES item 2 (noted in Phase 1) was replaced by "FEATURES item 1" in Phase 2 and absent from the summary.
  - Summary weaknesses: "Current goal" described the checkpoint, not the overall task; "Next planned action" was only "await direction" because the agent was never told Phase 3's purpose.
  - Judgment call: agent skipped the boundary procedure for M8 (writing the report) under CLAUDE.md item 9 and flagged this itself.
  - Operator error: M6 was pasted twice plus three fragments of agent output; the agent recognized the duplication and did not repeat work.
- **One context-management choice to change next time:** State the full phase plan (all three phases and what each produces) in M1 and in each boundary message, so the proactive summary's "Current goal" and "Next planned action" can carry the overall task forward. Also add a "facts to re-verify" line to the summary (e.g. framework behavior claims like F2) so that a boundary prompts re-checking instead of copying beliefs forward.
