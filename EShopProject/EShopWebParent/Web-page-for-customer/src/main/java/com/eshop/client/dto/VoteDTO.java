package com.eshop.client.dto;

public record VoteDTO(
        Integer reviewId,
        int upVotes,
        int downVotes,
        String myVote // "UP" | "DOWN" | "NONE"
) {}
