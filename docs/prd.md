# PRD: Frontend Build Verification Agent

## Workflow Description

A single agent runs the frontend's production build inside the sandboxed container
and reports whether it succeeded, so a human can decide if the frontend is ready for
the next step (e.g. deployment) without running the build themselves.

## Trigger

A human orchestrator manually invokes the agent (via `claude -p` inside the
`agent-sandbox:fashionmate` container) when they want to confirm the frontend still
builds cleanly -- for example, before merging a frontend-touching branch, or before a
deploy.

## Decision Events

- **Did the build command exit successfully or fail?** Governs whether the report's
  headline verdict is "ready" or "not ready."
- **Does the output contain warnings even on a successful build?** Governs whether the
  recommendation is unqualified ("ready") or qualified ("ready, but note these
  warnings").
- **Does anything in the output imply follow-up work is needed** (e.g. a deprecation
  notice, a missing env var warning)? Governs whether the agent flags a follow-up item
  beyond the pass/fail verdict itself.

## Actions (in order)

1. Change into `fashionmate-frontend/fashion-app`.
2. Run `npm run build`.
3. Capture the full command output (stdout/stderr) and its exit code.
4. Identify and summarize any errors or warnings present in that output.
5. State a clear pass/fail verdict for the build.
6. Recommend whether the frontend is ready for the next step, based on the verdict and
   any warnings found.
7. Do not modify any file in the repository. Do not run any command other than `npm run
   build` (and the `cd` to reach it) -- no `npm install`, no publish, no deploy, no push.

## Acceptance Criteria (falsifiable)

- The agent actually ran `npm run build` and reported its real result -- not a guess
  based on reading `package.json` or prior knowledge of the project.
- The stated pass/fail verdict matches the actual exit code of the build command.
- If the build failed, the report quotes the actual error text from the command output,
  not a paraphrase invented without evidence.
- If the build succeeded but produced warnings, the report explicitly lists them rather
  than silently omitting them.
- `git status` after the run shows zero files changed -- the agent touched nothing in
  the repository.
- The agent ran no command beyond `cd` and `npm run build`.
