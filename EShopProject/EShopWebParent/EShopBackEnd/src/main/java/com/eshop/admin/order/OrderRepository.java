package com.eshop.admin.order;

import com.eshop.common.entity.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

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

    public Long countById(Integer id);

    @Query("SELECT NEW com.eshop.common.entity.order.Order(o.id, o.orderTime, o.productCost, " +
            "o.subtotal, o.total) FROM Order o WHERE " +
            "o.orderTime BETWEEN ?1 AND ?2 ORDER BY o.orderTime ASC")
    public List<Order> findByOrderTimeBetween(Date startTime, Date endTime);
}
