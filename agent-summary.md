# Repository Summary

- **Backend** (`fashionmate-backend/`): Java Spring Boot application built with Maven (`pom.xml`, `mvnw`/`mvnw.cmd`). Source under `src/main/java/fashionmate_backend/`, organized into `controllers/`, `models/`, and `repositories/` packages (e.g. `UserController`, `ReviewRepository`, `GlamUp` model). Tests live under `src/test/java/fashionmate_backend/`.
- **Frontend** (`fashionmate-frontend/fashion-app/`): React application built with Vite and managed via npm (`package.json`, `package-lock.json`). Source under `src/`, split into `components/` (e.g. `FashionQuiz.jsx`, `GlamUp.css`, `Footer.jsx`), `context/`, plus `App.jsx`/`main.jsx` entry points and static `assets/`.
- Uses `@google/genai` / `@google/generative-ai` on the frontend, `react-router-dom` for routing, and ESLint (`eslint.config` via `eslint .`) for linting.

## Lint Result

Command: `cd fashionmate-frontend/fashion-app && npm run lint` (runs `eslint .`, no `--fix`, check-only)

- **Result: PASS**
- **Errors: 0**
- **Warnings: 0**
- Exit code: 0
