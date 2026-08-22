# Agent Container Setup

This documents how to run an AI coding agent (Claude Code) safely against this repository,
using the Docker image defined in the root `Dockerfile`. It's the baseline for the rest of
the course's agentic-engineering exercises -- update the container only when the agent has
a real, project-specific need for more tools, files, credentials, or network access.

## Build

```powershell
docker build -t agent-sandbox:fashionmate .
```

Builds on top of the course's published base image
(`us-central1-docker.pkg.dev/hire-human/hire-human-ai/agentic_engineer_1:latest`) and adds
only what this project needs: a Temurin JDK 21 + Maven 3.9.9 for the Spring Boot backend.
Node/npm for the frontend are already present in the base image; the frontend's own
`npm install` runs at agent runtime into the mounted project folder, not baked into the image.

## Run

```powershell
docker run -it --rm `
  -v claude-auth:/claude-auth `
  -v "C:\Users\sarat\IdeaProjects\Unit-2-Project-Fashion-Mate:/workspace" `
  agent-sandbox:fashionmate `
  claude
```

**Mounted paths:**
- `C:\Users\sarat\IdeaProjects\Unit-2-Project-Fashion-Mate` -> `/workspace` (bind mount) --
  this project only. No parent directory, home folder, Desktop, Downloads, `.ssh`, or cloud
  credential folder is ever mounted.
- `claude-auth` -> `/claude-auth` (named Docker volume) -- persists the Claude Code login
  credential across container runs so onboarding/login only has to happen once. This is the
  only thing that survives outside of `/workspace`; everything else in the container's own
  filesystem is ephemeral and disappears with `--rm`.

**Network mode:** default Docker bridge networking (no `--network` flag set, no `-p` ports
published). The container has outbound internet access (required for the Claude API, and
for `apt`/`npm`/Maven during image builds), but nothing inside it is reachable from the host
or the network, and no inbound ports are exposed.

## Smoke Test

Task given to the agent (via `claude -p`, scoped to only the tools it needed for this task):

```powershell
docker run --name smoke-test `
  -v claude-auth:/claude-auth `
  -v "C:\Users\sarat\IdeaProjects\Unit-2-Project-Fashion-Mate:/workspace" `
  agent-sandbox:fashionmate `
  claude -p --allowedTools="Bash,Write" "Summarize this repository's structure in a few bullet points (backend: Java/Spring Boot/Maven under fashionmate-backend; frontend: React/Vite/npm under fashionmate-frontend/fashion-app). Then run the frontend's existing lint check in check-only mode -- cd fashionmate-frontend/fashion-app && npm run lint -- do NOT pass --fix or modify any source files. Include the lint result (pass/fail, error/warning counts) in the summary. Write the final summary to /workspace/agent-summary.md. Do not create, edit, or delete any other files."
```

Terminal output:

```
Done. `npm run lint` passed cleanly (0 errors, 0 warnings, exit code 0) -- no source files were touched. Summary written to `/workspace/agent-summary.md`.
```

Verified afterward:
- `agent-summary.md` was written to the host filesystem via the bind mount (confirmed by
  reading it directly outside the container after it exited).
- `docker diff smoke-test` showed changes only in the container's own ephemeral paths
  (`/tmp/...` scratch files, `/root/.claude/*` and `/root/.npm/_logs` session/tool
  bookkeeping) -- nothing outside `/workspace` or `/claude-auth` was touched.

## Parallel Agent Sessions (Git Worktrees)

Running two agent sessions against this repo at once requires filesystem isolation, or
one session's writes can silently overwrite the other's with no error and no conflict
marker. Git worktrees give each session its own checked-out directory and branch while
still sharing one `.git` history, so the human orchestrator only has to scope the tasks
so they touch different files -- the worktrees handle keeping the sessions from
colliding on disk.

**1. Scope the tasks.** Pick two tasks that touch disjoint files. Example used here:
Agent A refactors/adds tests under `fashionmate-backend/src/test/`; Agent B corrects
documentation in `README.md`, `FEATURES.md`, and the frontend `README.md`. Neither
touches a file the other does, so the eventual merge is conflict-free by construction.

**2. Create one worktree + branch per session**, from the repo root, starting on `main`:

```bash
git worktree add ../fashionmate-agent-a -b feature/agent-a
git worktree add ../fashionmate-agent-b -b feature/agent-b
git worktree list   # confirms main + both worktrees, each on its own branch
```

**3. Launch one container per worktree**, mounting only that worktree (not the main repo)
at `/workspace`:

```powershell
docker run -d --name agent-a `
  -v claude-auth:/claude-auth `
  -v "C:\Users\sarat\IdeaProjects\fashionmate-agent-a:/workspace" `
  agent-sandbox:fashionmate `
  tail -f /dev/null

docker run -d --name agent-b `
  -v claude-auth:/claude-auth `
  -v "C:\Users\sarat\IdeaProjects\fashionmate-agent-b:/workspace" `
  agent-sandbox:fashionmate `
  tail -f /dev/null
```

Each session's task is then given to its own container via `docker exec ... claude -p
--allowedTools="..." "<scoped task>"`, with `--allowedTools` restricted to what that
specific task needs (e.g. Agent A got `Bash` to run `mvn test`; Agent B, doing pure doc
edits, did not).

**4. Monitor both sessions** while they run, and **5. review the diffs** in each worktree
against `main` before merging:

```bash
cd ../fashionmate-agent-a && git status --short && git diff
cd ../fashionmate-agent-b && git status --short && git diff
```

**6. Merge to main** (from the main worktree, one at a time):

```bash
git checkout main
git merge --no-ff feature/agent-a -m "Merge feature/agent-a: ..."
git merge --no-ff feature/agent-b -m "Merge feature/agent-b: ..."
```

Both merges were conflict-free here, which is the expected outcome of step 1's
non-overlapping task scoping -- worktrees only prevent *filesystem* collisions during
the session, they don't decide how the work is divided.

**7. Clean up**, once merged and pushed:

```powershell
docker rm -f agent-a agent-b
```
```bash
git worktree remove ../fashionmate-agent-a
git worktree remove ../fashionmate-agent-b
git branch -d feature/agent-a feature/agent-b   # -d refuses if not fully merged
git worktree list                                # confirms only main remains
```

### Two collisions found while doing this (worth keeping in mind)

**A Git worktree's `.git` file is not self-contained.** It's a one-line pointer back to
the main repo's `.git/worktrees/<name>` folder (that's how worktrees share one object
database/history). On Windows that pointer is an absolute `C:/Users/...` path, which a
Linux container can't resolve if only the worktree folder is mounted -- `git` commands
fail inside the container. Working around it by also bind-mounting the main repo's
`.git` folder into the container is possible but leaks the whole shared git database into
each session and creates other artifacts (a stray host-side directory, CRLF-driven false
diffs). The simpler, cleaner choice made here: containers are used only for the agent's
file edits/build/test work; anything requiring `git` (status, diff, commit, merge) is run
from the host, where the worktree link resolves correctly and the same files are
already there via the bind mount.

**The shared `claude-auth` volume is itself an unisolated resource.** Both containers
mounted the same named volume for the Claude Code OAuth credential. Launching both
sessions' first `claude -p` call at the same moment caused a real collision: one
session's token refresh raced the other's, and the losing session's container was left
with a broken local auth cache that kept failing on every retry -- even after the
credential file on the shared volume itself was fine again. The fix was recreating that
one container fresh (same shared volume, clean container filesystem), which immediately
authenticated. Documented under "what would be tightened next" below: staggering
startup or giving each parallel session its own credential volume would avoid this.

## Security Decisions (provisional -- first pass)

These are first-cut answers for this initial setup. They're expected to change as the
project grows and the agent's real needs become clearer.

**What can the agent see and touch?**
Only this repository (`/workspace`) and the persisted login credential (`/claude-auth`).
No host home directory, SSH keys, cloud credentials, or files from other projects are
reachable from inside the container -- confirmed directly by walking above `/workspace`
(`ls /workspace/..`) and finding no host paths leak through.

**What secrets/credentials does the container have?**
Only the Claude Code OAuth credential (in the `claude-auth` volume). The backend's own
runtime secrets (`DB_USERNAME`, `DB_PASSWORD`, `GEMINI_API_KEY`) are *not* passed into this
agent container at all right now -- the agent can read/edit/lint/compile the code, but
can't run the full Spring Boot app or its DB-backed tests without those being supplied
separately later, deliberately, when there's an actual need to.

**What network access does the container have?**
Outbound-only, default bridge networking. No ports are published to the host, so nothing
inside the container is reachable from outside it.

**What's the blast radius if the agent does something wrong?**
Limited to this one project folder on the host. A destructive or malicious action inside
the container can, at worst, damage files in `/workspace` -- which is this Git repository,
so anything lost is recoverable via `git`/version control. It cannot reach other projects,
the rest of the filesystem, or host credentials, because those were never mounted in the
first place.

**What would be tightened or expanded next, as the project evolves?**
- Add `DB_USERNAME`/`DB_PASSWORD`/`GEMINI_API_KEY` as env vars (not baked into the image)
  only once a task genuinely needs to run the full app or a Gemini-backed feature.
- Consider a MySQL sidecar (e.g. via `docker-compose`) scoped to a project-only Docker
  network, rather than pointing the container at a host-level MySQL instance, if/when
  DB-backed backend tests need to run inside the agent's environment.
- Consider `--network none` for tasks that don't need the Claude API or any other network
  access at all (e.g. pure static analysis), to shrink the attack surface further per task.
- Re-evaluate whether `opencode-ai`, still present from the base image but unused here,
  should be stripped out once the base image itself is revisited.
- Give parallel agent sessions their own `claude-auth` volume/credential (or stagger
  their startup) instead of sharing one -- see the parallel-sessions collision above.
