package com.eshop.admin.order;

import com.eshop.common.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("Select o From Order o where LOWER(o.firstName) Like LOWER(CONCAT('%', ?1, '%')) Or " +
            "LOWER(o.lastName) Like LOWER(CONCAT('%', ?1, '%')) Or " +
            "LOWER(o.phoneNumber) Like LOWER(CONCAT('%', ?1, '%')) Or " +
            "LOWER(o.addressLine1) Like LOWER(CONCAT('%', ?1, '%')) Or " +
            "LOWER(o.addressLine2) Like LOWER(CONCAT('%', ?1, '%')) Or " +
            "LOWER(o.postalCode) Like LOWER(CONCAT('%', ?1, '%')) Or " +
            "LOWER(o.city) Like LOWER(CONCAT('%', ?1, '%')) Or " +
            "LOWER(o.state) Like LOWER(CONCAT('%', ?1, '%')) Or " +
            "LOWER(o.country) Like LOWER(CONCAT('%', ?1, '%')) Or " +
            "o.paymentMethod Like %?1% Or o.status Like %?1% Or " +
            "LOWER(o.customer.firstName) Like LOWER(CONCAT('%', ?1, '%')) Or " +
            "LOWER(o.customer.lastName) Like LOWER(CONCAT('%', ?1, '%'))")
    public Page<Order> findAll(String keyWord, Pageable pageable);


}
