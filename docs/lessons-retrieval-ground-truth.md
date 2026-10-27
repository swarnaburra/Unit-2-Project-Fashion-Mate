# Lessons Learned Retrieval — Ground-Truth Query Set

Version 1.0.0 (2026-10-27). Written **before** running the retrieval server against the corpus; the answer key must not be edited to fit results.

- Corpus: `.memory/reference/lessons/` (9 documents, project `proj-lessons`; 8 `internal`, 1 `confidential`)
- Server: `mcp/retrieval/server.py` (port 8002), `RETRIEVAL_REFERENCE_DIR=/workspace/.memory/reference/lessons`, server cap `RETRIEVAL_MAX_CEILING=internal`
- Threshold `0.65`, `top_k` 3. Harness: `mcp/retrieval/run_lessons_ground_truth.py` (writes `docs/lessons-retrieval-validation.md`)
- Overall bar: at least 80% of queries pass (7 of 8).

| ID | Scenario | Query | Expected | Filters | Pass criterion |
|---|---|---|---|---|---|
| L1 | Direct match | "How do we run the FashionMate backend tests when the agent container has no JDK?" | `lesson-backend-test-runner.md` | project = proj-lessons, ceiling = internal | expected doc in top 3 with vector score ≥ 0.65 |
| L2 | Direct match | "How should we keep the user's password out of JSON API responses?" | `lesson-password-serialization.md` | project = proj-lessons, ceiling = internal | expected doc in top 3 with vector score ≥ 0.65 |
| L3 | Near-miss | "Should deleting another user's review return 403 or 404?" | `lesson-ownership-not-found.md` (decoy: `lesson-error-status-mapping.md`, which also discusses 404s and status codes) | project = proj-lessons, ceiling = internal | expected doc in top 3 with score ≥ 0.65 **and** ranked above the decoy |
| L4 | Near-miss | "Why does git status show dozens of modified files that nobody edited?" | `lesson-crlf-diff-noise.md` (decoy: `lesson-verified-before-resolved.md`, about trusting reports) | project = proj-lessons, ceiling = internal | expected doc in top 3 with score ≥ 0.65 **and** ranked above the decoy |
| L5 | Literal keyword | "EmptyResultDataAccessException" | `lesson-deleteById-silent.md` | project = proj-lessons, ceiling = internal | expected doc returned (vector or keyword fallback) |
| L6 | Direct match | "Which fields does the React About page read from each review?" | `lesson-frontend-contract.md` | project = proj-lessons, ceiling = internal | expected doc in top 3 with vector score ≥ 0.65 |
| L7 | Classification boundary | "Which authentication security weaknesses are still unpatched?" | best semantic match `security-open-exposures.md` (confidential) must be **absent** | project = proj-lessons, ceiling = internal | `security-open-exposures.md` not in results (empty or internal-only results pass) |
| L8 | Classification boundary, caller over-reach | same query as L7, but the caller **requests** `classification_ceiling = confidential` | `security-open-exposures.md` must still be **absent** | project = proj-lessons, requested ceiling = confidential, server cap = internal | `security-open-exposures.md` not in results; proves the cap is enforced by the server, not by the caller's choice |
