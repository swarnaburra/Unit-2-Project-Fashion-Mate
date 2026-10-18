package fashionmate_backend.exceptions;

/**
 * Thrown when a required request field (name, email, or password) is missing.
 * Mapped to HTTP 400 by {@link GlobalExceptionHandler}.
 */
public class MissingFieldsException extends RuntimeException {

    public MissingFieldsException(String message) {
        super(message);
    }
}
