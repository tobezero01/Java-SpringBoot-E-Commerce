package com.eshop.client.controller;

import com.eshop.client.dto.OrderDetailDTO;
import com.eshop.client.dto.OrderSummaryDTO;
import com.eshop.client.dto.request.OrderReturnRequest;
import com.eshop.client.dto.response.OrderReturnResponse;
import com.eshop.client.dto.response.PageResponse;
import com.eshop.client.dto.response.ReturnCheckResponse;
import com.eshop.client.exception.OrderNotFoundException;
import com.eshop.client.exception.OrderReturnNotAllowedException;
import com.eshop.client.mapper.OrderMapper;
import com.eshop.client.service.CustomerService;
import com.eshop.client.service.OrderService;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.order.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderRestController {

    private final OrderService orderService;
    private final OrderMapper mapper;
    private final CustomerService customerService;

    private Customer me() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return customerService.getCustomerByEmail(email);
    }

    @GetMapping
    public PageResponse<OrderSummaryDTO> list(@RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "orderTime") String sortField,
                                              @RequestParam(defaultValue = "desc") String sortDir,
                                              @RequestParam(required = false) String keyword) {
        Customer customer = me();
        Page<Order> orders = orderService.listForCustomerByPage(customer, page, sortField, sortDir, keyword);
        return PageResponse.of(
                orders.map(mapper::toSummary).getContent(),
                orders.getNumber() + 1, orders.getSize(),
                orders.getTotalElements(), orders.getTotalPages()
        );
    }

    @GetMapping("/{id}")
    public OrderDetailDTO detail(@PathVariable Integer id) {
        Customer customer = me();
        Order order = orderService.getOrder(id, customer);
        if (order == null) throw new RuntimeException("Order not found"); // hoặc @ControllerAdvice chuẩn hóa
        return mapper.toDetail(customer, order);
    }

    @GetMapping("/{id}/returnable")
    public ReturnCheckResponse returnable(@PathVariable Integer id) {
        Customer customer = me();
        Order order = orderService.getOrder(id, customer);
        boolean ok = order != null && orderService.canReturn(order);
        String reason = ok ? null : (order == null ? "Không tìm thấy đơn" : orderService.buildReturnBlockReason(order));
        return new ReturnCheckResponse(ok, reason);
    }

    @PostMapping("/return")
    public OrderReturnResponse requestReturn(@RequestBody OrderReturnRequest request) throws OrderNotFoundException {
        Customer customer = me();
        return orderService.setOrderReturnRequested(request, customer);
    }
}
