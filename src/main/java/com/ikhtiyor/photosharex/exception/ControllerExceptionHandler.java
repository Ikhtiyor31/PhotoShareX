package com.ikhtiyor.photosharex.exception;


import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
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

    private final Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<CustomErrorMessage> handleUserNotFoundException(
        UsernameNotFoundException ex
    ) {
        final var errorResponse = CustomErrorMessage.of(HttpStatus.NOT_FOUND, ex.getMessage());
        LOGGER.error("Not found: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustomErrorMessage> handleAccessDeniedException(
        AccessDeniedException ex
    ) {
        final var errorResponse = CustomErrorMessage.of(HttpStatus.FORBIDDEN, ex.getMessage());
        LOGGER.error("Access denied: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CustomErrorMessage> handleBadCredential(
        BadCredentialsException ex
    ) {
        final var errorResponse = CustomErrorMessage.of(HttpStatus.FORBIDDEN, ex.getMessage());
        LOGGER.error("Bad credentials: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CustomErrorMessage> handleAuthenticationException(
        AuthenticationException ex
    ) {
        final var errorResponse = CustomErrorMessage.of(HttpStatus.UNAUTHORIZED, ex.getMessage());
        LOGGER.error("Authentication failed: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidAccessTokenException.class)
    public ResponseEntity<CustomErrorMessage> handleInvalidAccessTokenException(
        InvalidAccessTokenException ex
    ) {
        final var errorResponse = CustomErrorMessage.of(HttpStatus.UNAUTHORIZED, ex.getMessage());
        LOGGER.error("Invalid exception: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorMessage> handleValidationException(
        MethodArgumentNotValidException ex
    ) {
        BindingResult result = ex.getBindingResult();
        final List<FieldError> fieldErrors = result.getFieldErrors();
        final var errorResponse = CustomErrorMessage.of(
            HttpStatus.BAD_REQUEST,
            fieldErrors.get(0).getDefaultMessage()
        );
        LOGGER.error("Validation failed: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({IllegalArgumentException.class, GCPStorageException.class,
        InvalidImageException.class, ResourceNotFoundException.class})
    public ResponseEntity<CustomErrorMessage> handleGenericException(
        RuntimeException ex
    ) {
        final var errorResponse = CustomErrorMessage.of(HttpStatus.BAD_REQUEST, ex.getMessage());
        LOGGER.error("An error occurred: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CustomErrorMessage> handleConstraintViolationException(ConstraintViolationException ex) {
        final var errorResponse = CustomErrorMessage.of(HttpStatus.BAD_REQUEST, ex.getMessage());
        LOGGER.error("Constraint violation: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CustomErrorMessage> handleDataIntegrityViolationException(
        DataIntegrityViolationException ex
    ) {
        final var errorResponse = CustomErrorMessage.of(HttpStatus.CONFLICT, ex.getMessage());
        LOGGER.error("Failed to save data: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
}
