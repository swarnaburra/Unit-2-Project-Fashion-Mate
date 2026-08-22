package fashionmate_backend.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImageRequestTest {

    @Test
    void gettersAndSettersRoundTripAllFields() {
        ImageRequest request = new ImageRequest();

        request.setContents("base64-data");
        request.setMimetype("image/png");
        request.setOccasion("wedding");
        request.setAge("30");
        request.setPreference("casual");

        assertThat(request.getContents()).isEqualTo("base64-data");
        assertThat(request.getMimetype()).isEqualTo("image/png");
        assertThat(request.getOccasion()).isEqualTo("wedding");
        assertThat(request.getAge()).isEqualTo("30");
        assertThat(request.getPreference()).isEqualTo("casual");
    }
}
