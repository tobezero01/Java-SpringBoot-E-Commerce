package com.eshop.product;

import com.eshop.common.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {
    public static final int PRODUCT_PER_PAGE = 10;
    @Autowired
    private ProductRepository productRepository;

    public Page<Product> listByCategory(int pageNum , Integer categoryId) {
        String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
        Pageable pageable = PageRequest.of(pageNum, PRODUCT_PER_PAGE);

        return productRepository.listByCategory(categoryId, categoryIdMatch, pageable);
    }

}
