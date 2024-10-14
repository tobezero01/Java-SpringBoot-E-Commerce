package com.eshop.admin.order;

import com.eshop.admin.exception.OrderNotFoundException;
import com.eshop.admin.paging.PagingAndSortingHelper;
import com.eshop.admin.paging.PagingAndSortingParam;
import com.eshop.admin.security.EShopUserDetails;
import com.eshop.admin.setting.SettingService;
import com.eshop.common.entity.Country;
import com.eshop.common.entity.order.Order;
import com.eshop.common.entity.order.OrderDetail;
import com.eshop.common.entity.order.OrderStatus;
import com.eshop.common.entity.order.OrderTrack;
import com.eshop.common.entity.product.Product;
import com.eshop.common.entity.setting.Setting;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

@Controller
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private SettingService settingService;

    String defaultRedirect = "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";

    @GetMapping("/orders")
    public String listFirstPage() {
        return defaultRedirect;
    }

    @GetMapping("/orders/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum,
                             @PagingAndSortingParam(listName = "listOrders") PagingAndSortingHelper helper,
                             HttpServletRequest request,
                             @AuthenticationPrincipal EShopUserDetails loggedUser
    ) {
        orderService.listByPage(pageNum, helper);
        loadCurrencySetting(request);

        if (loggedUser.hasRole("Shipper") &&
                !loggedUser.hasRole("Admin") &&
                !loggedUser.hasRole("Salesperson")) {
            return "orders/orders_shipper";
        }

        return "orders/orders";
    }

    private void loadCurrencySetting(HttpServletRequest request) {
        List<Setting> currencySettings = settingService.getCurrencySettings();
        for (Setting setting : currencySettings) {
            request.setAttribute(setting.getKey(), setting.getValue());
        }
    }

    @GetMapping("/orders/detail/{id}")
    public String viewDetails(@PathVariable("id") Integer id, Model model,
                              RedirectAttributes redirectAttributes, HttpServletRequest request) {
        try {
            Order order = orderService.get(id);
            loadCurrencySetting(request);
            model.addAttribute("order", order);
            return "orders/order_details_modal";
        } catch (OrderNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return defaultRedirect;
        }
    }

    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable("id") Integer id, Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            orderService.delete(id);
            redirectAttributes.addFlashAttribute("message", "The order ID = " + id + " has been deleted");
        } catch (OrderNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return defaultRedirect;
        }
        return defaultRedirect;
    }

    @GetMapping("/orders/edit/{id}")
    public String editOrder(@PathVariable("id") Integer id, Model model,
                            RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.get(id);
            List<Country> listCountries = orderService.listAllCountries();
            model.addAttribute("listCountries", listCountries);
            model.addAttribute("order", order);
            model.addAttribute("pageTitle", "Edit Order (ID = " + id + " )");

            return "orders/order_form";
        } catch (OrderNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
            return defaultRedirect;
        }
    }

    @PostMapping("/orders/save")
    public String saveOrder(Order order, HttpServletRequest request, RedirectAttributes redirectAttributes) throws ParseException {
        String countryName = request.getParameter("countryName");
        order.setCountry(countryName);
        updateProductDetails(order, request);
        updateOrderTracks(order, request);
        orderService.save(order);
        redirectAttributes.addFlashAttribute("message", "The Order ID " + order.getId() + " has been updated successfully");

        return defaultRedirect;
    }

    private void updateOrderTracks(Order order, HttpServletRequest request) {
        String[] trackIds = request.getParameterValues("trackId");
        String[] trackStatuses = request.getParameterValues("trackStatus");
        String[] trackDates = request.getParameterValues("trackDate");
        String[] trackNotes = request.getParameterValues("trackNotes");

        List<OrderTrack> orderTracks = order.getOrderTracks();
        DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");


        // Kiểm tra xem trackIds có phải là null không
        if (trackIds != null && trackStatuses != null && trackIds.length > 0  ) {
            for (int i = 0; i < trackIds.length; i++) {
                OrderTrack trackRecord = new OrderTrack();
                Integer trackId = Integer.parseInt(trackIds[i]);
                if (trackId > 0) trackRecord.setId(trackId);

                trackRecord.setOrder(order);
                trackRecord.setStatus(OrderStatus.valueOf(trackStatuses[i]));
                trackRecord.setNotes(trackNotes[i]);
                try {
                    trackRecord.setUpdatedTime(formatDate.parse(trackDates[i]));
                } catch (ParseException exception) {
                    exception.printStackTrace();
                }
                orderTracks.add(trackRecord);
            }
        } else {
            // Nếu không có track nào, có thể thêm một track mặc định
            OrderTrack trackRecord = new OrderTrack();
            trackRecord.setOrder(order);
            trackRecord.setStatus(OrderStatus.NEW);
            trackRecord.setNotes("NEW");
            orderTracks.add(trackRecord);
        }
    }


    private void updateProductDetails(Order order, HttpServletRequest request) {
        String[] detailIds = request.getParameterValues("detailId");
        String[] productIds = request.getParameterValues("productId");
        String[] productDetailCosts = request.getParameterValues("productDetailCost");
        String[] productPrices = request.getParameterValues("productPrice");
        String[] quantities = request.getParameterValues("quantity");
        String[] productSubtotals = request.getParameterValues("productSubtotal");
        String[] shippingCosts = request.getParameterValues("shippingCost");

        Set<OrderDetail> orderDetails = order.getOrderDetails();

        for (int i = 0; i < detailIds.length; i++) {
            OrderDetail orderDetail = new OrderDetail();
            Integer detailId = Integer.parseInt(detailIds[i]);
            if (detailId > 0) {
                orderDetail.setId(detailId);
            }

            orderDetail.setOrder(order);
            orderDetail.setProduct(new Product(Integer.parseInt(productIds[i])));
            orderDetail.setProductCost(Float.parseFloat(productDetailCosts[i]));
            orderDetail.setSubtotal(Float.parseFloat(productSubtotals[i]));
            orderDetail.setUnitPrice(Float.parseFloat(productPrices[i]));
            orderDetail.setShippingCost(Float.parseFloat(shippingCosts[i]));
            orderDetail.setQuantity(Integer.parseInt(quantities[i]));

            orderDetails.add(orderDetail);
        }
    }
}


