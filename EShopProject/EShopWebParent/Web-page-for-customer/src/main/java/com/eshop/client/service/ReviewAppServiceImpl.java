package com.eshop.client.service;

import com.eshop.client.dto.ReviewDTO;
import com.eshop.client.dto.VoteDTO;
import com.eshop.client.helper.VoteCounters;
import com.eshop.client.service.interfaceS.ReviewAppService;
import com.eshop.client.service.interfaceS.ReviewVoteService;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import static com.eshop.client.mapper.ReviewMapper.toDto;

@Service
@RequiredArgsConstructor
public class ReviewAppServiceImpl implements ReviewAppService {

    private final ReviewService reviewService;
    private final ReviewVoteService voteService;

    @Override
    public Page<ReviewDTO> listByProduct(Integer productId, int page, int size, String sortKey, Customer me) {
        Page<Review> pg = reviewService.listByProduct(productId, page, size, sortKey);
        return pg.map(r -> toDto(r, me));
    }

    @Override
    public ReviewDTO create(Customer me, Integer productId, int rating, String comment) {
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("Rating must be 1..5");
        if (!reviewService.canCustomerReviewProduct(me, productId)) {
            throw new IllegalStateException("Bạn chưa đủ điều kiện để đánh giá sản phẩm này");
        }
        Review r = reviewService.createReview(me, productId, rating, comment);
        return toDto(r, me);
    }

    @Override
    public ReviewDTO update(Customer me, Integer reviewId, int rating, String comment) {
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("Rating must be 1..5");
        Review r = reviewService.updateReview(me, reviewId, rating, comment);
        return toDto(r, me);
    }

    @Override
    public void delete(Customer me, Integer reviewId) {
        reviewService.deleteReview(me, reviewId);
    }

    @Override
    public VoteDTO vote(Customer me, Integer reviewId, boolean up) {
        VoteCounters c = voteService.vote(reviewId, me.getId(), up);
        String myVote = up ? "UP" : "DOWN";
        return new VoteDTO(reviewId, c.getUp(), c.getDown(), myVote);
    }

    @Override
    public VoteDTO unvote(Customer me, Integer reviewId) {
        VoteCounters c = voteService.removeVote(reviewId, me.getId());
        return new VoteDTO(reviewId, c.getUp(), c.getDown(), "NONE");
    }
}
