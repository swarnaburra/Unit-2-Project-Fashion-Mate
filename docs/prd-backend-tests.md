# PRD: Backend Test Suite Summary Agent

**Version:** 1.1.0 (see Changelog at the bottom)

## Workflow Description

A single agent runs the backend's test suite inside the sandboxed container and
produces a saved summary of the results, so a human can see test health without
running the suite themselves.

## Trigger

A human orchestrator manually invokes the agent via `claude -p`, either inside the plain
`agent-sandbox:fashionmate` container (no database reachable) or inside the `agent`
container defined in `docker-compose.yml` -- e.g. `docker compose run --rm agent` --
which also brings up a `mysql` sidecar service that the backend can reach at
`DB_HOST=mysql`. The compose invocation requires `DB_PASSWORD` (and `GEMINI_API_KEY`) to
be set in the host shell, and depends on the `claude-auth` volume being name-pinned in
`docker-compose.yml` -- without that pin Compose mounts an empty project-namespaced
volume and the agent dies at `Not logged in` before running anything (Run 003).
The orchestrator picks whichever invocation matches what they're
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
6. Write the summary to `docs/test-report.md`, and **state in it the exact command run
   and its exact numeric exit code** -- not just Maven's textual `BUILD SUCCESS` /
   `BUILD FAILURE` line.
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
- The summary states the exact command run and its exact numeric exit code (e.g.
  `Exit code: 0`), not only Maven's textual `BUILD SUCCESS`/`BUILD FAILURE` line.
- `git status` after the run shows changes only to `docs/test-report.md` -- no source or
  test file under `fashionmate-backend/src` was touched.
- The agent ran no command beyond `cd` and `mvn test`.

## Changelog

### 1.1.0 -- 2026-09-22

Two evidence-driven changes, both tied to behavior observed in Run 003:

1. **Fixed the compose invocation's auth failure.** `docker-compose.yml` declared the
   credential volume as a bare `claude-auth:` key, so Compose namespaced it to
   `unit-2-project-fashion-mate_claude-auth` -- an empty volume, distinct from the
   `claude-auth` that `setup.md`'s `docker run` populates. Every compose-based `claude -p`
   invocation therefore returned `Not logged in - Please run /login` and did no work.
   Pinned with `name: claude-auth`, and noted the dependency in Trigger above.
2. **Required the numeric exit code in the report.** Backend Runs 001 and 002 both scored
   Execution Fidelity 3 rather than 4 for exactly one reason: the report gave Maven's
   textual `BUILD SUCCESS`/`BUILD FAILURE` but no shell exit code. The Frontend agent hit
   the identical gap and closed it by adding one sentence to its prompt (Prompt 001 ->
   002), which lifted that dimension 3 -> 4 on the next run. Carried the same instruction
   here, into both Actions step 6 and the acceptance criteria, and into Prompt 002 below.

### 1.0.0 -- 2026-08-26

Initial PRD, added for Module 1's parallel-agent lab.
