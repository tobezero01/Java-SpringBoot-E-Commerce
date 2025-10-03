package com.eshop.client.mapper;

import com.eshop.client.dto.ReviewDTO;
import com.eshop.client.service.ReviewService;
import com.eshop.client.service.interfaceS.ReviewVoteService;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.Review;
import com.eshop.common.entity.product.Product;
import lombok.RequiredArgsConstructor;

public final class ReviewMapper {
    private static ReviewVoteService voteService ;

    public static ReviewDTO toDto(Review review, Customer me) {
        String myVote = "NONE";
        if (me != null) {
            Integer v = voteService.getMyVote(review.getId(), me.getId());
            if (v != null) myVote = v > 0 ? "UP" : v < 0 ? "DOWN" : "NONE";
        }
        Product p = review.getProduct();
        return new ReviewDTO(
                review.getId(),
                (p == null ? null : p.getId()),
                (p == null ? null : p.getName()),
                (p == null ? null : p.getAlias()),
                (p == null ? null : p.getMainImagePath()),
                review.getRating(),
                review.getComment(),
                review.getReviewTime(),
                myVote
        );
    }
}
