package com.eshop.order;

import com.eshop.common.entity.order.OrderDetail;
import com.eshop.common.entity.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    @Query("SELECT COUNT(d) FROM OrderDetail d JOIN OrderTrack t ON d.order.id = t.order.id " +
            "WHERE d.product.id = ?1 AND d.order.customer.id = ?2 AND t.status = ?3")
    public Long countByProductAndCustomerAndOrderStatus(Integer productId, Integer customerId, OrderStatus status);



}
