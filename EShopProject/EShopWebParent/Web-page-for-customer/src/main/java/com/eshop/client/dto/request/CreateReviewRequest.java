package com.eshop.client.dto.request;

public record CreateReviewRequest(
        Integer productId,
        int rating,
        String comment
) {}
