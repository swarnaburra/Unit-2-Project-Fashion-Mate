package fashionmate_backend.models;

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
}
