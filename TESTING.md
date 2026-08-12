# Testing Strategy (Course Requirement 3 baseline)

FashionMate currently has two independent automated checks — one per stack. Both are
run locally today; wiring either (or both) into a GitHub Action is Module 4 work, not
required yet.

## Frontend: ESLint

**Command:**

```bash
cd fashionmate-frontend/fashion-app
npm install   # first time only
npm run lint
```

**What it checks:** `eslint .` runs against every `.js`/`.jsx` file under `src/` using
the rules in `eslint.config.js` — the React Hooks rules-of-hooks/exhaustive-deps plugin,
the React Fast Refresh plugin, and `no-unused-vars`.

**What a passing result tells you:** no dangling/incorrect `useEffect` dependencies, no
dead variables, and no patterns that would break Vite's hot-module-reload in dev. It is
a static check — it does not verify runtime behavior — but it catches real bugs (it
originally caught a stale-`userId` bug in `About.jsx`'s review-fetching `useEffect`,
which has since been fixed).

**Current status:** clean (0 errors, 0 warnings) as of the fixes to `Header.jsx`,
`UserContext.jsx`, and `About.jsx`.

## Backend: Spring Boot context test

**Command:**

```bash
cd fashionmate-backend
./mvnw test
```

(Requires JDK 21 locally, matching `pom.xml`'s `<java.version>`. Run this from
IntelliJ or a machine with JDK 21 installed — it will not compile under older JDKs
because the code uses JDK 21 APIs, e.g. `List.getFirst()` in `GlamUpController`.)

**What it checks:** `FashionmateBackendApplicationTests.contextLoads()` boots the full
Spring application context using `application.properties` plus whatever `DB_USERNAME`,
`DB_PASSWORD`, and `GEMINI_API_KEY` environment variables are set (see root README for
setup).

**What a passing result tells you:** every `@Component`/`@RestController`/`@Repository`
bean wires up correctly, the JPA entity mappings are valid, and the app can reach the
configured MySQL database. It is a coarse smoke test, not behavioral coverage — see
`FEATURES.md` item 4 for the planned expansion into real request-level tests
(`@WebMvcTest`, `@DataJpaTest`) for the controllers.

## Answering the course's two required questions

- **What command runs the check?** `npm run lint` (frontend) and `./mvnw test`
  (backend) — see above for working directories and prerequisites.
- **What does a passing result tell you?** Frontend: the React code has no lint-level
  bugs or unsafe hook usage. Backend: the Spring Boot app starts cleanly with the
  current configuration and entity mappings.
