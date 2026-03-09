package fashionmate_backend.controllers;

import fashionmate_backend.models.Review;
import fashionmate_backend.models.User;
import fashionmate_backend.repositories.ReviewRepository;
import fashionmate_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{userId}")
    public List<Review> getReviewById(@PathVariable Long userId) {
        checkUserExist(userId);
        return reviewRepository.findAllByUserId(userId);
    }


    @PostMapping("/{userId}")
    public Review createReview(@PathVariable Long userId, @RequestBody Review review) {
        final User user = checkUserExist(userId);
        review.setUser(user);
        return reviewRepository.save(review);
    }


    @DeleteMapping("/{userId}/review/{reviewId}")
    public void deleteReview(@PathVariable Long userId, @PathVariable Long reviewId) {
        checkUserExist(userId);
        reviewRepository.deleteById(reviewId);
    }

    private User checkUserExist(Long userId) {
        final Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("User does not exist");
        }
        return user.get();
    }
}
