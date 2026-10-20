# Decision 003 - Run backend tests with the Maven Docker image

**Date:** 2026-10-18
**Review by:** 2027-01-16
**Status:** Active
**Scope:** Project-scoped (all backend test runs). Re-check whenever the dev-container image (`agentic_engineer_2`), `pom.xml` Java version, or the Maven wrapper changes.

**Decision:** Run the backend test suite from the host with the official image `maven:3.9-eclipse-temurin-21`, mounting `fashionmate-backend` at `/app` and caching dependencies in the named volume `fashionmate-m2`, and exclude `FashionmateBackendApplicationTests` (the `contextLoads` test). The command, run from **PowerShell** in the repo root:

`docker run --rm -v "${PWD}\fashionmate-backend:/app" -v fashionmate-m2:/root/.m2 -w /app maven:3.9-eclipse-temurin-21 mvn test "-Dtest=!FashionmateBackendApplicationTests" "-Dsurefire.failIfNoSpecifiedTests=false"`

Baseline: 2026-10-18 29 tests; 2026-10-19 32 tests (after decision-007); 2026-10-20 34 tests (after decision-005 gap 3), 0 failures, 0 errors, BUILD SUCCESS (~44 s on the first run including downloads, faster once `fashionmate-m2` is warm).

**Rationale:** The `agentic_engineer_2` dev container has no JDK or Maven, and `fashionmate-backend/.mvn/wrapper/maven-wrapper.properties` is missing, so `./mvnw test` fails everywhere. `pom.xml` sets `java.version` 21, so the image must be a Temurin 21 one. `contextLoads` needs a live MySQL database (see the comment in the test and `docs/prd-backend-tests.md`), so it fails with "Connection refused" without one; excluding it keeps the unit-test result meaningful. The named volume avoids re-downloading ~100 MB of dependencies each run.

**Alternatives rejected:**
- Installing a JDK in the dev container: changes the course-provided image, and would be lost on every `--rm` restart unless the Dockerfile is rebuilt.
- Restoring the Maven wrapper: still needs a JDK on the host or in the container.
- Running from MSYS2/UCRT64 Git Bash: pulling the image fails with `docker-credential-desktop: executable file not found`, because that shell can't see Docker Desktop's helper folder. Git Bash works only once the image is already pulled.
- Running `contextLoads` against `docker-compose.yml`'s MySQL sidecar: valid for integration checks, but too slow and heavy for every change.

**Known noise (not failures):** Mockito prints "self-attaching to enable the inline-mock-maker" and JDK dynamic-agent warnings on Java 21. Harmless today; revisit if Mockito or the JDK starts disallowing dynamic agents.
