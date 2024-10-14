package com.eshop.admin.order.controller;

import com.eshop.admin.paging.PagingAndSortingHelper;
import com.eshop.admin.paging.PagingAndSortingParam;
import com.eshop.admin.product.ProductService;
import com.eshop.common.entity.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProductSearchController {

    @Autowired private ProductService service;

    @GetMapping("/orders/search_product")
    public String showSearchProductPage() {

        return "orders/search_product";
    }

    @PostMapping("/orders/search_product")
    public String searchProducts(String keyWord) {
        return "redirect:/orders/search_product/page/1?sortField=name&sortDir=asc&keyWord=" + keyWord;
    }

    @GetMapping("/orders/search_product/page/{pageNum}")
    public String searchProductsByPage(@PathVariable(name = "pageNum") int pageNum ,
                                       @PagingAndSortingParam(listName = "listProducts") PagingAndSortingHelper helper) {
        service.searchProduct(pageNum, helper);
        return "/orders/search_product";
    }

}
