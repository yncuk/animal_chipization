package chipization.exceptions.handler;

import chipization.exceptions.EntityBadRequestException;
import chipization.exceptions.EntityForbiddenException;
import chipization.exceptions.EntityNotAuthorizedException;
import chipization.exceptions.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final EntityBadRequestException e) {
        log.debug("Ошибка 400, сообщение об ошибке: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleNotAuthorized(final EntityNotAuthorizedException e) {
        log.debug("Ошибка 401, сообщение об ошибке: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbidden(final EntityForbiddenException e) {
        log.debug("Ошибка 403, сообщение об ошибке: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final EntityNotFoundException e) {
        log.debug("Ошибка 404, сообщение об ошибке: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(final DataIntegrityViolationException e) {
        log.debug("Ошибка 409, сообщение об ошибке: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }



}
