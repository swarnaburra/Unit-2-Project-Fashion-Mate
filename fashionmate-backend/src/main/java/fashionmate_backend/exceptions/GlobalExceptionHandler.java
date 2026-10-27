package fashionmate_backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * The single place where application exceptions become HTTP responses.
 * Every error body has the shape {@code {"message": "..."}}.
 * There is deliberately no catch-all handler for {@link Exception}, so Spring's
 * own 4xx responses (for example malformed JSON) are not turned into 500s.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleReviewNotFound(ReviewNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(UserNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(GlamUpNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleGlamUpNotFound(GlamUpNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(MissingFieldsException.class)
    public ResponseEntity<Map<String, String>> handleMissingFields(MissingFieldsException ex) {
        return error(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(InvalidUserIdException.class)
    public ResponseEntity<Map<String, String>> handleInvalidUserId(InvalidUserIdException ex) {
        return error(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyInUse(EmailAlreadyInUseException ex) {
        return error(HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return error(HttpStatus.UNAUTHORIZED, ex);
    }

    private ResponseEntity<Map<String, String>> error(HttpStatus status, RuntimeException ex) {
        return ResponseEntity.status(status).body(Map.of("message", ex.getMessage()));
    }
}
