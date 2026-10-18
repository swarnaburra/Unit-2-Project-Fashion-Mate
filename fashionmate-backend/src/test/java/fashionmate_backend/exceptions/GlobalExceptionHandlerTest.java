package fashionmate_backend.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks the status code and {"message": ...} body for every mapped exception.
 * Plain unit test (no MockMvc), per the project's test conventions.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void reviewNotFoundMapsTo404WithMessageBody() {
        assertError(handler.handleReviewNotFound(new ReviewNotFoundException("Review not found")),
                HttpStatus.NOT_FOUND, "Review not found");
    }

    @Test
    void userNotFoundMapsTo404WithMessageBody() {
        assertError(handler.handleUserNotFound(new UserNotFoundException("User does not exist")),
                HttpStatus.NOT_FOUND, "User does not exist");
    }

    @Test
    void missingFieldsMapsTo400WithMessageBody() {
        assertError(handler.handleMissingFields(new MissingFieldsException("Add the missing fields")),
                HttpStatus.BAD_REQUEST, "Add the missing fields");
    }

    @Test
    void emailAlreadyInUseMapsTo409WithMessageBody() {
        assertError(handler.handleEmailAlreadyInUse(new EmailAlreadyInUseException("Email already in use")),
                HttpStatus.CONFLICT, "Email already in use");
    }

    @Test
    void invalidCredentialsMapsTo401WithMessageBody() {
        assertError(handler.handleInvalidCredentials(new InvalidCredentialsException("User not found")),
                HttpStatus.UNAUTHORIZED, "User not found");
    }

    private void assertError(ResponseEntity<Map<String, String>> response, HttpStatus status, String message) {
        assertThat(response.getStatusCode()).isEqualTo(status);
        assertThat(response.getBody()).containsExactly(Map.entry("message", message));
    }
}
