# FashionMate — Frontend (fashion-app)

React + Vite single-page app for FashionMate. Talks to the Spring Boot backend in
`fashionmate-backend` (see the root `README.md` for backend setup).

## Running locally

```bash
npm install
npm run dev      # start the Vite dev server
npm run lint      # run ESLint (see ../../TESTING.md)
npm run build     # production build
```

The app expects the backend at `http://localhost:8080` — this URL is currently
hardcoded in several components (`UserContext.jsx`, `About.jsx`, `GlamUp.jsx`,
`OutfitTip.jsx`, `StyleLensUpload.jsx`) rather than read from an env var; see the root
`FEATURES.md` item 3 for the planned fix.

## Routes

- `/` — Home (fashion tip + get-started CTA)
- `/signup`, `/login` — auth screens (email/password, no JWT yet)
- `/stylelens` — upload an outfit photo for Gemini-powered YAY/NAY feedback (protected)
- `/fashionquiz` — client-side style/color quiz, no backend call (protected)
- `/glamup` — trending style + images (protected)
- `/about` — team info, feature list, and the review form (protected)

"Protected" routes redirect to `/signup` if no user is logged in (see
`src/components/ProtectedRoute.jsx`). Auth state is just a numeric user ID kept in
`localStorage` via `src/context/UserContext.jsx` — there is no token/session yet.

---

# React + Vite

This template provides a minimal setup to get React working in Vite with HMR and some ESLint rules.

Currently, two official plugins are available:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react) uses [Babel](https://babeljs.io/) (or [oxc](https://oxc.rs) when used in [rolldown-vite](https://vite.dev/guide/rolldown)) for Fast Refresh
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc) uses [SWC](https://swc.rs/) for Fast Refresh

## React Compiler

The React Compiler is not enabled on this template because of its impact on dev & build performances. To add it, see [this documentation](https://react.dev/learn/react-compiler/installation).

## Expanding the ESLint configuration

If you are developing a production application, we recommend using TypeScript with type-aware lint rules enabled. Check out the [TS template](https://github.com/vitejs/vite/tree/main/packages/create-vite/template-react-ts) for information on how to integrate TypeScript and [`typescript-eslint`](https://typescript-eslint.io) in your project.
