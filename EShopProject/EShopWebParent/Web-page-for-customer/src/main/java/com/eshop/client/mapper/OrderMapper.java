package com.eshop.client.mapper;

import com.eshop.client.dto.CheckoutSummaryDTO;
import com.eshop.client.dto.OrderDetailDTO;
import com.eshop.client.dto.OrderItemDTO;
import com.eshop.client.dto.OrderSummaryDTO;
import com.eshop.client.helper.CheckoutInfo;
import com.eshop.client.service.*;
import com.eshop.common.entity.Address;
import com.eshop.common.entity.CartItem;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.order.Order;
import com.eshop.common.entity.order.OrderDetail;
import com.eshop.common.entity.product.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final ReviewService reviewService;

    public OrderSummaryDTO toSummary(Order o) {
        int items = o.getOrderDetails() == null ? 0 : o.getOrderDetails().size();
        return new OrderSummaryDTO(
                o.getId(), o.getOrderNumber(), o.getOrderTime(),
                o.getStatus().name(), o.getTotal(), items
        );
    }

    public OrderDetailDTO toDetail(Customer customer, Order o) {
        List<OrderItemDTO> items = o.getOrderDetails().stream().map(d -> toItem(customer, d)).toList();
        return new OrderDetailDTO(
                o.getId(), o.getOrderNumber(), o.getOrderTime(),
                o.getStatus().name(), o.getPaymentMethod().name(),
                o.getSubtotal(), o.getShippingCost(), o.getTotal(),
                items,
                o.getFirstName(), o.getLastName(), o.getPhoneNumber(), o.getAddressLine1(), o.getAddressLine2(),
                o.getCity(), o.getState(), o.getPostalCode(), o.getCountry() == null ? null : o.getCountry()
        );
    }

    private OrderItemDTO toItem(Customer customer, OrderDetail d) {
        Product p = d.getProduct();
        boolean reviewed = reviewService.didCustomerReviewProduct(customer, p.getId());
        boolean canReview = reviewed ? false : reviewService.canCustomerReviewProduct(customer, p.getId());
        return new OrderItemDTO(
                p.getId(), p.getName(), p.getAlias(), p.getMainImagePath(),
                d.getUnitPrice(), d.getQuantity(), d.getSubtotal(),
                reviewed, canReview
        );
    }
}
