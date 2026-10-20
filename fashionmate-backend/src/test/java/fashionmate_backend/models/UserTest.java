package fashionmate_backend.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void gettersAndSettersRoundTripAllFields() {
        User user = new User();

        user.setId(1L);
        user.setName("Jamie");
        user.setEmail("jamie@example.com");
        user.setPassword("secret");

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("Jamie");
        assertThat(user.getEmail()).isEqualTo("jamie@example.com");
        assertThat(user.getPassword()).isEqualTo("secret");
    }

    @Test
    void serializingUserOmitsPassword() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Jamie");
        user.setEmail("jamie@example.com");
        user.setPassword("secret");

        String json = new ObjectMapper().writeValueAsString(user);

        assertThat(json).doesNotContain("password").doesNotContain("secret");
    }

    @Test
    void deserializingUserStillSetsPassword() throws Exception {
        String json = "{\"id\":1,\"name\":\"Jamie\",\"email\":\"jamie@example.com\",\"password\":\"secret\"}";

        User user = new ObjectMapper().readValue(json, User.class);

        assertThat(user.getPassword()).isEqualTo("secret");
    }
}
