package com.eshop.client.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<Map<String, Object>> err(HttpStatus status, String message, String code ) {
        return ResponseEntity.status(status).body(Map.of(
           "error", message,
           "code", code,
           "status", status,
           "timestamp", Instant.now().toString()
        ));
    }

    @ExceptionHandler({
            ProductNotFoundException.class,
            CategoryNotFoundException.class
    })
    public ResponseEntity<?> notFound(RuntimeException ex) {
        return err(HttpStatus.NOT_FOUND, ex.getMessage(), "NOT_FOUND");
    }

    @ExceptionHandler({
            ShoppingCartException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<?> badReq(RuntimeException ex) {
        return err(HttpStatus.BAD_REQUEST, ex.getMessage(), "BAD_REQUEST");
    }

    @ExceptionHandler({
            CustomerNotFoundException.class,
            org.springframework.security.access.AccessDeniedException.class
    })
    public ResponseEntity<?> unauthorized(RuntimeException ex) {
        return err(HttpStatus.UNAUTHORIZED, ex.getMessage(), "UNAUTHORIZED");
    }

//    @ExceptionHandler(PaypalAPIException.class)
//    public ResponseEntity<?> paypal(PaypalAPIException ex) {
//        return err(HttpStatus.BAD_GATEWAY, ex.getMessage(), "PAYMENT_UPSTREAM");
//    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> server(Exception ex) {
        return err(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error", "SERVER_ERROR");
    }
}
