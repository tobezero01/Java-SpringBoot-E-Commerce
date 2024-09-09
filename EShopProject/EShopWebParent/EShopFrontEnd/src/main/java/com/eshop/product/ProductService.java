package com.eshop.product;

import com.eshop.common.entity.Product;
import com.eshop.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductService {
    public static final int PRODUCT_PER_PAGE = 10;
    @Autowired
    private ProductRepository productRepository;

    public List<Product> listByCategory( Integer categoryId) {
        return productRepository.listByCategory(categoryId);
    }

    public Product getProduct(String alias) throws ProductNotFoundException {
        Product product = productRepository.findByAlias(alias);
        if(product == null) {
            throw new ProductNotFoundException("Product not found with alias " + alias);
        }
        return product;
    }

}
