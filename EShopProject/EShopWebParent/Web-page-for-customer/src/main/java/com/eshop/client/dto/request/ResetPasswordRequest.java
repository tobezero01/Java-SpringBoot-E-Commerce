package com.eshop.client.dto.request;

public record ResetPasswordRequest(String token, String newPassword) {}

