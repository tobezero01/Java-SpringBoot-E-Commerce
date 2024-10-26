package com.eshop.review.vote;

import com.eshop.common.entity.ReviewVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewVoteRepository extends JpaRepository<ReviewVote, Integer> {

    @Query("SELECT v FROM ReviewVote v WHERE v.review.id = ?1 AND v.customer.id = ?2")
    public ReviewVote findByReviewAndCustomer(Integer reviewId, Integer customerId);

    @Query("SELECT v FROM ReviewVote v WHERE v.review.product.id = ?1 AND v.customer.id = ?2")
    public List<ReviewVote> findByProductAndCustomer(Integer productId, Integer customerId);
}
