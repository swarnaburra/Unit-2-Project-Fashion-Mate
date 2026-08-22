# Parallel Agent Session Tasks

## Session A

Branch name: `session-a-controller-docstrings`
Worktree directory: `../fashionmate-session-a`
Task: Add Javadoc comments to every public method in the backend controller classes
(`fashionmate-backend/src/main/java/fashionmate_backend/controllers/`), explaining what
each endpoint does, its parameters, and its return/error behavior. Do not change any
logic, method signatures, or behavior -- comments only.
Files or folders the agent may write to: `fashionmate-backend/src/main/java/fashionmate_backend/controllers/**`
Files or folders the agent may read but not write to: the rest of the repository (models,
repositories, frontend, docs, tests, build files) -- read for context only, needed to
describe each endpoint's behavior accurately.
Commands the agent may run: `mvn compile` (from `fashionmate-backend/`), to confirm the
added comments don't break compilation. No other commands.
Definition of done: every public method in every controller class has a Javadoc comment;
`mvn compile` succeeds; `git diff` shows only comment additions, no logic/signature
changes.

## Session B

Branch name: `session-b-form-error-messages`
Worktree directory: `../fashionmate-session-b`
Task: Improve the user-facing error and validation messages shown in the Login and
Signup forms (e.g. empty fields, invalid email, wrong password, duplicate email) so
they're specific and clear, instead of generic. Do not change form logic/behavior beyond
the message text and whatever minimal conditional is needed to select the right message.
Files or folders the agent may write to:
`fashionmate-frontend/fashion-app/src/components/Login.jsx`,
`fashionmate-frontend/fashion-app/src/components/Signup.jsx`, and their paired `.css`
files if a small style tweak to the error text is genuinely needed.
Files or folders the agent may read but not write to: the rest of the repository
(backend controllers this hits, `UserContext.jsx`, docs, etc.) -- read for context only.
Commands the agent may run: `npm run lint` (check-only, no `--fix`) from
`fashionmate-frontend/fashion-app/`, to confirm no lint regressions. No other commands.
Definition of done: error/validation messages in both forms are specific and
user-friendly; `npm run lint` passes with 0 errors/warnings; no functional behavior
changes beyond message text; `git diff` touches only the four files listed above.
