package com.eshop.product;

import com.eshop.common.entity.product.Product;
import com.eshop.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    public static final int PRODUCT_PER_PAGE = 10;
    public static final int SEARCH_RESULT_PER_PAGE = 5;
    @Autowired
    private ProductRepository productRepository;

    public List<Product> listByCategory( Integer categoryId) {
        return productRepository.listByCategory(categoryId);
    }

    public Product getProduct(String alias) throws ProductNotFoundException {
        Product product = productRepository.findByAlias(alias);
        if (product == null) {
            throw new ProductNotFoundException("Product not found with alias " + alias);
        }
        return product;  // Ensure this return is here
    }

    public Page<Product> search(String keyWord, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum - 1, SEARCH_RESULT_PER_PAGE);
        return productRepository.search(keyWord, pageable);
    }


}
