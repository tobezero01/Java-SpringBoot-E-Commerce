package com.eshop.client.service;

import com.eshop.client.dto.request.OrderReturnRequest;
import com.eshop.client.dto.response.OrderReturnResponse;
import com.eshop.client.exception.OrderNotFoundException;
import com.eshop.client.exception.OrderReturnNotAllowedException;
import com.eshop.client.helper.CheckoutInfo;
import com.eshop.client.repository.OrderRepository;
import com.eshop.common.entity.Address;
import com.eshop.common.entity.CartItem;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.order.*;
import com.eshop.common.entity.product.Product;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderService {
    public static final int ORDERS_PER_PAGE = 5;
    private static final int RETURN_WINDOW_DAYS = 7;
    private final OrderRepository orderRepository;

    public Order createOrder(Customer customer, Address address, List<CartItem> cartItems,
                             PaymentMethod paymentMethod, CheckoutInfo checkoutInfo) {
        Order newOrder = new Order();

        newOrder.setOrderTime(new Date());
        if (paymentMethod.equals(PaymentMethod.PAYPAL)) {
            newOrder.setStatus(OrderStatus.PAID);
        }else {
            newOrder.setStatus(OrderStatus.NEW);
        }

        newOrder.setCustomer(customer);
        newOrder.setProductCost(checkoutInfo.getProductCost());
        newOrder.setSubtotal(checkoutInfo.getProductTotal());
        newOrder.setShippingCost(checkoutInfo.getShippingCostTotal());
        newOrder.setTax(0.0f);
        newOrder.setTotal(checkoutInfo.getPaymentTotal());
        newOrder.setPaymentMethod(paymentMethod);
        newOrder.setDeliverDays(checkoutInfo.getDeliverDays());
        newOrder.setDeliverDate(checkoutInfo.getDeliverDate());

        if (address == null) {
            newOrder.copyAddressFromCustomer();
        } else {
            newOrder.copyShippingAddress(address);
        }

        Set<OrderDetail> orderDetails = newOrder.getOrderDetails();
        for (CartItem item : cartItems) {
            OrderDetail orderDetail = getOrderDetail(item, newOrder);
            orderDetails.add(orderDetail);
        }
        return orderRepository.save(newOrder);
    }

    private static OrderDetail getOrderDetail(CartItem item, Order newOrder) {
        Product product = item.getProduct();
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(newOrder);
        orderDetail.setProduct(product);
        orderDetail.setQuantity(item.getQuantity());
        orderDetail.setUnitPrice(product.getDiscountPrice());
        orderDetail.setProductCost(product.getCost() * item.getQuantity());
        orderDetail.setSubtotal(item.getSubtotal());
        orderDetail.setShippingCost(item.getShippingCost());
        return orderDetail;
    }

    public Page<Order> listForCustomerByPage(Customer customer, int pageNum,
                                             String sortField, String sortDir, String keyWord) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);
        if (keyWord != null) {
            return orderRepository.findALl(keyWord, customer.getId(), pageable);
        }
        return orderRepository.findAll(customer.getId(), pageable);
    }

    public Order getOrder(Integer id, Customer customer) {
        return orderRepository.findByIdAndCustomer(id, customer);
    }

    public boolean canReturn(Order order) {
        if (order == null) return false;
        if (order.isReturnRequested() || order.isReturned() || order.isRefunded() || order.isCanceled()) {
            return false;
        }
        if (!order.isDelivered()) return false;
        Date deliver = order.getDeliverDate();
        if (deliver == null) return false;
        LocalDate deliverDate = Instant.ofEpochMilli(deliver.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();
        long days = Duration.between(deliverDate.atStartOfDay(), today.plusDays(0).atStartOfDay()).toDays();
        return days >= 0 && days <= RETURN_WINDOW_DAYS;
    }

    @Transactional
    public OrderReturnResponse setOrderReturnRequested(OrderReturnRequest request, Customer customer)
            throws OrderNotFoundException {
        Order order = orderRepository.findByIdAndCustomer(request.orderId(), customer);
        if (!canReturn(order)) throw new OrderReturnNotAllowedException("Order cannot return");

        if (!order.isReturnRequested()) {
            OrderTrack track = new OrderTrack();
            track.setOrder(order);
            track.setUpdatedTime(new Date());
            track.setStatus(OrderStatus.RETURN_REQUESTED);
            String notes = "Reason: " + (request.reason() == null ? "" : request.reason());
            if (request.note() != null && !request.note().isBlank()) notes += ". " + request.note();
            track.setNotes(notes);
            order.getOrderTracks().add(track);
            order.setStatus(OrderStatus.RETURN_REQUESTED);
            orderRepository.save(order);
        }
        return new OrderReturnResponse(order.getId(), order.getStatus().name());
    }

    public String buildReturnBlockReason(Order order) {
        if (order.isReturnRequested()) return "Đơn đã có yêu cầu trả hàng trước đó";
        if (order.isReturned()) return "Đơn đã được trả hàng";
        if (order.isRefunded()) return "Đơn đã hoàn tiền";
        if (order.isCanceled()) return "Đơn đã bị hủy";
        if (!order.isDelivered()) return "Chỉ trả hàng sau khi đơn đã được giao";
        Date deliver = order.getDeliverDate();
        if (deliver == null) return "Không có ngày giao hàng để đối chiếu";
        LocalDate d = Instant.ofEpochMilli(deliver.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate deadline = d.plusDays(RETURN_WINDOW_DAYS);
        return "Quá hạn trả hàng (hạn đến " + deadline + ")";
    }

    private static String nullToEmpty(String s) { return s == null ? "" : s; }


}
