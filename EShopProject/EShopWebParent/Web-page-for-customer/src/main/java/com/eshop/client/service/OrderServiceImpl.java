package com.eshop.client.service;

import com.eshop.client.dto.request.OrderReturnRequest;
import com.eshop.client.dto.response.OrderReturnResponse;
import com.eshop.client.exception.OrderNotFoundException;
import com.eshop.client.exception.OrderReturnNotAllowedException;
import com.eshop.client.helper.CheckoutInfo;
import com.eshop.client.repository.OrderDetailRepository;
import com.eshop.client.repository.OrderRepository;
import com.eshop.client.service.interfaceS.OrderService;
import com.eshop.common.entity.Address;
import com.eshop.common.entity.CartItem;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.order.*;
import com.eshop.common.entity.product.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    public static final int ORDERS_PER_PAGE = 5;
    private static final int RETURN_WINDOW_DAYS = 7;
    private final OrderRepository orderRepository;

    private final OrderDetailRepository orderDetailRepository;


    @Override
    @Transactional
    public Order createOrder(Customer customer,
                             Address address,                 // có thể null → copy từ customer
                             List<CartItem> cartItems,
                             CheckoutInfo checkoutInfo,
                             PaymentMethod paymentMethod,
                             String note,
                             String orderNumber) {

        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setOrderTime(new Date());
        order.setCustomer(customer);
        order.setPaymentMethod(paymentMethod);

        // Trạng thái khởi tạo theo PM
        order.setStatus(paymentMethod == PaymentMethod.PAYPAL ? OrderStatus.PENDING_PAYMENT_PAYPAL : OrderStatus.NEW);

        // Tổng tiền lấy từ CheckoutInfo (snapshot)
        order.setProductCost(checkoutInfo.getProductCost());
        order.setSubtotal(checkoutInfo.getProductTotal());
        order.setShippingCost(checkoutInfo.getShippingCostTotal());
        order.setTax(0.0f);
        order.setTotal(checkoutInfo.getPaymentTotal());
        order.setDeliverDays(checkoutInfo.getDeliverDays());
        order.setDeliverDate(checkoutInfo.getDeliverDate());
        order.setNote(note);

        // Copy địa chỉ giao hàng (flatten)
        if (address == null) {
            order.copyAddressFromCustomer();
        } else {
            order.copyShippingAddress(address);
        }

        // Lưu order trước để có ID
        orderRepository.save(order);

        // Lưu chi tiết
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            OrderDetail d = new OrderDetail();
            d.setOrder(order);
            d.setProduct(product);

            // unitPrice = discountPrice > 0 ? discountPrice : price
            float unitPrice = (product.getDiscountPrice() > 0 && product.getDiscountPrice() > 0)
                    ? product.getDiscountPrice()
                    : product.getPrice();

            d.setUnitPrice(unitPrice);
            d.setQuantity(item.getQuantity());
            d.setProductCost(product.getCost() * item.getQuantity());
            d.setSubtotal(unitPrice * item.getQuantity());
            d.setShippingCost(item.getShippingCost());

            orderDetailRepository.save(d);
        }

        return order;
    }

    @Override
    @Transactional(readOnly = true)
    public Order getByOrderNumberAndCustomer(String orderNumber, Customer customer) {
        return orderRepository.findByOrderNumberAndCustomerId(orderNumber, customer.getId())
                .orElse(null);
    }

//    private static OrderDetail getOrderDetail(CartItem item, Order newOrder) {
//        Product product = item.getProduct();
//        OrderDetail orderDetail = new OrderDetail();
//        orderDetail.setOrder(newOrder);
//        orderDetail.setProduct(product);
//        orderDetail.setQuantity(item.getQuantity());
//        orderDetail.setUnitPrice(product.getDiscountPrice());
//        orderDetail.setProductCost(product.getCost() * item.getQuantity());
//        orderDetail.setSubtotal(item.getSubtotal());
//        orderDetail.setShippingCost(item.getShippingCost());
//        return orderDetail;
//    }

    @Override
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

    @Override
    public Order getOrder(Integer id, Customer customer) {
        return orderRepository.findWithDetailsByIdAndCustomerId(id, customer.getId());
    }

    @Override
    public boolean canReturn(Order order) {
        if (order == null) return false;
        if (order.isReturnRequested() || order.isReturned() || order.isRefunded() || order.isCanceled()) {
            return false;
        }
        if (!order.isDelivered()) return false;
        Optional<Instant> deliveredAtOpt = resolveDeliveredInstant(order);
        if (deliveredAtOpt.isEmpty()) return false;
        ZoneId zone = ZoneId.systemDefault();
        LocalDate deliveredDate = deliveredAtOpt.get().atZone(zone).toLocalDate();
        LocalDate today = LocalDate.now(zone);
        long days = ChronoUnit.DAYS.between(deliveredDate, today);
        return days >= 0 && days <= RETURN_WINDOW_DAYS;
//        Date deliver = order.getDeliverDate();
//        if (deliver == null) return false;
//        LocalDate deliverDate = Instant.ofEpochMilli(deliver.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
//        LocalDate today = LocalDate.now();
//        long days = Duration.between(deliverDate.atStartOfDay(), today.plusDays(0).atStartOfDay()).toDays();
//        return days >= 0 && days <= RETURN_WINDOW_DAYS;
    }

    @Override
    @Transactional
    public OrderReturnResponse setOrderReturnRequested(OrderReturnRequest request, Customer customer) {
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

    @Override
    public String buildReturnBlockReason(Order order) {
        if (order.isReturnRequested()) return "Đơn đã có yêu cầu trả hàng trước đó";
        if (order.isReturned()) return "Đơn đã được trả hàng";
        if (order.isRefunded()) return "Đơn đã hoàn tiền";
        if (order.isCanceled()) return "Đơn đã bị hủy";
        if (!order.isDelivered()) return "Chỉ trả hàng sau khi đơn đã được giao";
        Date deliver = order.getDeliverDate();
        if (deliver == null) return "Không có ngày giao hàng để đối chiếu";
        LocalDate d = Instant.ofEpochMilli(deliver.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        ZoneId zone = ZoneId.systemDefault();
        Optional<Instant> deliveredAtOpt = resolveDeliveredInstant(order);
        LocalDate deliveredDateInReal = deliveredAtOpt.get().atZone(zone).toLocalDate();
        LocalDate deadline = deliveredDateInReal.plusDays(RETURN_WINDOW_DAYS);
        return "Quá hạn trả hàng (hạn đến " + deadline + ")";
    }

    private static String nullToEmpty(String s) { return s == null ? "" : s; }

    @Override
    @Transactional
    public Order save(Order o) {
        return orderRepository.save(o);
    }

    private Optional<Instant> resolveDeliveredInstant(Order order) {
        if (order.getOrderTracks() != null) {
            Optional<Instant> fromTracks = order.getOrderTracks().stream()
                    .filter(t -> t.getStatus() == OrderStatus.DELIVERED && t.getUpdatedTime() != null)
                    .map(t -> t.getUpdatedTime().toInstant())
                    .max(Comparator.naturalOrder());
            if (fromTracks.isPresent()) return fromTracks;
        }
        return Optional.ofNullable(order.getDeliverDate()).map(Date::toInstant);
    }

}
