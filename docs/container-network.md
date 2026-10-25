# Container Network for Agents and MCP Servers

Last updated: 2026-10-20 (Module 3, "Stand Up MCP Servers for Storage and Retrieval")

## Network: `agent-internal`

```
docker network create --internal agent-internal
```

`--internal` keeps the bridge network but removes its route to the outside world: processes in the container, and containers on the same network, can reach each other, but nothing can reach the internet. Use it instead of `--network none` whenever the agent must reach an MCP server in another container or a published port (for example MCP Inspector at `http://localhost:8001/mcp`).

Start an MCP-server or isolated test container on it:

```powershell
docker run -it --rm --network agent-internal -e SLACK_BOT_TOKEN=dummy -e SLACK_TEAM_ID=dummy -v "${PWD}:/workspace" -v "${PWD}/.memory:/memory" agentic_engineer_3 bash
```

## Verified behavior (2026-10-20, `agentic_engineer_3`)

| Network | `curl http://localhost:8000` (server in the same container) | `curl https://example.com` |
|---|---|---|
| `--network none` | **works** (HTTP 200, directory listing) | blocked: `Could not resolve host` |
| `--network agent-internal` | works | blocked: `Could not resolve host` |

**Finding:** contrary to the lesson's expectation, `--network none` does *not* block localhost inside a single container. Docker keeps the loopback interface (`lo`), so an agent and an MCP server in the **same** container can already talk. What `--network none` removes is every other path: other containers, the host, and published ports (`-p` has no effect). The real reasons for `agent-internal` are therefore (1) agent and MCP servers in **separate** containers and (2) reaching a server from the host (MCP Inspector), while still blocking the internet.

## Trade-off: Claude Code needs the model API

On `agent-internal`, the Claude Code harness itself can't reach the Anthropic API either, so sessions that run `claude` use the normal (default bridge) network with explicit `-p` port mappings, as in the lesson's storage-server command. Egress for the harness is then limited by what the container can do, not by the network. Options to close that gap later (Module 4): an egress proxy that allows only the model API host, or running the harness outside the internal network and only the MCP servers inside it.

FashionMate's own `docker-compose.yml` already defines an internal network for the app stack (`unit-2-project-fashion-mate_fashionmate-internal`).
