---
classification: internal
project: proj-lessons
doc_type: lesson
---

## Lesson: deleteById does not throw EmptyResultDataAccessException in Spring Data 3

What happened: a review agent reported that deleting a missing review crashed with an `EmptyResultDataAccessException` and a 500 error, ranked it as a top bug, and carried the claim through two context boundaries and a session summary into the final report.

What we learned: FashionMate uses Spring Boot 3.4.3, which ships Spring Data JPA 3.x. Since Spring Data 3.0, `CrudRepository.deleteById` silently ignores an id that does not exist; the old exception belongs to Spring Data 2.x. The claim came from outdated framework knowledge, not from the code.

How to apply it: verify framework behavior against the version in `pom.xml` before ranking a bug on it. To report a missing entity, look it up first (for example `findByIdAndUserId`) and throw an explicit not-found exception, as `ReviewController.deleteReview` now does.
