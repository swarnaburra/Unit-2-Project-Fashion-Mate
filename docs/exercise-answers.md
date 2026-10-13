# Exercise Answers: Frontend Build Verification Agent

Subject: the frontend build verification agent, defined in `docs/prd.md`, scored against
`docs/rubric.md`, and recorded as Runs 006-009 in `docs/iteration-log.md`. The agent
definition is `.claude/agents/frontend-build-verifier.md`.

## Q1: Why was this task a good candidate for an agent?

**Repeatable workflow.** The task runs the same command under the same conditions every
time, so a change in score can be traced back to a change in the prompt. Repeatability
also had to be built on purpose. Run 006 built cleanly, which meant the remediation path
was never reached. So I induced the known Rollup failure deliberately, by moving the
platform-specific Rollup binaries out of `node_modules`, and kept that failure identical
across Runs 007 and 008. The Remediation Handling improvement can be credited to the
prompt change only because the failure condition stayed the same. The workflow is only
partly repeatable, though. Its output is stable, but cost and the agent's choice of extra
commands vary from run to run.

**Need for judgment.** On a clean build the agent adds little over a script that runs the
build and prints the exit code. The judgment is needed on the failure path. The agent has
to decide whether a stack trace matches the known `@rollup/rollup-*` optional-dependency
signature, and that decision gates a destructive action (`rm -rf node_modules` followed by
`npm install`). It also has to separate blocking errors from non-blocking findings when it
makes its recommendation. A static script cannot make either call well, which is why the
failure condition was the right one to test under.

**Evaluability.** Cheap, independent checks exist for almost every claim the agent makes:
the build's exit code, `git status`, an md5 checksum of `package-lock.json`, and the
session transcript. None of the scoring has to rest on the agent's own word. The main
lesson was that evaluability has to be instrumented, not assumed. The default JSON output
returns only the final result, so Scope Compliance was not actually measured until the
transcripts were recovered. Once they were, the real scores turned out to be lower than
the ones recorded earlier.

**Scoped tool access.** The agent needs only Bash, and it reports to standard output, so
it is given no Write tool. That makes it unable to write a tracked file at the harness
level, whatever its prompt says. The container limits its reach to the workspace, and the
only destructive action it can take affects a single gitignored directory. The gap is
that Bash is one tool that grants an unbounded command space. The PRD allows only a
handful of commands, and every scope violation recorded in this cycle happened in the
space between "Bash is allowed" and "only these commands are allowed."

## Q2: What was the most important misfire in the initial run, and what caused it?

The most important misfire came in Run 007, the first run against the reproduced Rollup
failure. The agent correctly identified the error signature and correctly cited the
underlying npm bug. It then declined the remediation that the PRD makes mandatory. In its
own words, it did not act because doing so "touches lockfile/node_modules state -- that's
a call for you to make." The build was left failing, and the required action never
happened.

The cause was a gap between the definition and the instructions, not a reasoning error by
the agent. The remediation step had been added to the PRD earlier, but it was never
carried into the prompt the agent actually received. Prompt 002 still said "Do not modify
any file," which forbids the remediation outright. The agent cannot see the PRD, so it
obeyed the prompt it was given, which is correct behavior for an agent and a defect in the
definition.

A second cause made the misfire worse because it hid it. Under the rubric at that time,
Run 007 scored a clean pass. Every rubric mention of the remediation penalized running it
wrongly or too often, and none penalized skipping it when it was required. The run also
included unflagged read-only commands (`ls`, `cat package.json`, `grep`), which were
detected only later from the transcript.

## Q3: What change did you implement, and why did you expect it to help?

I changed one variable: Prompt 002 was replaced with Prompt 003, released alongside PRD
v1.1.0. Prompt 003 replaces the blanket "do not modify any file" with a narrow
authorization. If, and only if, the build fails with the known Rollup signature, the agent
is authorized and required to remediate exactly once: remove `node_modules`, run
`npm install` with no flags, and rebuild a single time. It must use that rebuild as its
final verdict, state that it remediated, and quote the triggering error. It must never
touch `package-lock.json`. All other prohibitions stay in place.

I expected this to help because of the evidence from Run 007. There the agent named the
exact clause it was obeying when it declined, which showed that the missing authorization
was the only thing stopping it. It had already diagnosed the problem correctly. Giving it
explicit permission, tied to the same signature it had already recognized, should turn a
correct diagnosis into the required action. At the same time, I added rubric dimension 6
(Remediation Handling) so that skipping a required remediation would be scored as a
failure instead of passing silently.

## Q4: Did the revised version improve?

**Per-dimension rubric scores (Run 007 compared with Run 008):**

| Dimension | Run 007 | Run 008 |
|---|---|---|
| Execution Fidelity | 4 | 4 |
| Verdict Accuracy | 3 | 3 |
| Evidence Quality | 3 | 4 |
| Scope Compliance | 2 | 1 |
| Recommendation Quality | 3 | 4 |
| Remediation Handling | 2 | 4 |
| Overall | FAIL | FAIL |

**What improved.** The targeted fix worked. Remediation Handling rose from 2 to 4. In Run
008 the agent matched the signature, quoted the full Rollup error as its justification,
ran the remediation exactly once, and used the rebuild as its verdict. That rebuild
exited 0 with 66 modules transformed. The checksum of `package-lock.json` was identical
before and after, and `git status` showed no tracked changes. There were also two gains I
had not aimed for. Evidence Quality rose from 3 to 4 because the agent separated the
blocking build error from informational findings. Recommendation Quality rose from 3 to 4
because it distinguished the blocking error, now fixed, from fourteen non-blocking
`npm audit` findings, and it correctly declined to act on those. Execution Fidelity and
Verdict Accuracy stayed at their maximums.

**Regression.** Scope Compliance fell from 2 to 1, so Run 008 still failed overall. The
transcript shows the agent ran five `git` commands plus `file`, and the rubric names a
git command as a disqualifying example. The cause was the new prompt itself. The line
"Never delete or modify `package-lock.json`" created a duty to verify compliance, and git
was the only way to verify it, yet git was never authorized. Inside the container, git
reported the package files as modified because of line-ending differences across the bind
mount. The agent spent six commands proving that this was not a real change. The rubric
was also inconsistent with itself: level 4 rewarded self-documenting compliance, while
level 1 forbade the only tool that could produce it.

**Cycle time and cost.** Run 008 was much slower and more expensive. Wall time went from
18 seconds to 105 seconds, mostly because of the `npm install` of 216 packages that the
remediation requires. Cost rose from $0.0512 to $0.2466, about 4.8 times higher. Output
tokens went from 747 to 2,436, and cache-read tokens went from about 101,000 to about
387,000, with about 15,500 cache-creation tokens added. Some of that increase is the
required remediation doing its job. Some of it is waste from the git detour. Earlier runs
also showed that cost varies by up to 58% on identical prompts because of cache behavior,
so the cost comparison is only approximate.

**Run 009.** Prompt 004 (PRD v1.1.1) was written to fix the scope regression. It makes the
authorized command list a closed set, forbids exploration and all git commands by name,
assigns scope verification to the orchestrator, and asks the agent to list every command
it ran. Run 009 was meant to verify it, but it never executed. The container's
authentication had expired and the credential was wiped, so the run made no API calls and
cost nothing. It is logged as BLOCKED rather than FAIL because there was no agent behavior
to score. The Scope Compliance fix is therefore committed but unverified.

**Limitations.** The comparison rests on one run per prompt, so a single run cannot show
whether a score change is stable or just run-to-run variation, and the agent's choice of
commands has varied between runs. Scope Compliance scores before transcript recovery were
inferred from `git status` and were too high, so only the transcript-based rescores are
comparable. The Rollup failure was induced artificially, which keeps the condition fixed
but may not match every way the real failure appears. Finally, no version of the agent has
yet passed every dimension. The revision fixed the misfire it targeted and introduced a
new one, and the fix for that new one is still waiting on a successful run.
