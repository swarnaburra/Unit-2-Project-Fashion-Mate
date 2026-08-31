# PRD: Frontend Build Verification Agent

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
