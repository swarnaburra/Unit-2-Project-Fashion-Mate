# Decision 002 - Test file layout

**Date:** 2026-10-17
**Review by:** 2027-01-15
**Status:** Active
**Scope:** Project-scoped (applies to all backend and frontend tests). Reviewed every 90 days.

**Decision:** Test files live in a separate test tree, never alongside the source files they test. For the backend this is the existing Maven layout: source in `fashionmate-backend/src/main/java/...`, tests in `fashionmate-backend/src/test/java/...`, mirroring the source package structure (for example `controllers/ReviewController.java` -> `controllers/ReviewControllerTest.java`). The frontend (`fashionmate-frontend/fashion-app`, which has no tests yet as of 2026-10-17) follows the same principle when tests are added: a dedicated test directory, not `*.test.*` files next to components.

**Rationale:** Keeps source and test code clearly separated, and the backend already follows this layout, so no files need to move. `src/test/java` is where Maven discovers tests by default, so no `pom.xml` changes are needed.

**Alternatives rejected:** Moving backend tests to a new top-level `/tests` directory (would require overriding `testSourceDirectory` in `pom.xml`; if done without that change, `mvn test` silently runs no tests). Co-locating tests next to source files.

**How to apply:** Put new backend tests under `src/test/java` in the package matching the class under test. Do not move existing tests or change the build's test source directory. Before adding the first frontend tests, confirm the exact directory name with the human.
