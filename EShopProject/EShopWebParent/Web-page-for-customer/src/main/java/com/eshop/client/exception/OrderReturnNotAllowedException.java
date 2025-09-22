package com.eshop.client.exception;

public class OrderReturnNotAllowedException extends RuntimeException {
    public OrderReturnNotAllowedException(String message) { super(message); }
}
