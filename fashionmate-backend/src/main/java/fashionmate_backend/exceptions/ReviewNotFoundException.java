package fashionmate_backend.exceptions;

/**
 * Thrown when a review does not exist, or exists but does not belong to the
 * requesting user. Both cases are treated identically so that callers cannot
 * distinguish "not found" from "not yours" and probe for valid review ids.
 */
public class ReviewNotFoundException extends RuntimeException {

    public ReviewNotFoundException(String message) {
        super(message);
    }
}
