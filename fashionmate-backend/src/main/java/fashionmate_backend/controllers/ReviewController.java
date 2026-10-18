package fashionmate_backend.controllers;

import fashionmate_backend.exceptions.ReviewNotFoundException;
import fashionmate_backend.exceptions.UserNotFoundException;
import fashionmate_backend.models.Review;
import fashionmate_backend.models.User;
import fashionmate_backend.repositories.ReviewRepository;
import fashionmate_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Manages user-submitted {@link Review}s.
 */
@RestController
@RequestMapping("/api/reviews")
@CrossOrigin
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Handles {@code GET /api/reviews/{userId}}.
     * <p>
     * Looks up all reviews belonging to the given user.
     *
     * @param userId the id of the user whose reviews should be returned
     * @return the list of {@link Review} entities belonging to the user (may be empty)
     * @throws UserNotFoundException if no user exists with the given {@code userId} (404)
     */
    @GetMapping("/{userId}")
    public List<Review> getReviewById(@PathVariable Long userId) {
        checkUserExist(userId);
        return reviewRepository.findAllByUserId(userId);
    }


    /**
     * Handles {@code POST /api/reviews/{userId}}.
     * <p>
     * Creates a new review associated with the given user.
     *
     * @param userId the id of the user the review is being created for
     * @param review the review payload from the request body (id is ignored/generated)
     * @return the saved {@link Review}, including its generated id and the associated user
     * @throws UserNotFoundException if no user exists with the given {@code userId} (404)
     */
    @PostMapping("/{userId}")
    public Review createReview(@PathVariable Long userId, @RequestBody Review review) {
        final User user = checkUserExist(userId);
        review.setUser(user);
        return reviewRepository.save(review);
    }


    /**
     * Handles {@code DELETE /api/reviews/{userId}/review/{reviewId}}.
     * <p>
     * Deletes the review identified by {@code reviewId}, provided it belongs to
     * {@code userId}.
     *
     * @param userId the id of the user the review must belong to
     * @param reviewId the id of the review to delete
     * @throws UserNotFoundException if no user exists with the given {@code userId} (404)
     * @throws ReviewNotFoundException if no review with {@code reviewId} belongs to {@code userId}
     */
    @DeleteMapping("/{userId}/review/{reviewId}")
    public void deleteReview(@PathVariable Long userId, @PathVariable Long reviewId) {
        checkUserExist(userId);
        final Review review = reviewRepository.findByIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));
        reviewRepository.delete(review);
    }

    private User checkUserExist(Long userId) {
        final Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }
        return user.get();
    }
}
