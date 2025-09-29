package com.eshop.client.dto;

import java.util.Map;

public record ApiError(
        String timestamp,
        int status,
        String error,
        String code,
        String message,
        String path,
        Map<String, ?> details
) {}
