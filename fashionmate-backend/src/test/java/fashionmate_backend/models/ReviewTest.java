package fashionmate_backend.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewTest {

    @Test
    void gettersAndSettersRoundTripAllFields() {
        Review review = new Review();
        User user = new User();
        user.setId(1L);

        review.setId(1L);
        review.setName("Jamie");
        review.setComment("Loved the fit");
        review.setRating(5);
        review.setUser(user);

        assertThat(review.getId()).isEqualTo(1L);
        assertThat(review.getName()).isEqualTo("Jamie");
        assertThat(review.getComment()).isEqualTo("Loved the fit");
        assertThat(review.getRating()).isEqualTo(5);
        assertThat(review.getUser()).isEqualTo(user);
    }
}
