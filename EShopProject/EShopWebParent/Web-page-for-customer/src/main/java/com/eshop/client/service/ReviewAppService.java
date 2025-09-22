package com.eshop.client.service;

import com.eshop.client.dto.ReviewDTO;
import com.eshop.client.dto.VoteDTO;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.Review;
import com.eshop.common.entity.product.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewAppService {

    private final ReviewService reviewService;
    private final ReviewVoteService voteService;

    private ReviewDTO toDto(Review review, Customer me) {
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

    public Page<ReviewDTO> listByProduct(Integer productId, int page, int size, String sortKey, Customer me) {
        Page<Review> pg = reviewService.listByProduct(productId, page, size, sortKey);
        return pg.map(r -> toDto(r, me));
    }

    public ReviewDTO create(Customer me, Integer productId, int rating, String comment) {
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("Rating must be 1..5");
        if (!reviewService.canCustomerReviewProduct(me, productId)) {
            throw new IllegalStateException("Bạn chưa đủ điều kiện để đánh giá sản phẩm này");
        }
        Review r = reviewService.createReview(me, productId, rating, comment);
        return toDto(r, me);
    }

    public ReviewDTO update(Customer me, Integer reviewId, int rating, String comment) {
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("Rating must be 1..5");
        Review r = reviewService.updateReview(me, reviewId, rating, comment);
        return toDto(r, me);
    }

    public void delete(Customer me, Integer reviewId) {
        reviewService.deleteReview(me, reviewId);
    }

    // ===== Vote =====
    public VoteDTO vote(Customer me, Integer reviewId, boolean up) {
        ReviewVoteService.VoteCounters c = voteService.vote(reviewId, me.getId(), up);
        String myVote = up ? "UP" : "DOWN";
        return new VoteDTO(reviewId, c.getUp(), c.getDown(), myVote);
    }

    public VoteDTO unvote(Customer me, Integer reviewId) {
        ReviewVoteService.VoteCounters c = voteService.removeVote(reviewId, me.getId());
        return new VoteDTO(reviewId, c.getUp(), c.getDown(), "NONE");
    }
}
