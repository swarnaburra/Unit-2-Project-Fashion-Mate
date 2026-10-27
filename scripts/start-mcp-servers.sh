#!/bin/sh
# Start FashionMate's storage (8001) and retrieval (8002) MCP servers inside the
# agent container (agentic_engineer_3). Run from /workspace:  sh scripts/start-mcp-servers.sh
# Data locations are fixed here, not chosen by agents:
#   storage DB + audit log -> .memory/runtime/ (gitignored runtime data)
#   lessons corpus         -> .memory/reference/lessons/ (curated, committed, mounted :ro)
set -e
cd /workspace
mkdir -p .memory/runtime

STORAGE_DB_PATH=/workspace/.memory/runtime/storage.db \
STORAGE_AUDIT_PATH=/workspace/.memory/runtime/storage-audit.log \
STORAGE_ALLOWED_PROJECTS=proj-lessons \
  python3 mcp/storage/server.py --port 8001 --host 127.0.0.1 > .memory/runtime/storage-server.log 2>&1 &
echo "storage   pid $!  (log: .memory/runtime/storage-server.log)"

RETRIEVAL_REFERENCE_DIR=/workspace/.memory/reference/lessons \
RETRIEVAL_MAX_CEILING=internal \
  python3 mcp/retrieval/server.py --port 8002 --host 127.0.0.1 --chunking paragraph > .memory/runtime/retrieval-server.log 2>&1 &
echo "retrieval pid $!  (log: .memory/runtime/retrieval-server.log)"

echo "Waiting for both servers..."
for i in $(seq 1 90); do
  if grep -q "Uvicorn running" .memory/runtime/storage-server.log 2>/dev/null && \
     grep -q "Uvicorn running" .memory/runtime/retrieval-server.log 2>/dev/null; then
    grep -h "Indexed\|WARNING" .memory/runtime/retrieval-server.log
    echo "Both servers are up: storage http://localhost:8001/mcp, retrieval http://localhost:8002/mcp"
    exit 0
  fi
  sleep 2
done
echo "Timed out; check the logs in .memory/runtime/"; exit 1
