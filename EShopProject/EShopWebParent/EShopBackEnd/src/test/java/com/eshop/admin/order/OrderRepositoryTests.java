package com.eshop.admin.order;

import com.eshop.admin.order.repository.OrderRepository;
import com.eshop.common.entity.*;
import com.eshop.common.entity.order.*;
import com.eshop.common.entity.product.Product;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class OrderRepositoryTests {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    public void createOrderWithSingleProduct() {
        Customer customer = entityManager.find(Customer.class, 5);
        Product product1 = entityManager.find(Product.class, 6);

        Order mainOrder = new Order();
        mainOrder.setOrderTime(new Date());
        mainOrder.setCustomer(customer);
        mainOrder.copyAddressFromCustomer();

        OrderDetail orderDetail1 = new OrderDetail();
        orderDetail1.setProduct(product1);
        orderDetail1.setOrder(mainOrder);
        orderDetail1.setProductCost(product1.getCost());
        orderDetail1.setShippingCost(10);
        orderDetail1.setQuantity(1);
        orderDetail1.setSubtotal(product1.getPrice());
        orderDetail1.setUnitPrice(product1.getPrice());

        mainOrder.getOrderDetails().add(orderDetail1);

        mainOrder.setShippingCost(30);
        mainOrder.setProductCost(product1.getCost() );
        mainOrder.setTax(0);
        float subtotal =  product1.getPrice();
        mainOrder.setSubtotal(subtotal);
        mainOrder.setTotal(subtotal + 30);
        mainOrder.setPaymentMethod(PaymentMethod.COD);
        mainOrder.setDeliverDate(new Date());
        mainOrder.setStatus(OrderStatus.PACKAGED);
        mainOrder.setDeliverDays(4);

        Order saved = orderRepository.save(mainOrder);
        assertThat(saved.getId()).isGreaterThan(0);
    }

    @Test
    public void createOrderWithMultipleProduct() {
        Customer customer = entityManager.find(Customer.class, 5);
        Product product1 = entityManager.find(Product.class, 2);
        Product product2 = entityManager.find(Product.class, 5);

        Order mainOrder = new Order();
        mainOrder.setOrderTime(new Date());
        mainOrder.setCustomer(customer);
        mainOrder.copyAddressFromCustomer();


        OrderDetail orderDetail1 = new OrderDetail();
        orderDetail1.setProduct(product1);
        orderDetail1.setOrder(mainOrder);
        orderDetail1.setProductCost(product1.getCost());
        orderDetail1.setShippingCost(10);
        orderDetail1.setQuantity(1);
        orderDetail1.setSubtotal(product1.getPrice());
        orderDetail1.setUnitPrice(product1.getPrice());

        OrderDetail orderDetail2 = new OrderDetail();
        orderDetail2.setProduct(product2);
        orderDetail2.setOrder(mainOrder);
        orderDetail2.setProductCost(product2.getCost());
        orderDetail2.setShippingCost(10);
        orderDetail2.setQuantity(2);
        orderDetail2.setSubtotal(product2.getPrice() * 2);
        orderDetail2.setUnitPrice(product2.getPrice());

        mainOrder.getOrderDetails().add(orderDetail1);
        mainOrder.getOrderDetails().add(orderDetail2);

        mainOrder.setShippingCost(30);
        mainOrder.setProductCost(product1.getCost() + product2.getCost());
        mainOrder.setTax(0);
        float subtotal = product2.getPrice() * 2 + product1.getPrice();
        mainOrder.setSubtotal(subtotal);
        mainOrder.setTotal(subtotal + 30);
        mainOrder.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        mainOrder.setDeliverDate(new Date());
        mainOrder.setStatus(OrderStatus.PROCESSING);
        mainOrder.setDeliverDays(4);

        Order saved = orderRepository.save(mainOrder);
        assertThat(saved.getId()).isGreaterThan(0);
    }


    //test listALL

    //test get

    //update order

    //delete

    // update order track
    @Test
    public void testUpdateOrderTracks() {
        Integer orderId = 6;
        Order order = orderRepository.findById(orderId).get();

        OrderTrack orderTrack = new OrderTrack();
        orderTrack.setOrder(order);
        orderTrack.setUpdatedTime(new Date());
        orderTrack.setStatus(OrderStatus.PACKAGED);
        orderTrack.setNotes(OrderStatus.PACKAGED.defaultDescription());
        OrderTrack orderTrack1 = new OrderTrack();
        orderTrack1.setOrder(order);
        orderTrack1.setUpdatedTime(new Date());
        orderTrack1.setStatus(OrderStatus.PROCESSING);
        orderTrack1.setNotes(OrderStatus.PROCESSING.defaultDescription());

        List<OrderTrack> orderTracks = order.getOrderTracks();
        orderTracks.add(orderTrack);
        orderTracks.add(orderTrack1);
        Order updateOrder = orderRepository.save(order);
        assertThat(updateOrder.getOrderTracks()).hasSize(2);
    }
}
