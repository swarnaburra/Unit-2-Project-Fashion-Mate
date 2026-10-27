---
classification: internal
project: proj-lessons
doc_type: lesson
---

## Lesson: someone else's review is "not found", never "forbidden"

What happened: `ReviewController.deleteReview` checked only that the user existed and then deleted any review id it was given, so any caller could delete another user's review by guessing ids.

What we learned: the fix is a scoped lookup, `ReviewRepository.findByIdAndUserId(reviewId, userId)`, followed by `delete(review)` on the loaded entity. When the lookup is empty the controller throws `ReviewNotFoundException`, which returns 404 with the same message whether the review does not exist or belongs to another user. Returning 403 was rejected because it confirms the review exists and lets callers probe valid ids.

How to apply it: any endpoint acting on a resource owned by a user must load it scoped to that user and answer 404 for both "missing" and "not yours". Test both cases separately and assert that nothing is deleted.
