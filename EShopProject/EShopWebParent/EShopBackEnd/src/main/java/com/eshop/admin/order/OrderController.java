package com.eshop.admin.order;

import com.eshop.admin.paging.PagingAndSortingHelper;
import com.eshop.admin.paging.PagingAndSortingParam;
import com.eshop.admin.setting.SettingService;
import com.eshop.common.entity.Setting;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

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
    public String listByPage(@PathVariable(name = "pageNum") int pageNum ,
                             @PagingAndSortingParam(listName = "listOrders") PagingAndSortingHelper helper,
                             HttpServletRequest request
    ) {
        orderService.listByPage(pageNum,helper );
        loadCurrencySetting(request);
        return "orders/orders";
    }

    private void loadCurrencySetting(HttpServletRequest request) {
        List<Setting> currencySettings = settingService.getCurrencySettings();
        for (Setting setting : currencySettings) {
            request.setAttribute(setting.getKey(), setting.getValue());
        }
    }
}
