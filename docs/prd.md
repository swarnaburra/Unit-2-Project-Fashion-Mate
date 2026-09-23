# PRD: Frontend Build Verification Agent

**Version:** 1.1.1 (see Changelog at the bottom)

## Workflow Description

A single agent runs the frontend's production build inside the sandboxed container
and reports whether it succeeded, so a human can decide if the frontend is ready for
the next step (e.g. deployment) without running the build themselves.

## Trigger

A human orchestrator manually invokes the agent (via `claude -p` inside the `agent`
container defined in `docker-compose.yml` -- e.g. `docker compose run --rm agent`,
which builds the same `agent-sandbox:fashionmate` image that `setup.md`'s standalone
`docker run` command used directly) when they want to confirm the frontend still
builds cleanly -- for
example, before merging a frontend-touching branch, or before a deploy. The MySQL
sidecar that `docker-compose.yml` also defines is irrelevant to this workflow (the
frontend build has no database dependency) and does not need to be running for this
agent to be invoked.

## Decision Events

- **Did the build command exit successfully or fail?** Governs whether the report's
  headline verdict is "ready" or "not ready."
- **Does the output contain warnings even on a successful build?** Governs whether the
  recommendation is unqualified ("ready") or qualified ("ready, but note these
  warnings").
- **Does anything in the output imply follow-up work is needed** (e.g. a deprecation
  notice, a missing env var warning)? Governs whether the agent flags a follow-up item
  beyond the pass/fail verdict itself.
- **Does the failure match the known `@rollup/rollup-*-gnu` optional-dependency bug
  signature** (`Cannot find module '@rollup/rollup-linux-x64-gnu'` or equivalent,
  npm/cli#4828 -- seen in Runs 001-003, caused by a `node_modules` installed for a
  different platform than the container's)? Governs whether the agent performs the one
  authorized remediation (see Actions) before giving a final verdict, versus reporting
  the failure as-is.

## Actions (in order)

1. Change into `fashionmate-frontend/fashion-app`.
2. Run `npm run build`.
3. Capture the full command output (stdout/stderr) and its exit code.
4. **If, and only if,** the build failed with the known `@rollup/rollup-*-gnu`
   optional-dependency signature (see Decision Events): run `rm -rf node_modules` (never
   touch the committed `package-lock.json`), run `npm install`, then re-run `npm run
   build` exactly once more. Report that this remediation was attempted and why, and use
   the result of the re-run as the final result. Do not repeat this remediation loop
   more than once per invocation.

   > **This step is mandatory when the signature matches, not optional.** It must be
   > carried into the invoking prompt verbatim enough that the agent knows it is
   > authorized -- a prompt that says only "do not modify any file" *overrides* this step
   > and the agent will correctly decline to act on it. See Run 007, where exactly that
   > happened, and the Changelog entry below.
5. Identify and summarize any errors or warnings present in the (possibly re-run)
   output.
6. State a clear pass/fail verdict for the build.
7. Recommend whether the frontend is ready for the next step, based on the verdict and
   any warnings found.
8. Do not modify any tracked file in the repository. Do not run any command other than
   `cd`, `npm run build`, and -- only under the specific condition in step 4 -- `rm -rf
   node_modules` and `npm install`. No `npm install --save`/`--force`, no touching
   `package-lock.json`, no publish, no deploy, no push.

## Acceptance Criteria (falsifiable)

- The agent actually ran `npm run build` and reported its real result -- not a guess
  based on reading `package.json` or prior knowledge of the project.
- The stated pass/fail verdict matches the actual exit code of the build command.
- If the build failed, the report quotes the actual error text from the command output,
  not a paraphrase invented without evidence.
- If the build succeeded but produced warnings, the report explicitly lists them rather
  than silently omitting them.
- `git status` after the run shows zero **tracked** files changed -- in particular,
  `package-lock.json` is untouched even if the Step 4 remediation ran (only the
  gitignored `node_modules` directory may differ).
- The agent ran no command beyond `cd`, `npm run build`, and -- only when Step 4's
  specific failure signature was matched -- `rm -rf node_modules` followed by one `npm
  install` and one re-run of `npm run build`.
- If the Step 4 remediation ran, the report explicitly states that it did, and why
  (quoting the matched error signature) -- it is not silently folded into the verdict as
  if the first attempt had succeeded.
- **Conversely, if the build failed with the Step 4 signature and the remediation did
  *not* run, that is a failed run** -- the agent under-reached on a mandatory action.
  Scored by Rubric dimension 6 (Remediation Handling).

## Changelog

### 1.1.1 -- 2026-09-23

Closes the Scope Compliance regression from Run 008 and the older read-only overreach
that transcript capture exposed in Runs 006-007. Both are the same failure in different
clothes: **the agent reaching for a tool it was never authorized to use, because the
definition gave it a duty without giving it a means.**

- **Run 008 (score 1):** ran five `git` commands verifying it had not modified
  `package-lock.json` -- a duty Prompt 003 created and never assigned an owner.
- **Runs 006-007 (score 2):** ran `ls` / `cat package.json` / `grep` to orient
  themselves before building, without flagging it.

**Fix, in Prompt 004:** state the authorized command list as a closed set; name the
common temptations explicitly (`ls`, `cat`, `grep`, `file`, and *any* `git` command);
assign scope verification to the orchestrator in so many words; and require the report
to list every command actually run, with an explicit admission if any fell outside the
set. That last clause converts an undetectable violation into a self-reported one --
the agent can always narrate its own commands for free, whereas confirming repository
state costs an unauthorized tool (see Lesson L5).

**Fix, in Rubric dimension 4:** level 4 previously rewarded making scope compliance
"self-documenting rather than something the reviewer has to verify independently,"
while level 1 disqualified any `git` command -- rewarding a goal and forbidding its only
instrument. Level 4 now means *narrating the commands you ran*, explicitly not
*inspecting repository state*.

### 1.1.0 -- 2026-09-22

One defect, two causes, both observed in Run 007:

**Observed.** With the `@rollup/rollup-linux-x64-gnu` failure reproduced deliberately,
the agent correctly identified the signature, correctly diagnosed npm/cli#4828 -- and
then declined the mandatory Step 4 remediation, saying: *"I did not do this myself since
it falls outside 'do not run npm install with --save/--force' caution and touches
lockfile/node_modules state -- that's a call for you to make."*

**Cause 1 -- prompt/PRD drift.** Step 4 was added to this PRD in commit `2c56a6c` but was
never carried into the invoking prompt. Prompt 002 instead says "Do not modify any
file," which forbids the remediation outright. The agent obeyed the prompt over a PRD it
cannot see, which is the correct behavior for an agent and a defect in the definition.
Fixed by Prompt 003, which carries the conditional authorization (and the
`package-lock.json` prohibition) into the prompt itself.

**Cause 2 -- rubric blind spot.** Every mention of the remediation in `docs/rubric.md`
penalized running it *wrongly or too often*; none penalized *skipping it when required*.
Run 007 therefore scored 4/3/3/4/3 -- a clean **PASS** -- while violating a mandatory
PRD action. Fixed by new Rubric dimension 6 (Remediation Handling), which scores the
under-reach case and is marked `N/A` when the signature never matched.

### 1.0.0 -- 2026-08-25

Initial PRD. Step 4 remediation added later in commit `2c56a6c`.
