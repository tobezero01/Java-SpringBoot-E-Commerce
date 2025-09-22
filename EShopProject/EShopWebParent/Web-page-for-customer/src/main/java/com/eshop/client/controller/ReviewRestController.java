package com.eshop.client.controller;

import com.eshop.client.dto.ReviewDTO;
import com.eshop.client.dto.request.CreateReviewRequest;
import com.eshop.client.dto.request.UpdateReviewRequest;
import com.eshop.client.dto.response.PageResponse;
import com.eshop.client.exception.CustomerNotFoundException;
import com.eshop.client.helper.ControllerHelper;
import com.eshop.client.service.ReviewAppService;
import com.eshop.common.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewRestController {

    private final ReviewAppService app;
    private final ControllerHelper helper;

    private Customer me() throws CustomerNotFoundException { return helper.requireAuthenticatedCustomer(); }

    /** Public: danh sách review theo sản phẩm (có phân trang) */
    @GetMapping("/products/{productId}/reviews")
    public PageResponse<ReviewDTO> list(@PathVariable Integer productId,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(defaultValue = "newest") String sort) {
        Customer current = null;
        try { current = me(); } catch (Exception ignore) {} // nếu chưa đăng nhập -> myVote = "NONE"
        Page<ReviewDTO> p = app.listByProduct(productId, page, size, sort, current);
        return PageResponse.of(p.getContent(), p.getNumber() + 1, p.getSize(), p.getTotalElements(), p.getTotalPages());
    }

    /** Create review (JWT) */
    @PostMapping("/reviews")
    public ReviewDTO create(@RequestBody CreateReviewRequest req) throws CustomerNotFoundException {
        return app.create(me(), req.productId(), req.rating(), req.comment());
    }

    /** Update my review (JWT) */
    @PutMapping("/reviews/{id}")
    public ReviewDTO update(@PathVariable Integer id, @RequestBody UpdateReviewRequest req) throws CustomerNotFoundException {
        return app.update(me(), id, req.rating(), req.comment());
    }

    /** Delete my review (JWT) */
    @DeleteMapping("/reviews/{id}")
    public void delete(@PathVariable Integer id) throws CustomerNotFoundException {
        app.delete(me(), id);
    }
}
