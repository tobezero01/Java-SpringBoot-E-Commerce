package com.eshop.client.service.interfaceS;

import com.eshop.client.dto.ReviewDTO;
import com.eshop.client.dto.VoteDTO;
import com.eshop.common.entity.Customer;
import org.springframework.data.domain.Page;

public interface ReviewAppService {
    Page<ReviewDTO> listByProduct(Integer productId, int page, int size, String sortKey, Customer me);
    ReviewDTO create(Customer me, Integer productId, int rating, String comment);
    ReviewDTO update(Customer me, Integer reviewId, int rating, String comment);
    void delete(Customer me, Integer reviewId);
    VoteDTO vote(Customer me, Integer reviewId, boolean up);
    VoteDTO unvote(Customer me, Integer reviewId);
}
