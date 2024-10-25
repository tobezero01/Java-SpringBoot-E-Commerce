package com.eshop.order;

import com.eshop.Utility;
import com.eshop.common.entity.Customer;
import com.eshop.customer.CustomerService;
import com.eshop.exception.CustomerNotFoundException;
import com.eshop.exception.OrderNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderRestController {

    @Autowired private OrderService orderService;
    @Autowired private CustomerService customerService;

    @PostMapping("/orders/return")
    public ResponseEntity<?> handlerOrderReturnRequest(@RequestBody OrderReturnRequest returnRequest,
                                                       HttpServletRequest servletRequest) {
        Customer customer = null;

        try {
            customer = getAuthenticatedCustomer(servletRequest);
        } catch (CustomerNotFoundException exception) {
            return new ResponseEntity<>("Authenticated required", HttpStatus.BAD_REQUEST);
        }

        try {
            orderService.setOrderReturnRequested(returnRequest, customer);
        } catch (OrderNotFoundException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new OrderReturnResponse(returnRequest.getOrderId()), HttpStatus.OK);
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String email = Utility.getMailOfAuthenticatedCustomer(request);
        if (email == null) {
            throw new CustomerNotFoundException("No Authenticated Found");
        }
        return customerService.getCustomerByEmail(email);
    }
}
