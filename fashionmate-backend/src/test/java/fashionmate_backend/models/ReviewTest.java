package fashionmate_backend.models;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Test
    void serializingReviewOmitsReviewersPassword() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Jamie");
        user.setEmail("jamie@example.com");
        user.setPassword("secret");

        Review review = new Review();
        review.setId(1L);
        review.setName("Jamie");
        review.setComment("Loved the fit");
        review.setRating(5);
        review.setUser(user);

        String json = new ObjectMapper().writeValueAsString(review);

        assertThat(json).doesNotContain("password").doesNotContain("secret");
    }
}
