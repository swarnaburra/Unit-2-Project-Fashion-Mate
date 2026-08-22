package fashionmate_backend.models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GlamUpTest {

    @Test
    void gettersAndSettersRoundTripAllFields() {
        GlamUp glamUp = new GlamUp();

        glamUp.setId(1L);
        glamUp.setTrendingStyle("Cottagecore");
        glamUp.setImageUrl1("https://example.com/1.jpg");
        glamUp.setAltText1("Look 1");
        glamUp.setImageUrl2("https://example.com/2.jpg");
        glamUp.setAltText2("Look 2");
        glamUp.setImageUrl3("https://example.com/3.jpg");
        glamUp.setAltText3("Look 3");

        assertThat(glamUp.getId()).isEqualTo(1L);
        assertThat(glamUp.getTrendingStyle()).isEqualTo("Cottagecore");
        assertThat(glamUp.getImageUrl1()).isEqualTo("https://example.com/1.jpg");
        assertThat(glamUp.getAltText1()).isEqualTo("Look 1");
        assertThat(glamUp.getImageUrl2()).isEqualTo("https://example.com/2.jpg");
        assertThat(glamUp.getAltText2()).isEqualTo("Look 2");
        assertThat(glamUp.getImageUrl3()).isEqualTo("https://example.com/3.jpg");
        assertThat(glamUp.getAltText3()).isEqualTo("Look 3");
    }
}
