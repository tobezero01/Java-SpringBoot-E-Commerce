package com.eshop.client.dto;

import java.util.Date;

public record ReviewDTO(
        Integer id,
        Integer productId,
        String productName,
        String alias,
        String image,
        int rating,
        String comment,
        Date createdAt,
        String myVote
) {}
