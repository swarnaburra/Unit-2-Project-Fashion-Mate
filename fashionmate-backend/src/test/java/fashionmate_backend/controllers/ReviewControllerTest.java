package fashionmate_backend.controllers;

import fashionmate_backend.models.Review;
import fashionmate_backend.models.User;
import fashionmate_backend.repositories.ReviewRepository;
import fashionmate_backend.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewController reviewController;

    @Test
    void getReviewByIdReturnsReviewsForExistingUser() {
        User user = new User();
        user.setId(1L);
        Review review = new Review();
        review.setName("Jamie");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.findAllByUserId(1L)).thenReturn(List.of(review));

        List<Review> reviews = reviewController.getReviewById(1L);

        assertThat(reviews).containsExactly(review);
    }

    @Test
    void getReviewByIdThrowsWhenUserMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewController.getReviewById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User does not exist");
    }

    @Test
    void createReviewAttachesUserAndSaves() {
        User user = new User();
        user.setId(2L);
        Review review = new Review();

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(reviewRepository.save(review)).thenReturn(review);

        Review saved = reviewController.createReview(2L, review);

        assertThat(saved.getUser()).isEqualTo(user);
        verify(reviewRepository).save(review);
    }

    @Test
    void createReviewThrowsWhenUserMissingAndDoesNotSave() {
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewController.createReview(3L, new Review()))
                .isInstanceOf(RuntimeException.class);

        verify(reviewRepository, never()).save(any());
    }

    @Test
    void deleteReviewDeletesWhenUserExists() {
        when(userRepository.findById(4L)).thenReturn(Optional.of(new User()));

        reviewController.deleteReview(4L, 10L);

        verify(reviewRepository).deleteById(10L);
    }

    @Test
    void deleteReviewThrowsWhenUserMissingAndDoesNotDelete() {
        when(userRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewController.deleteReview(5L, 10L))
                .isInstanceOf(RuntimeException.class);

        verify(reviewRepository, never()).deleteById(any());
    }
}
