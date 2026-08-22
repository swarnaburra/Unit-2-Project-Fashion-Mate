package fashionmate_backend.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StyleLensTest {

    @Test
    void gettersAndSettersRoundTripAllFields() {
        StyleLens styleLens = new StyleLens();
        User user = new User();
        user.setId(2L);

        styleLens.setId(1L);
        styleLens.setDecision("YAY");
        styleLens.setImage("base64-image");
        styleLens.setUser(user);

        assertThat(styleLens.getId()).isEqualTo(1L);
        assertThat(styleLens.getDecision()).isEqualTo("YAY");
        assertThat(styleLens.getImage()).isEqualTo("base64-image");
        assertThat(styleLens.getUser()).isEqualTo(user);
    }
}
