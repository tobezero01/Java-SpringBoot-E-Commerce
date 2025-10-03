package com.eshop.client.service.interfaceS;

import com.eshop.client.exception.ProductNotFoundException;
import com.eshop.common.entity.product.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    List<Product> listByCategory(Integer categoryId);
    Product getProduct(String alias) throws ProductNotFoundException;
    Product getProduct(Integer id) throws ProductNotFoundException;
    Page<Product> search(String keyWord, int pageNum);
}
