"""Diagnostic only: best vector score per ground-truth query under several chunkings.

Changes nothing (no server, corpus, or answer-key edits). Prints scores only, no
excerpts, and skips any document not classified public/internal.
Run by the human inside the container:
    python3 mcp/retrieval/probe_lesson_scores.py
"""
from __future__ import annotations

import re
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from server import enforce_sizes, parse_front_matter  # noqa: E402
from sentence_transformers import SentenceTransformer  # noqa: E402

CORPUS = Path("/workspace/.memory/reference/lessons")
THRESHOLD = 0.65
QUERIES = {
    "L1": ("How do we run the FashionMate backend tests when the agent container has no JDK?", "lesson-backend-test-runner.md"),
    "L2": ("How should we keep the user's password out of JSON API responses?", "lesson-password-serialization.md"),
    "L3": ("Should deleting another user's review return 403 or 404?", "lesson-ownership-not-found.md"),
    "L4": ("Why does git status show dozens of modified files that nobody edited?", "lesson-crlf-diff-noise.md"),
    "L6": ("Which fields does the React About page read from each review?", "lesson-frontend-contract.md"),
}


def paragraphs(body: str) -> list[str]:
    return [p.strip() for p in re.split(r"\n\s*\n", body) if p.strip()]


def config_current(body: str) -> list[str]:          # what the server does now
    return enforce_sizes(paragraphs(body), 50, 300)


def config_min20(body: str) -> list[str]:
    return enforce_sizes(paragraphs(body), 20, 300)


def config_min1(body: str) -> list[str]:             # one chunk per paragraph
    return enforce_sizes(paragraphs(body), 1, 300)


CONFIGS = {"current(min50)": config_current, "min20": config_min20, "min1": config_min1}


def main() -> None:
    model = SentenceTransformer("all-MiniLM-L6-v2")
    docs = {}
    for path in sorted(CORPUS.glob("*.md")):
        meta, body = parse_front_matter(path.read_text(encoding="utf-8"))
        if meta.get("classification") in ("public", "internal"):
            docs[path.name] = body
    for name, chunker in CONFIGS.items():
        chunks = [(d, i, c) for d, b in docs.items() for i, c in enumerate(chunker(b))]
        emb = model.encode([c for _, _, c in chunks], normalize_embeddings=True)
        print(f"\n== {name}: {len(chunks)} chunks (internal docs only)")
        passes = 0
        for qid, (q, expected) in QUERIES.items():
            qv = model.encode(q, normalize_embeddings=True)
            scored = sorted(((float(emb[k] @ qv), d, i) for k, (d, i, _) in enumerate(chunks)), reverse=True)
            top3 = scored[:3]
            best_exp = max(s for s, d, _ in scored if d == expected)
            ok = any(d == expected and s >= THRESHOLD for s, d, _ in top3)
            passes += ok
            print(f"  {qid}: {'PASS' if ok else 'FAIL'} expected best={best_exp:.3f} | top3: "
                  + ", ".join(f"{d}#{i} {s:.3f}" for s, d, i in top3))
        print(f"  vector precision cases passing: {passes}/{len(QUERIES)}")


if __name__ == "__main__":
    main()
