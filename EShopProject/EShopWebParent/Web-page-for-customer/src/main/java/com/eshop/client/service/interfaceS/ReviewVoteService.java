package com.eshop.client.service.interfaceS;

import com.eshop.client.helper.VoteCounters;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.Review;

import java.util.List;

public interface ReviewVoteService {
    Integer getMyVote(Integer reviewId, Integer customerId);
    VoteCounters vote(Integer reviewId, Integer customerId, boolean up);
    VoteCounters removeVote(Integer reviewId, Integer customerId);
//    VoteCounters doVote(Integer reviewId, Customer customer, com.eshop.client.helper.VoteType voteType);
//    VoteCounters undoVote(Integer reviewId, Customer customer);
//    void markReviewsVotedForProductByCustomer(List<Review> listReviews, Integer productId, Integer customerId);
}
