package com.eshop.client.repository;

import com.eshop.common.entity.Customer;
import com.eshop.common.entity.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("SELECT o FROM Order o JOIN o.orderDetails od JOIN od.product p " +
            "WHERE o.customer.id = ?2 AND (p.name LIKE %?1% OR CAST(o.status AS string) LIKE %?1%)")
    Page<Order> findALl(String keyWord, Integer customerId, Pageable pageable);


    @Query("SELECT o FROM Order o WHERE o.customer.id = ?1")
    public Page<Order> findAll(Integer customerId, Pageable pageable);

    public Order findByIdAndCustomer(Integer id, Customer customer);

    Optional<Order> findByOrderNumberAndCustomerId(String orderNumber, Integer customerId);

    @Query("""
         select o from Order o
         left join fetch o.orderDetails d
         left join fetch d.product p
         where o.id = :id and o.customer.id = :customerId
         """)
    Order findWithDetailsByIdAndCustomerId(@Param("id") Integer id,
                                                     @Param("customerId") Integer customerId);
}
