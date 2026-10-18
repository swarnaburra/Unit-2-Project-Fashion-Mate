package fashionmate_backend.exceptions;

/**
 * Thrown when no user matches the given email/password pair. The same
 * exception and message are used for an unknown email and a wrong password,
 * so callers cannot tell which accounts exist.
 * Mapped to HTTP 401 by {@link GlobalExceptionHandler}.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
