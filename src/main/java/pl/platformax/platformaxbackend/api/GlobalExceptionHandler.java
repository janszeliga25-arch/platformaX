package pl.platformax.platformaxbackend.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.platformax.platformaxbackend.api.org.activity.MissingOrganizationContextException;
import pl.platformax.platformaxbackend.domain.account.EmailAlreadyUsedException;
import pl.platformax.platformaxbackend.domain.account.InvalidCredentialsException;
import pl.platformax.platformaxbackend.domain.account.KrsAlreadyUsedException;
import pl.platformax.platformaxbackend.domain.activity.ActivityCannotBePublishedException;
import pl.platformax.platformaxbackend.domain.activity.ActivityNotFoundException;
import pl.platformax.platformaxbackend.domain.activity.OrganizationNotVerifiedException;
import pl.platformax.platformaxbackend.domain.org.OrganizationNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EmailAlreadyUsedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailAlreadyUsed(EmailAlreadyUsedException e) {
        log.warn("Email already used: {}", e.getMessage());
        return new ErrorResponse("EMAIL_ALREADY_USED");
    }

    @ExceptionHandler(KrsAlreadyUsedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleKrsAlreadyUsed(KrsAlreadyUsedException e) {
        log.warn("KRS already used: {}", e.getMessage());
        return new ErrorResponse("KRS_ALREADY_USED");
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInvalidCredentials(InvalidCredentialsException e) {
        log.warn("Invalid credentials: {}", e.getMessage());
        return new ErrorResponse("INVALID_CREDENTIALS");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException e) {
        log.warn("Validation error: {}", e.getMessage());
        return new ErrorResponse("VALIDATION_ERROR");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotReadable(HttpMessageNotReadableException e) {
        log.warn("Unreadable request: {}", e.getMessage());
        return new ErrorResponse("VALIDATION_ERROR");
    }

    @ExceptionHandler(OrganizationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleOrganizationNotFound(OrganizationNotFoundException e) {
        log.warn("Organization not found: {}", e.getMessage());
        return new ErrorResponse("ORGANIZATION_NOT_FOUND");
    }

    @ExceptionHandler(OrganizationNotVerifiedException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleOrganizationNotVerified(OrganizationNotVerifiedException e) {
        log.warn("Organization not verified: {}", e.getMessage());
        return new ErrorResponse("ORGANIZATION_NOT_VERIFIED");
    }

    @ExceptionHandler(ActivityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleActivityNotFound(ActivityNotFoundException e) {
        log.warn("Activity not found: {}", e.getMessage());
        return new ErrorResponse("ACTIVITY_NOT_FOUND");
    }

    @ExceptionHandler(ActivityCannotBePublishedException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleActivityCannotBePublished(ActivityCannotBePublishedException e) {
        log.warn("Activity cannot be published: {}", e.getMessage());
        return new ErrorResponse("ACTIVITY_CANNOT_BE_PUBLISHED");
    }

    @ExceptionHandler(MissingOrganizationContextException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleMissingOrganizationContext(MissingOrganizationContextException e) {
        log.error("Missing organization context: {}", e.getMessage());
        return new ErrorResponse("ORG_CONTEXT_MISSING");
    }
}
