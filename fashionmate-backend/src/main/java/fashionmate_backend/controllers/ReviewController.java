package fashionmate_backend.controllers;

import fashionmate_backend.models.Review;
import fashionmate_backend.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;


    @GetMapping("/{id}")
    public Optional<Review> getReviewById(@PathVariable Long id) {
        return reviewRepository.findById(id);
    }


    @PostMapping
    public Review createReview(@RequestBody Review review) {
        return reviewRepository.save(review);
    }

    // Update an existing review
   @PutMapping("/{id}")
   public Review updateReview(@PathVariable Long id, @RequestBody Review reviewDetails){
        Review review = reviewRepository.findById(id).orElse(null);

        if(review == null){
            throw new RuntimeException("Review not found");
        }

        return reviewRepository.save(review);
   }



    // Delete a review
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewRepository.deleteById(id);
    }
}
