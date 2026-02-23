package pl.platformax.platformaxbackend.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.platformax.platformaxbackend.domain.account.EmailAlreadyUsedException;
import pl.platformax.platformaxbackend.domain.account.KrsAlreadyUsedException;

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
}
