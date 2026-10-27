---
classification: internal
project: proj-lessons
doc_type: lesson
---

## Lesson: run backend tests with the Maven Docker image, not inside the agent container

What happened: the first time we needed to verify FashionMate's backend changes, `./mvnw test` failed inside the agent container. The `agentic_engineer_2` and `agentic_engineer_3` images have no JDK and no Maven, and `fashionmate-backend/.mvn/wrapper/maven-wrapper.properties` is missing, so the wrapper cannot bootstrap. A tester agent then spent minutes probing the environment with `which java` and `docker version` instead of reporting.

What we learned: the reliable path is the official `maven:3.9-eclipse-temurin-21` image run from the Windows host in PowerShell, with the backend mounted at `/app`, the `fashionmate-m2` volume as the dependency cache, and `contextLoads` excluded because it needs a live MySQL database. Git Bash cannot pull that image because it cannot find `docker-credential-desktop`; PowerShell can.

How to apply it: a tester or verifier should report `NOT RUN - needs human` for backend changes and hand the human the PowerShell command, never install a JDK or probe the container. Quote the real `Tests run:` line from the pasted output; the baseline was 34 passing tests on 2026-10-20.
