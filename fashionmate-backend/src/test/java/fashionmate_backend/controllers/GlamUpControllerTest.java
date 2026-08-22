package fashionmate_backend.controllers;

import fashionmate_backend.models.GlamUp;
import fashionmate_backend.repositories.GlamUpRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlamUpControllerTest {

    @Mock
    private GlamUpRepository glamUpRepository;

    @InjectMocks
    private GlamUpController glamUpController;

    @Test
    void getGlamUpByReturnsFirstResultFromRepository() {
        GlamUp first = new GlamUp();
        first.setTrendingStyle("Cottagecore");
        GlamUp second = new GlamUp();
        second.setTrendingStyle("Streetwear");
        when(glamUpRepository.findAll()).thenReturn(List.of(first, second));

        GlamUp result = glamUpController.getGlamUpBy();

        assertThat(result.getTrendingStyle()).isEqualTo("Cottagecore");
    }

    @Test
    void getGlamUpByThrowsWhenNoGlamUpsExist() {
        when(glamUpRepository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> glamUpController.getGlamUpBy())
                .isInstanceOf(NoSuchElementException.class);
    }
}
