package com.eshop.admin.order.repository;

import com.eshop.common.entity.order.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

//    public List<OrderDetail> findWithCategoryAndTimeBetween(Date startDate, Date endDate);
}
