package chipization.exceptions;

public class EntityNotAuthorizedException extends RuntimeException {
    public EntityNotAuthorizedException(final String message) {
        super(message);
    }
}
