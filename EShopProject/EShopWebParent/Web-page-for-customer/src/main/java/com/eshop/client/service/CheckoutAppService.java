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

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CheckoutAppService {
    private final ShoppingCartService cartService;
    private final AddressService addressService;
    private final ShippingRateService shippingRateService;
    private final CheckoutService checkoutService;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderEmailService orderEmailService;

    /** TÍNH TOÁN TRƯỚC KHI ĐẶT HÀNG */
    public CheckoutSummaryDTO summarize(Customer customer, Integer addressId) {
        List<CartItem> cartItems = cartService.listCartItems(customer);
        if (cartItems.isEmpty()) throw new IllegalArgumentException("Giỏ hàng trống");

        Address address = resolveAddress(customer, addressId);

        ShippingRate rate = (address != null)
                ? shippingRateService.getShippingRateForAddress(address)
                : shippingRateService.getShippingRateForCustomer(customer);

        CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, rate);

        List<CheckoutItemDTO> dtoItems = cartItems.stream().map( i -> {
            Product product = i.getProduct();
            Float unitPrice = (Float.valueOf(product.getDiscountPrice()) != null || product.getDiscountPrice() > 0)
                    ? product.getDiscountPrice()
                    : product.getPrice();

            return new CheckoutItemDTO(
                    product.getId(), product.getName(), product.getAlias(), product.getMainImagePath(),
                    unitPrice, i.getQuantity(), i.getSubtotal()
            );
        }).toList();

        return new CheckoutSummaryDTO(
                dtoItems,
                checkoutInfo.getProductTotal(),
                checkoutInfo.getShippingCostTotal(),
                checkoutInfo.getPaymentTotal(),
                rate != null,
                (address != null ? address.getId() : null),
                address.toString()
        );

    }
    @Transactional
    public PlaceOrderResponse placeOrderCod(Customer customer, PlaceOrderRequest req) throws MessagingException, UnsupportedEncodingException {
        PlaceOrderRequest codRequest = new PlaceOrderRequest(req.addressId(), "COD", req.note());
        CheckoutSummaryDTO sum = summarize(customer, codRequest.addressId());

        Order order = materializeOrder(customer, sum, PaymentMethod.COD, req.note());
        materializeDetails(customer, order);

        // Clear cart ngay với COD
        cartService.deleteByCustomer(customer);
        orderEmailService.sendOrderConfirmation(customer, order);

        return resp(order);
    }

    @Transactional
    public Order createPendingPaypalOrder(Customer customer, Integer addressId, String note) {
        CheckoutSummaryDTO sum = summarize(customer, addressId);
        Order order = materializeOrder(customer, sum, PaymentMethod.PAYPAL, note);
        return orderRepository.save(order);
    }

    private Order materializeOrder(Customer customer, CheckoutSummaryDTO sum,
                                   PaymentMethod pm, String note) {
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomer(customer);
        order.setOrderTime(new Date());
        order.setPaymentMethod(pm);
        order.setStatus(OrderStatus.NEW);
        order.setProductCost(sum.productTotal());
        order.setShippingCost(sum.shippingCost());
        order.setTotal(sum.paymentTotal());
        // gán địa chỉ giao hàng (flatten)
        Address ship = resolveAddress(customer, sum.addressId());
        if (ship != null) {
            order.setFirstName(ship.getFirstName());
            order.setLastName(ship.getLastName());
            order.setPhoneNumber(ship.getPhoneNumber());
            order.setAddressLine1(ship.getAddressLine1());
            order.setAddressLine2(ship.getAddressLine2());
            order.setCity(ship.getCity());
            order.setState(ship.getState());
            order.setPostalCode(ship.getPostalCode());
            order.setCountry(ship.getCountry().toString());
        }
        order.setNote(note);
        return orderRepository.save(order);
    }

    private void materializeDetails(Customer customer, Order order) {
        List<CartItem> cartItems = cartService.listCartItems(customer);
        for (CartItem item : cartItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(item.getProduct());
            Float unitPrice = (Float.valueOf(item.getProduct().getDiscountPrice()) != null)
                    ? item.getProduct().getDiscountPrice() : item.getProduct().getPrice();
            orderDetail.setUnitPrice(unitPrice);
            orderDetail.setQuantity(item.getQuantity());
            orderDetail.setSubtotal(item.getSubtotal());
            orderDetailRepository.save(orderDetail);
        }
    }

    private PlaceOrderResponse resp(Order order){
        return new PlaceOrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus().name(),
                order.getPaymentMethod().name(),
                order.getProductCost(),
                order.getShippingCost(),
                order.getTotal(),
                order.getOrderTime().toInstant()
        );
    }

    private Address resolveAddress(Customer customer, Integer addressId) {
        return (addressId != null)
                ? addressService.get(addressId, customer.getId())
                : addressService.getDefaultAddress(customer);
    }
    private String generateOrderNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String rand = Long.toString(new Random().nextLong(1_000_000L, 36_000_000L), 36).toUpperCase();
        return "OD" + date + "-" + rand.substring(0, 6);
    }
}
