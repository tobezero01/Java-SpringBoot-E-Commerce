package com.eshop.client.dto.request;

public record UpdateReviewRequest(
        int rating,
        String comment
) {}
