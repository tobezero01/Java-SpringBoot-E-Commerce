package com.eshop.client.controller;

import com.eshop.client.dto.ProductDTO;
import com.eshop.client.dto.response.PageResponse;
import com.eshop.client.exception.ProductNotFoundException;
import com.eshop.client.mapper.ProductMapper;
import com.eshop.client.service.interfaceS.ProductService;
import com.eshop.common.entity.product.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductRestController {
    private final ProductService productService;

    @GetMapping("/by-category/{categoryId}")
    public List<ProductDTO> byCategory(@PathVariable Integer categoryId) {
        return  productService.listByCategory(categoryId)
                .stream().map(ProductMapper::toDto).toList();

    }

    /** Chi tiết sản phẩm theo alias (public). */
    @GetMapping("/alias/{alias}")
    public ProductDTO byAlias(@PathVariable String alias) throws ProductNotFoundException {
        Product product = productService.getProduct(alias);
        return ProductMapper.toDto(product);
    }

    /** Chi tiết sản phẩm theo id (public). */
    @GetMapping("/{id}")
    public ProductDTO byId(@PathVariable Integer id) throws ProductNotFoundException {
        Product p = productService.getProduct(id);
        return ProductMapper.toDto(p);
    }

    @GetMapping("/search")
    public PageResponse<ProductDTO> search(@RequestParam("keyword") String keyword,
                                           @RequestParam(defaultValue = "1") int page) {
        Page<Product> pg = productService.search(keyword, page);
        return new PageResponse<>(
                page ,
                pg.getSize(),
                pg.getTotalElements(),
                pg.getTotalPages(),
                pg.getContent().stream().map(ProductMapper::toDto).toList()
        );
    }
}
