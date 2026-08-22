package fashionmate_backend.controllers;

import fashionmate_backend.models.ImageRequest;
import fashionmate_backend.repositories.StyleLensRepository;
import fashionmate_backend.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StyleLensControllerTest {

    @Mock
    private StyleLensRepository styleLensRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StyleLensController styleLensController;

    @Test
    void processImageThrowsAndSkipsSaveWhenUserMissing() {
        when(userRepository.findById(42L)).thenReturn(Optional.empty());

        ImageRequest request = new ImageRequest();
        request.setOccasion("wedding");

        assertThatThrownBy(() -> styleLensController.processImage("42", request))
                .isInstanceOf(Exception.class)
                .hasMessage("Wrong user");

        verify(styleLensRepository, never()).save(any());
    }
}
