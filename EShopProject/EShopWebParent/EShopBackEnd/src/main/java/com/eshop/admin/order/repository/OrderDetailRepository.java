package com.eshop.admin.order.repository;

import com.eshop.common.entity.order.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    @Query("SELECT NEW com.eshop.common.entity.order.OrderDetail( " +
            "d.product.category.name, d.quantity, d.productCost, d.shippingCost, d.subtotal) " +
            "FROM OrderDetail d " +
            "WHERE d.order.orderTime BETWEEN ?1 AND ?2")
    List<OrderDetail> findWithCategoryAndTimeBetween(Date startDate, Date endDate);

    @Query("SELECT NEW com.eshop.common.entity.order.OrderDetail( " +
            "d.quantity, d.product.name, d.productCost, d.shippingCost, d.subtotal) " +
            "FROM OrderDetail d " +
            "WHERE d.order.orderTime BETWEEN ?1 AND ?2")
    List<OrderDetail> findWithProductAndTimeBetween(Date startDate, Date endDate);

}
