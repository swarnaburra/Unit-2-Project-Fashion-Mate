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
