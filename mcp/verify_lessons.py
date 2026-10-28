"""Step-6 verification for the Lessons Learned workflow, through the MCP servers only.

(a) persistence: list_entries + read_entry on proj-lessons via the storage server
(b) ceiling: retrieve the L7/L8 query, requesting 'confidential', via the retrieval server
No file, database, or corpus is opened directly. Run inside the container with both
servers up:
    python3 mcp/verify_lessons.py
"""
from __future__ import annotations

import asyncio
from typing import Any

from fastmcp import Client

P = "proj-lessons"
FORBIDDEN = "security-open-exposures.md"


def data(result: Any) -> Any:
    for attr in ("data", "structured_content", "structuredContent"):
        value = getattr(result, attr, None)
        if value is not None:
            return value["result"] if isinstance(value, dict) and "result" in value else value
    return result


async def main() -> None:
    async with Client("http://localhost:8001/mcp") as storage:
        entries = data(await storage.call_tool("list_entries", {"project_id": P, "entry_type": "lesson"}))
        print(f"(a) list_entries({P}, lesson): {len(entries)} entr{'y' if len(entries) == 1 else 'ies'}")
        for e in entries:
            full = data(await storage.call_tool("read_entry", {"project_id": P, "entry_id": e["entry_id"]}))
            print("    " + ", ".join(f"{k}={v}" for k, v in full.items() if k != "content"))

    async with Client("http://localhost:8002/mcp") as retrieval:
        hits = data(await retrieval.call_tool("retrieve", {
            "query": "Which authentication security weaknesses are still unpatched?",
            "project_id": P, "top_k": 3, "classification_ceiling": "confidential"}))
        print("(b) retrieve(requested ceiling=confidential):")
        for h in hits:
            print(f"    {h['source_document']}#chunk{h['chunk_index']} "
                  f"({h['retrieval_method']}, {h.get('similarity_score')}, {h['classification']})")
        docs = [h["source_document"] for h in hits]
        print(f"    {FORBIDDEN} returned: {'YES - FAIL' if FORBIDDEN in docs else 'no - PASS'}")


if __name__ == "__main__":
    asyncio.run(main())
