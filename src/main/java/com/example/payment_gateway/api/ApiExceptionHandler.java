package com.example.payment_gateway.api;

import com.example.payment_gateway.service.IdempotencyConflictException;
import com.example.payment_gateway.service.PaymentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;

// Centralizes API exception handling to ensure consistent error responses across controllers.
@RestControllerAdvice
public class ApiExceptionHandler {

    // Handles cases where a requested payment resource does not exist.
    @ExceptionHandler(PaymentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleNotFound(PaymentNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Resource not found");
        pd.setDetail(ex.getMessage());
        pd.setProperty("timestamp", Instant.now().toString());
        return pd;
    }

    // Handles validation errors triggered by invalid request payloads.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation error");
        pd.setDetail("One or more fields are invalid.");
        pd.setProperty("timestamp", Instant.now().toString());

        // Collects field-level validation errors to provide detailed client feedback.
        var errors = new HashMap<String, String>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

        pd.setProperty("errors", errors);
        return pd;
    }

    // Handles idempotency conflicts to prevent duplicate or inconsistent operations.
    @ExceptionHandler(IdempotencyConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleIdempotencyConflict(IdempotencyConflictException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Idempotency conflict");
        pd.setDetail(ex.getMessage());
        pd.setProperty("timestamp", Instant.now().toString());
        return pd;
    }

    // Handles invalid arguments that indicate a malformed or incorrect client request.
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleBadRequest(IllegalArgumentException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Bad request");
        pd.setDetail(ex.getMessage());
        pd.setProperty("timestamp", Instant.now().toString());
        return pd;
    }
}
