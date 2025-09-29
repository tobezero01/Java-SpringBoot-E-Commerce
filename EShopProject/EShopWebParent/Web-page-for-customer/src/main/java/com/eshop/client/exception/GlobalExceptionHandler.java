package com.eshop.client.exception;

import com.eshop.client.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerErrorException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 404: dữ liệu không tồn tại
    @ExceptionHandler(BrandNotFoundException.class)
    public ResponseEntity<ApiError> brandNotFound(BrandNotFoundException exception, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "BRAND_NOT_FOUND", exception.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiError> orderNotFound(OrderNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(OrderReturnNotAllowedException.class)
    public ResponseEntity<ApiError> orderNotFound(OrderReturnNotAllowedException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "ORDER_RETURN_NOT_ALLOW", ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<ApiError> orderNotFound(ReviewNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "REVIEW_NOT_FOUND", ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiError> productNotFound(ProductNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND", ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity<ApiError> productNotFound(CartItemNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "CART_ITEM_NOT_FOUND", ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ApiError> categoryNotFound(CategoryNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "CATEGORY_NOT_FOUND", ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(PaypalAPIException.class)
    public ResponseEntity<ApiError> handleCart(PaypalAPIException exception, HttpServletRequest request) {
        return build(HttpStatus.MULTI_STATUS, "PAYPAL_ERROR", exception.getMessage(), request.getRequestURI(), null);
    }

    // 422: vượt quy tắc giỏ hàng, v.v.
    @ExceptionHandler(ShoppingCartException.class)
    public ResponseEntity<ApiError> handleCart(ShoppingCartException exception, HttpServletRequest request) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "CART_RULE_VIOLATION", exception.getMessage(), request.getRequestURI(), null);
    }

    // 400: request không hợp lệ
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException exception, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", exception.getMessage(), request.getRequestURI(), null);
    }

    // 400: lỗi validate @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        Map<String, String> details = exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        f -> f.getField(),
                        f -> Objects.toString(f.getDefaultMessage(), ""),
                        (a, b) -> a,
                        LinkedHashMap ::new
                ));
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Invalid request", request.getRequestURI(), details);
    }

    // 401: chưa đăng nhập / token hợp lệ nhưng user không tồn tại
    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ApiError> handleUnauthorized(CustomerNotFoundException exception, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", exception.getMessage(), request.getRequestURI(), null);
    }

    // 403: đã đăng nhập nhưng thiếu quyền
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleForbidden(AccessDeniedException exception, HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, "FORBIDDEN", "Bạn không có quyền thực hiện hành động này", request.getRequestURI(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> all(Exception ex, HttpServletRequest req) {
        ex.printStackTrace();
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR", "Internal error", req.getRequestURI(), null);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String code, String message, String path, Map<String, ?> details) {
        ApiError body = new ApiError(
                Instant.now().toString(),
                status.value(),
                status.getReasonPhrase(),
                code,
                message,
                path,
                details
        );
        return ResponseEntity.status(status).body(body);
    }
}
