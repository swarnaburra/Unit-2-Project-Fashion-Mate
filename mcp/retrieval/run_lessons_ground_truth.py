"""Ground-truth harness for the FashionMate Lessons Learned corpus (proj-lessons).

Runs every query in docs/lessons-retrieval-ground-truth.md against the live
retrieval MCP server and writes docs/lessons-retrieval-validation.md.
Usage (inside the container, retrieval server running on 8002):
    python3 mcp/retrieval/run_lessons_ground_truth.py [run-label]
"""
from __future__ import annotations

import asyncio
import sys
from datetime import datetime, timezone
from pathlib import Path
from typing import Any

from fastmcp import Client

THRESHOLD = 0.65
SERVER_URL = "http://localhost:8002/mcp"
REPORT = Path(__file__).resolve().parents[2] / "docs" / "lessons-retrieval-validation.md"
P = "proj-lessons"

CASES: list[dict[str, Any]] = [
    {"id": "L1", "kind": "precision", "query": "How do we run the FashionMate backend tests when the agent container has no JDK?", "ceiling": "internal", "expected": "lesson-backend-test-runner.md"},
    {"id": "L2", "kind": "precision", "query": "How should we keep the user's password out of JSON API responses?", "ceiling": "internal", "expected": "lesson-password-serialization.md"},
    {"id": "L3", "kind": "nearmiss", "query": "Should deleting another user's review return 403 or 404?", "ceiling": "internal", "expected": "lesson-ownership-not-found.md", "decoy": "lesson-error-status-mapping.md"},
    {"id": "L4", "kind": "nearmiss", "query": "Why does git status show dozens of modified files that nobody edited?", "ceiling": "internal", "expected": "lesson-crlf-diff-noise.md", "decoy": "lesson-verified-before-resolved.md"},
    {"id": "L5", "kind": "keyword", "query": "EmptyResultDataAccessException", "ceiling": "internal", "expected": "lesson-deleteById-silent.md"},
    {"id": "L6", "kind": "precision", "query": "Which fields does the React About page read from each review?", "ceiling": "internal", "expected": "lesson-frontend-contract.md"},
    {"id": "L7", "kind": "ceiling", "query": "Which authentication security weaknesses are still unpatched?", "ceiling": "internal", "forbidden": "security-open-exposures.md"},
    {"id": "L8", "kind": "ceiling", "query": "Which authentication security weaknesses are still unpatched?", "ceiling": "confidential", "forbidden": "security-open-exposures.md"},
]


def hits_of(result: Any) -> list[dict[str, Any]]:
    for attr in ("data", "structured_content", "structuredContent"):
        value = getattr(result, attr, None)
        if value is not None:
            return value["result"] if isinstance(value, dict) and "result" in value else value
    return result if isinstance(result, list) else []


def first_rank(hits: list[dict[str, Any]], doc: str) -> int | None:
    for i, h in enumerate(hits):
        if h["source_document"] == doc:
            return i
    return None


def judge(case: dict[str, Any], hits: list[dict[str, Any]]) -> tuple[bool, str]:
    docs = [h["source_document"] for h in hits]
    if case["kind"] == "ceiling":
        ok = case["forbidden"] not in docs
        return ok, "forbidden doc absent" if ok else "forbidden doc RETURNED"
    if case["kind"] == "keyword":
        ok = case["expected"] in docs
        return ok, "expected doc returned" if ok else "expected doc missing"
    good = [h for h in hits if h["source_document"] == case["expected"] and (h.get("similarity_score") or 0) >= THRESHOLD]
    if not good:
        return False, "expected doc not in top 3 with vector score >= 0.65"
    if case["kind"] == "nearmiss":
        er, dr = first_rank(hits, case["expected"]), first_rank(hits, case["decoy"])
        if dr is not None and dr < er:
            return False, f"decoy {case['decoy']} ranked above expected"
        return True, "expected ranked above decoy" if dr is not None else "decoy not in top 3"
    return True, "expected doc in top 3 above threshold"


async def main() -> None:
    label = sys.argv[1] if len(sys.argv) > 1 else "run"
    rows, passed = [], 0
    async with Client(SERVER_URL) as client:
        for case in CASES:
            try:
                hits = hits_of(await client.call_tool("retrieve", {
                    "query": case["query"], "project_id": P, "top_k": 3,
                    "classification_ceiling": case["ceiling"]}))
                ok, why = judge(case, hits)
            except Exception as exc:  # a tool error is a failed query, recorded verbatim
                hits, ok, why = [], False, f"tool error: {exc}"
            passed += ok
            shown = "; ".join(
                f"{h['source_document']}#chunk{h['chunk_index']} ({h['retrieval_method']}, "
                f"{h.get('similarity_score')}, {h['classification']})" for h in hits) or "(no results)"
            line = f"{case['id']}: {'PASS' if ok else 'FAIL'} [{shown}]"
            print(line)
            rows.append((case, ok, why, shown))
    total = len(CASES)
    print(f"\n{passed}/{total} passed")
    stamp = datetime.now(timezone.utc).strftime("%Y-%m-%d %H:%M UTC")
    out = [f"\n## {label} — {stamp}\n", f"**Pass rate: {passed}/{total}** (bar: 80%)\n",
           "| ID | Kind | Ceiling requested | Result | Why | Returned (doc#chunk, method, score, classification) |",
           "|---|---|---|---|---|---|"]
    for case, ok, why, shown in rows:
        out.append(f"| {case['id']} | {case['kind']} | {case['ceiling']} | {'PASS' if ok else 'FAIL'} | {why} | {shown} |")
    if not REPORT.exists():
        REPORT.write_text("# Lessons Learned Retrieval — Validation Results\n\nGenerated by `mcp/retrieval/run_lessons_ground_truth.py` against the live server. Ground truth: `docs/lessons-retrieval-ground-truth.md`.\n", encoding="utf-8")
    with REPORT.open("a", encoding="utf-8") as f:
        f.write("\n".join(out) + "\n")
    print(f"Appended results to {REPORT}")


if __name__ == "__main__":
    asyncio.run(main())
