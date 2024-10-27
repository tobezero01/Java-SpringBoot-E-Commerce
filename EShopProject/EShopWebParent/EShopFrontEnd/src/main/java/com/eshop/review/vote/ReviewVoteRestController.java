package com.eshop.review.vote;

import com.eshop.ControllerHelper;
import com.eshop.common.entity.Customer;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReviewVoteRestController {
    @Autowired private ReviewVoteService voteService;
    @Autowired private ControllerHelper helper;

    @PostMapping("/vote_review/{id}/{type}")
    public VoteResult voteResult(@PathVariable(name = "id") Integer reviewId,
                                 @PathVariable(name = "type") String type ,
                                 HttpServletRequest request) {
        Customer customer = helper.getAuthenticatedCustomer(request);

        if (customer == null) {
            return VoteResult.fail("YOu must login to vote the review");
        }

        VoteType voteType = VoteType.valueOf(type.toUpperCase());
        return voteService.doVote(reviewId, customer, voteType);
    }
}
