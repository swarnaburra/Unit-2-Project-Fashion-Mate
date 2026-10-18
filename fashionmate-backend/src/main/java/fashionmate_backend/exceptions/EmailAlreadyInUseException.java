package fashionmate_backend.exceptions;

/**
 * Thrown when signing up with an email that is already registered.
 * Mapped to HTTP 409 by {@link GlobalExceptionHandler}.
 */
public class EmailAlreadyInUseException extends RuntimeException {

    public EmailAlreadyInUseException(String message) {
        super(message);
    }
}
