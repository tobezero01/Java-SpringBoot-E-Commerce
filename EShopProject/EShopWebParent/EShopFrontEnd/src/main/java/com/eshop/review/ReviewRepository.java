package com.eshop.review;

import com.eshop.common.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    @Query("SELECT r FROM Review r WHERE r.customer.id = ?1")
    public Page<Review> findByCustomer(Integer customerId, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.customer.id = ?1 AND ( " +
            "r.headLine LIKE %?2% OR r.comment LIKE %?2% OR " +
            "r.product.name LIKE %?2% )")
    public Page<Review> findByCustomer(Integer customerId, String keyWord, Pageable pageable) ;

    @Query("SELECT r FROM Review r WHERE r.customer.id = ?1 AND r.id = ?2")
    public Review findCustomerAndId(Integer customerId, Integer reviewId);

}
