package com.eshop.client.service;

import com.eshop.client.exception.ProductNotFoundException;
import com.eshop.client.repository.ProductRepository;
import com.eshop.common.entity.product.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProductService {
    public static final int SEARCH_RESULT_PER_PAGE = 5;
    private final ProductRepository productRepository;

    public List<Product> listByCategory( Integer categoryId) {
        return productRepository.listByCategory(categoryId);
    }

    public Product getProduct(String alias) throws ProductNotFoundException {
        Product product = productRepository.findByAlias(alias);
        if (product == null) {
            throw new ProductNotFoundException("Product not found with alias " + alias);
        }
        return product;
    }

    public Product getProduct(Integer id) throws ProductNotFoundException {
        try {
            Product product = productRepository.findById(id).get();
            return product;
        } catch (NoSuchElementException ex) {
            throw new ProductNotFoundException("Product not found with ID " + id);
        }

    }

    public Page<Product> search(String keyWord, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum - 1, SEARCH_RESULT_PER_PAGE);
        return productRepository.search(keyWord, pageable);
    }


}
