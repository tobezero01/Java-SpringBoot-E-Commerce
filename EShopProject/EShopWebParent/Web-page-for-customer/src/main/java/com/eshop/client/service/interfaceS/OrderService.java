package com.eshop.client.service.interfaceS;

import com.eshop.client.dto.request.OrderReturnRequest;
import com.eshop.client.dto.response.OrderReturnResponse;
import com.eshop.client.helper.CheckoutInfo;
import com.eshop.common.entity.Address;
import com.eshop.common.entity.CartItem;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.order.Order;
import com.eshop.common.entity.order.PaymentMethod;
import org.springframework.data.domain.Page;

public interface OrderService {
    Order createOrder(Customer customer, Address address, java.util.List<CartItem> cartItems,
                      CheckoutInfo checkoutInfo, PaymentMethod paymentMethod, String note, String orderNumber);

    Order getByOrderNumberAndCustomer(String orderNumber, Customer customer);

    Page<Order> listForCustomerByPage(Customer customer, int pageNum, String sortField, String sortDir, String keyWord);

    Order getOrder(Integer id, Customer customer);

    boolean canReturn(Order order);

    OrderReturnResponse setOrderReturnRequested(OrderReturnRequest request, Customer customer);

    String buildReturnBlockReason(Order order);

    Order save(Order o);
}
