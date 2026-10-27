---
classification: internal
project: proj-lessons
doc_type: lesson
---

## Lesson: check the React frontend before changing a response shape

What happened: before changing error status codes and hiding the password, we checked what the React app actually reads. `UserContext.jsx` treats any non-OK signup or signin response as a generic failure and stores only the numeric user id from the success body. `About.jsx` reads only `id`, `name`, `rating`, and `comment` from each review and never the embedded user.

What we learned: the frontend contract is narrower than the backend's JSON, so many backend changes are safe, but the safe set lives in `fashionmate-frontend/fashion-app/src`, not in the backend repo. `POST /api/users` is not called by the frontend at all.

How to apply it: before changing a controller's request or response, read the matching `fetch` call in the frontend and keep the fields and statuses it relies on. If both sides must change, plan them in the same task.
