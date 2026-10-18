package fashionmate_backend.exceptions;

/**
 * Thrown when a user id in the request path does not match any user.
 * Mapped to HTTP 404 by {@link GlobalExceptionHandler}.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
