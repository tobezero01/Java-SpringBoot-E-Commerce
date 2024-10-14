package com.eshop.admin.product.controller;

import com.eshop.admin.exception.ProductNotFoundException;
import com.eshop.admin.product.ProductDTO;
import com.eshop.admin.product.ProductService;
import com.eshop.common.entity.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductRestController {
    @Autowired
    private ProductService service;

    @PostMapping("/products/check_unique")
    public String checkUnique( Integer id, String name) {
        return service.checkUnique(id, name);
    }

    @GetMapping("/products/get/{id}")
    public ProductDTO getProductInfo(@PathVariable("id") Integer id) throws ProductNotFoundException {
        Product product = service.get(id);
        return new ProductDTO(product.getName(), product.getMainImagePath(), product.getDiscountPrice(), product.getCost());
    }
}
