# PRD: Backend Test Suite Summary Agent

## Workflow Description

A single agent runs the backend's test suite inside the sandboxed container and
produces a saved summary of the results, so a human can see test health without
running the suite themselves.

## Trigger

A human orchestrator manually invokes the agent via `claude -p`, either inside the plain
`agent-sandbox:fashionmate` container (no database reachable) or inside the `agent`
container defined in `docker-compose.yml` -- e.g. `docker compose run --rm agent` --
which also brings up a `mysql` sidecar service that the backend can reach at
`DB_HOST=mysql`. The orchestrator picks whichever invocation matches what they're
checking: the plain container for a quick DB-independent test sanity check, or the
compose invocation when they specifically want to know whether the full suite,
including `contextLoads()`, passes against a live database. This is invoked when they
want a current summary of backend test health -- for example, before merging a
backend-touching branch, or to check whether a known-blocked test is still blocked.

## Decision Events

- **Did every test pass, or were there failures/errors?** Governs whether the summary's
  headline is a clean pass or a list of problems.
- **Is a failure a genuine regression or an environment limitation** (e.g. the
  `contextLoads()` test failing for lack of a live MySQL connection)? Governs whether
  the agent should flag a result as "needs attention" versus "expected,
  environment-only." **This is conditional on which invocation was used** (see
  Trigger): in the plain container, `contextLoads()` failing with `Connection refused`
  is a known, accepted, environment-only condition (documented in `setup.md`); in the
  `docker-compose.yml` invocation, a live `mysql` sidecar is reachable, so
  `contextLoads()` failing there is a genuine regression the summary must flag as
  "needs attention," not wave away as expected.
- **Does the output file already exist?** Governs whether the agent overwrites it with a
  fresh summary (it should) rather than appending or refusing to write.

## Actions (in order)

1. Change into `fashionmate-backend`.
2. Run `mvn test`.
3. Capture the full command output (stdout/stderr) and its exit code.
4. Count total tests run, passed, failed, and errored.
5. For any test that didn't pass, quote its actual failure/error message from the
   output.
6. Write the summary to `docs/test-report.md`.
7. Do not modify any source or test file, and do not attempt to fix any failing test.
   Do not run any command other than `cd` and `mvn test`.

## Acceptance Criteria (falsifiable)

- The agent actually ran `mvn test` and reported its real result -- not a guess based on
  reading test source files.
- The reported pass/fail/error counts match the actual counts in Maven's own summary
  line (e.g. `Tests run: X, Failures: Y, Errors: Z`).
- Every failure/error mentioned is quoted or accurately paraphrased from the real
  output, not invented.
- The summary states which invocation was used (plain container vs. the
  `docker-compose.yml` `agent` service with the `mysql` sidecar), since that determines
  whether a `contextLoads()` failure is expected/environment-only or a genuine
  regression -- see Decision Events.
- `docs/test-report.md` exists after the run and contains the summary.
- `git status` after the run shows changes only to `docs/test-report.md` -- no source or
  test file under `fashionmate-backend/src` was touched.
- The agent ran no command beyond `cd` and `mvn test`.
