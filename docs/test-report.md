# Backend Test Report

**Command:** `mvn test` (run from `fashionmate-backend/`, inside the `agent` container
defined in `docker-compose.yml`, against the `mysql` sidecar service)
**Date:** 2026-08-31
**Result:** BUILD SUCCESS (all tests passed)

## Summary

| Total | Passed | Failed | Errored |
|-------|--------|--------|---------|
| 23    | 23     | 0      | 0       |

## Per-class results

| Test class | Tests run | Failures | Errors |
|---|---|---|---|
| `fashionmate_backend.controllers.GlamUpControllerTest` | 2 | 0 | 0 |
| `fashionmate_backend.controllers.OutfitTipControllerTest` | 1 | 0 | 0 |
| `fashionmate_backend.controllers.ReviewControllerTest` | 6 | 0 | 0 |
| `fashionmate_backend.controllers.StyleLensControllerTest` | 1 | 0 | 0 |
| `fashionmate_backend.controllers.UserControllerTest` | 7 | 0 | 0 |
| `fashionmate_backend.FashionmateBackendApplicationTests` | 1 | 0 | 0 |
| `fashionmate_backend.models.GlamUpTest` | 1 | 0 | 0 |
| `fashionmate_backend.models.ImageRequestTest` | 1 | 0 | 0 |
| `fashionmate_backend.models.ReviewTest` | 1 | 0 | 0 |
| `fashionmate_backend.models.StyleLensTest` | 1 | 0 | 0 |
| `fashionmate_backend.models.UserTest` | 1 | 0 | 0 |

## `contextLoads` (previously errored, now passing)

`FashionmateBackendApplicationTests.contextLoads` boots the full Spring application
context, which requires a live MySQL connection to build Hibernate's
`EntityManagerFactory`. In the previous run (2026-08-26, recorded further down this
file's history), no MySQL server was reachable from the plain `agent-sandbox:fashionmate`
container, so this test errored with `Connection refused`.

This run added a MySQL sidecar via `docker-compose.yml` (see `setup.md`, "Running
Backend Tests Against a Live Database") and pointed the backend at it with `DB_HOST=mysql`.
With a real database reachable, `contextLoads` now connects successfully:

- `HikariPool-1` opened a connection to the `mysql` service.
- Hibernate auto-created the schema (`glam_up`, `review`, `style_lens`, `user` tables,
  including the `user.email` unique constraint and the `review`/`style_lens` foreign
  keys to `user.id`) via `spring.jpa.hibernate.ddl-auto=update`.
- The application context started in 25.19s; the test completed in 28.08s total.

## Notes

- No source or test files were modified as part of this run (only `application.properties`,
  `docker-compose.yml`, and `setup.md` changed, to add DB host configurability and the
  sidecar itself -- not to make any test pass artificially).
- The MySQL sidecar is ephemeral (no data volume) and isolated (no published ports,
  reachable only from the `agent` container on the compose-created internal network).
- This supersedes the 2026-08-26 run's `contextLoads` error, which was correctly
  diagnosed at the time as an environment limitation (no DB reachable), not a code
  defect -- that diagnosis is confirmed by this run: the same test now passes once a
  database is actually reachable, with zero source/test changes in between.
