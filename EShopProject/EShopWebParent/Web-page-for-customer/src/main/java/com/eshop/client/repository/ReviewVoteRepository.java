package com.eshop.client.repository;

import com.eshop.common.entity.ReviewVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewVoteRepository extends JpaRepository<ReviewVote, Integer> {

    @Query("SELECT v FROM ReviewVote v WHERE v.review.id = ?1 AND v.customer.id = ?2")
    public ReviewVote findByReviewAndCustomer(Integer reviewId, Integer customerId);

    @Query("SELECT v FROM ReviewVote v WHERE v.review.product.id = ?1 AND v.customer.id = ?2")
    public List<ReviewVote> findByProductAndCustomer(Integer productId, Integer customerId);

    @Query("SELECT COUNT(v) FROM ReviewVote v WHERE v.review.id = :reviewId AND v.votes = 1")
    long countUp(@Param("reviewId") Integer reviewId);

    @Query("SELECT COUNT(v) FROM ReviewVote v WHERE v.review.id = :reviewId AND v.votes = -1")
    long countDown(@Param("reviewId") Integer reviewId);

    @Modifying
    @Query("DELETE FROM ReviewVote v WHERE v.review.id = :reviewId AND v.customer.id = :customerId")
    int deleteByReviewAndCustomer(@Param("reviewId") Integer reviewId, @Param("customerId") Integer customerId);
}
