package com.eshop.client.service;

import com.eshop.client.dto.CheckoutItemDTO;
import com.eshop.client.dto.CheckoutSummaryDTO;
import com.eshop.client.dto.request.PlaceOrderRequest;
import com.eshop.client.dto.response.PlaceOrderResponse;
import com.eshop.client.helper.CheckoutInfo;
import com.eshop.client.repository.OrderDetailRepository;
import com.eshop.client.repository.OrderRepository;
import com.eshop.common.entity.Address;
import com.eshop.common.entity.CartItem;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.ShippingRate;
import com.eshop.common.entity.order.Order;
import com.eshop.common.entity.order.OrderDetail;
import com.eshop.common.entity.order.OrderStatus;
import com.eshop.common.entity.order.PaymentMethod;
import com.eshop.common.entity.product.Product;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class CheckoutAppService {
    private final ShoppingCartService cartService;
    private final AddressService addressService;
    private final ShippingRateService shippingRateService;
    private final CheckoutService checkoutService;
    private final OrderService orderService;
    private final OrderEmailService orderEmailService;

    public CheckoutSummaryDTO summarize(Customer customer, Integer addressId) {
        List<CartItem> cartItems = cartService.listCartItems(customer);
        if (cartItems.isEmpty()) throw new IllegalArgumentException("Giỏ hàng trống");

        Address address = resolveAddress(customer, addressId);
        ShippingRate rate = (address != null)
                ? shippingRateService.getShippingRateForAddress(address)
                : shippingRateService.getShippingRateForCustomer(customer);

        CheckoutInfo info = checkoutService.prepareCheckout(cartItems, rate);

        // BUG fix: điều kiện chọn unitPrice
        List<CheckoutItemDTO> dtoItems = cartItems.stream().map(i -> {
            Product product = i.getProduct();
            float unitPrice = (product.getDiscountPrice() > 0 && product.getDiscountPrice() > 0) ? product.getDiscountPrice() : product.getPrice();
            return new CheckoutItemDTO(
                    product.getId(), product.getName(), product.getAlias(), product.getMainImagePath(),
                    unitPrice, i.getQuantity(), unitPrice * i.getQuantity()
            );
        }).toList();

        return new CheckoutSummaryDTO(
                dtoItems,
                info.getProductTotal(),
                info.getShippingCostTotal(),
                info.getPaymentTotal(),
                rate != null,
                (address != null ? address.getId() : null),
                (address != null ? address.toString() : null)
        );
    }

    @Transactional
    public PlaceOrderResponse placeOrderCod(Customer customer, PlaceOrderRequest request)
            throws MessagingException, UnsupportedEncodingException {

        CheckoutSummaryDTO summary = summarize(customer, request.addressId());
        Address address = resolveAddress(customer, summary.addressId());
        List<CartItem> items   = cartService.listCartItems(customer);
        if (items.isEmpty()) throw new IllegalArgumentException("Giỏ hàng trống");

        CheckoutInfo checkoutInfo = getPrepareCheckout(items, address, customer);

        // Tạo đơn (NEW) + chi tiết
        Order order = orderService.createOrder(
                customer, address, items,
                checkoutInfo,
                PaymentMethod.COD,
                request.note(),
                generateOrderNumber()
        );

        // Clear cart & email sau khi tạo
        cartService.deleteByCustomer(customer);

        // Gửi email NÊN chuyển sang after-commit (xem bên dưới)
        orderEmailService.sendOrderConfirmation(customer, order);

        return new PlaceOrderResponse(
                order.getId(), order.getOrderNumber(), order.getStatus().name(),
                order.getPaymentMethod().name(), checkoutInfo.getProductTotal(),
                checkoutInfo.getShippingCostTotal(), order.getTotal(), order.getOrderTime()
        );
    }

    public CheckoutInfo getPrepareCheckout(List<CartItem> items, Address address, Customer customer) {
        return checkoutService.prepareCheckout(items,
                (address != null) ? shippingRateService.getShippingRateForAddress(address)
                        : shippingRateService.getShippingRateForCustomer(customer));
    }

    @Transactional
    public Order createPendingPaypalOrder(Customer customer, Integer addressId, String note) {
        CheckoutSummaryDTO summary = summarize(customer, addressId);
        Address address = resolveAddress(customer, summary.addressId());
        List<CartItem> items   = cartService.listCartItems(customer);
        if (items.isEmpty()) throw new IllegalArgumentException("Giỏ hàng trống");
        CheckoutInfo checkoutInfo = getPrepareCheckout(items, address, customer);

        // chụp chi tiết ngay bây giờ; status = PENDING_PAYMENT
        return orderService.createOrder(
                customer, address, items,
                checkoutInfo,
                PaymentMethod.PAYPAL,
                note,
                generateOrderNumber()
        );
    }

    public Address resolveAddress(Customer customer, Integer addressId) {
        return (addressId != null) ? addressService.get(addressId, customer.getId())
                : addressService.getDefaultAddress(customer);
    }

    private String generateOrderNumber() {
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE); // yyyyMMdd
        int n = ThreadLocalRandom.current().nextInt(0, 1_000_000);              // 0..999999
        return "OD" + dateTime + "-" + String.format("%06d", n);
    }

    /**
     * Hoàn tất đơn PayPal sau khi capture thành công:
     * - Chuyển trạng thái từ PENDING_PAYMENT -> PAID/PROCESSING
     *
     * - Clear giỏ
     * - Gửi email xác nhận
     */
    @Transactional
    public Order finalizePaypalOrderAfterCapture(Customer customer,
                                                 String localOrderNumber,
                                                 String captureId,
                                                 Date capturedAt,
                                                 Float capturedAmount,
                                                 String capturedCurrency) {
        Order order = orderService.getByOrderNumberAndCustomer(localOrderNumber, customer);
        if (order == null) {
            throw new IllegalArgumentException("Local order not found: " + localOrderNumber);
        }

        if ("PENDING_PAYMENT_PAYPAL".equalsIgnoreCase(order.getStatus().name())) {
            order.setStatus(OrderStatus.PAID);
        }

         if (captureId != null) order.setPaymentTransactionId(captureId);
         if (capturedAt != null) order.setPaidTime(capturedAt);
         if (capturedAmount != null) order.setPaidAmount(Float.valueOf(capturedAmount));
         if (capturedCurrency != null) order.setPaidCurrency(capturedCurrency);

        orderService.save(order);
        cartService.deleteByCustomer(customer);

        try {
            orderEmailService.sendOrderConfirmation(customer, order);
        } catch (Exception e) {

        }
        return order;
    }
}