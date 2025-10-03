package com.eshop.client.controller;

import com.eshop.client.dto.VoteDTO;
import com.eshop.client.dto.request.VoteRequest;
import com.eshop.client.exception.CustomerNotFoundException;
import com.eshop.client.helper.ControllerHelper;
import com.eshop.client.service.interfaceS.ReviewAppService;
import com.eshop.common.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews/{reviewId}")
@RequiredArgsConstructor
public class ReviewVoteRestController {

    private final ReviewAppService app;
    private final ControllerHelper helper;

    private Customer me() throws CustomerNotFoundException { return helper.requireAuthenticatedCustomer(); }

    /** Vote like/unlike (JWT) */
    @PostMapping("/vote")
    public VoteDTO vote(@PathVariable Integer reviewId, @RequestBody VoteRequest req) throws CustomerNotFoundException {
        if (req.up() == null) throw new IllegalArgumentException("Missing 'up' field");
        return app.vote(me(), reviewId, req.up());
    }

    /** Bỏ vote (JWT) */
    @DeleteMapping("/vote")
    public VoteDTO unvote(@PathVariable Integer reviewId) throws CustomerNotFoundException {
        return app.unvote(me(), reviewId);
    }
}
