package fashionmate_backend.exceptions;

/**
 * Thrown when no GlamUp content exists in the data store.
 */
public class GlamUpNotFoundException extends RuntimeException {

    public GlamUpNotFoundException(String message) {
        super(message);
    }
}
