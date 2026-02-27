package com.digitalmoneyhouse.account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorBody> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorBody(404, ex.getMessage()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorBody> handleForbidden(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorBody(403, ex.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorBody> handleConflict(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorBody(409, ex.getMessage()));
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorBody> handleInsufficient(InsufficientFundsException ex) {
        return ResponseEntity.status(HttpStatus.GONE).body(new ErrorBody(410, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorBody> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b).orElse("Validacion fallida");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorBody(400, msg));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorBody> handleIllegalArg(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorBody(400, ex.getMessage()));
    }

    @ExceptionHandler(InvalidCardException.class)
    public ResponseEntity<ErrorBody> handleInvalidCard(InvalidCardException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorBody(400, ex.getMessage()));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorBody> handleTokenExpired(TokenExpiredException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorBody(401, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorBody> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorBody(500, "Error interno"));
    }

    public record ErrorBody(int status, String message) {}
}
