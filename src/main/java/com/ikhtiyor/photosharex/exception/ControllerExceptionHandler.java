package com.ikhtiyor.photosharex.exception;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {


    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<CustomErrorMessage> handleFlightNotFound(
        UsernameNotFoundException ex) {

        CustomErrorMessage body = new CustomErrorMessage(
            LocalDateTime.now(),
            ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustomErrorMessage> handleAccessDeniedException(
        AccessDeniedException ex) {
        CustomErrorMessage body = new CustomErrorMessage(
            LocalDateTime.now(),
            ex.getMessage()
        );

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CustomErrorMessage> handleBadCredential(
        BadCredentialsException ex
    ) {
        CustomErrorMessage body = new CustomErrorMessage(
            LocalDateTime.now(),
            ex.getMessage()
        );

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CustomErrorMessage> handleBadCredential(
        AuthenticationException ex
    ) {
        CustomErrorMessage body = new CustomErrorMessage(
            LocalDateTime.now(),
            ex.getMessage()
        );

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidAccessTokenException.class)
    public ResponseEntity<CustomErrorMessage> handleJwtExpiredException(
        InvalidAccessTokenException ex) {
        CustomErrorMessage body = new CustomErrorMessage(
            LocalDateTime.now(),
            ex.getMessage()
        );

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorMessage> handleValidationException(
        MethodArgumentNotValidException ex
    ) {
        BindingResult result = ex.getBindingResult();
        final List<FieldError> fieldErrors = result.getFieldErrors();
        CustomErrorMessage body = new CustomErrorMessage(
            LocalDateTime.now(),
            Arrays.toString(fieldErrors.toArray(new FieldError[0]))
        );

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
