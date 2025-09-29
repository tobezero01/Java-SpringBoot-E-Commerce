package com.eshop.client.exception;

public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException(String message) { super(message); }
}