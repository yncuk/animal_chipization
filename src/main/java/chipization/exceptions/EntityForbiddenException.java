package chipization.exceptions;

public class EntityForbiddenException extends RuntimeException {
    public EntityForbiddenException(final String message) {
        super(message);
    }
}
