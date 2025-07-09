package com.eshop;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ElseServiceController {
    @GetMapping("/contact")
    public String contact() {
        return "service_view/contact";
    }

    @GetMapping("/shippingAndDelivery")
    public String shipping() {
        return "service_view/shippingAndDelivery";
    }

    @GetMapping("/about")
    public String about() {
        return "service_view/about";
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "service_view/privacy";
    }

    @GetMapping("/returnsPolicy")
    public String returns() {
        return "service_view/returns";
    }
}
