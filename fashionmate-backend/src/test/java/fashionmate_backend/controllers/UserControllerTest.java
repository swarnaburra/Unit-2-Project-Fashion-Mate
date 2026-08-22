package fashionmate_backend.controllers;

import fashionmate_backend.models.User;
import fashionmate_backend.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    @Test
    void createUserSavesAndReturnsUser() {
        User user = new User();
        user.setName("Jamie");
        when(userRepository.save(user)).thenReturn(user);

        User result = userController.createUser(user);

        assertThat(result).isEqualTo(user);
    }

    @Test
    void signUpThrowsWhenFieldsMissing() {
        User request = new User();
        request.setEmail("jamie@example.com");

        assertThatThrownBy(() -> userController.signUp(request))
                .isInstanceOf(Exception.class)
                .hasMessage("Add the missing fields");
    }

    @Test
    void signUpThrowsWhenEmailAlreadyUsed() {
        User request = new User();
        request.setName("Jamie");
        request.setEmail("jamie@example.com");
        request.setPassword("secret");

        when(userRepository.existsByEmail("jamie@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userController.signUp(request))
                .isInstanceOf(Exception.class)
                .hasMessage("Email already in use");
    }

    @Test
    void signUpSavesNewUserAndReturnsId() throws Exception {
        User request = new User();
        request.setName("Jamie");
        request.setEmail("jamie@example.com");
        request.setPassword("secret");

        User saved = new User();
        saved.setId(7L);

        when(userRepository.existsByEmail("jamie@example.com")).thenReturn(false);
        when(userRepository.save(request)).thenReturn(saved);

        Long id = userController.signUp(request);

        assertThat(id).isEqualTo(7L);
    }

    @Test
    void signInThrowsWhenCredentialsMissing() {
        User request = new User();
        request.setEmail("jamie@example.com");

        assertThatThrownBy(() -> userController.signIn(request))
                .isInstanceOf(Exception.class)
                .hasMessage("Please enter your correct email and password");
    }

    @Test
    void signInThrowsWhenUserNotFound() {
        User request = new User();
        request.setEmail("jamie@example.com");
        request.setPassword("wrong");

        when(userRepository.findByEmailAndPassword("jamie@example.com", "wrong")).thenReturn(null);

        assertThatThrownBy(() -> userController.signIn(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }

    @Test
    void signInReturnsIdWhenCredentialsMatch() throws Exception {
        User request = new User();
        request.setEmail("jamie@example.com");
        request.setPassword("secret");

        User found = new User();
        found.setId(8L);

        when(userRepository.findByEmailAndPassword("jamie@example.com", "secret")).thenReturn(found);

        Long id = userController.signIn(request);

        assertThat(id).isEqualTo(8L);
    }
}
