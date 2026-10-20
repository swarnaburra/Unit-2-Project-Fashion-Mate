package fashionmate_backend.exceptions;

/**
 * Thrown when a user id in the request path cannot be parsed as a valid id.
 * Mapped to HTTP 400 by {@link GlobalExceptionHandler}.
 */
public class InvalidUserIdException extends RuntimeException {

    public InvalidUserIdException(String message) {
        super(message);
    }
}
