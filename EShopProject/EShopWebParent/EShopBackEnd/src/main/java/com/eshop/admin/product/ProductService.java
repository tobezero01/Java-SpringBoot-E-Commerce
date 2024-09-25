package com.eshop.admin.product;

import com.eshop.admin.exception.ProductNotFoundException;
import com.eshop.common.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ProductService {
    public static final int PRODUCTS_BY_PAGE = 5;

    @Autowired
    private ProductRepository productRepository;
    public List<Product> listAll() {
        return productRepository.findAll();
    }

    public Page<Product> listByPage(int pageNum, String sortField, String sortDir, String keyWord) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1 , PRODUCTS_BY_PAGE, sort);
        if(keyWord != null) {
            return productRepository.findAll(keyWord, pageable);
        }
        return productRepository.findAll(pageable);
    }

    public Product save(Product product) {
        if(product.getId() == null) {
            product.setCreatedTime(new Date());
        }

        if (product.getAlias() == null || product.getAlias().isEmpty()) {
            String defaultAlias = product.getName().replace(" ", "-");
            product.setAlias(defaultAlias);
        } else {
            product.setAlias(product.getName().replace(" ", "-"));
        }

        product.setUpdatedTime(new Date());
        return productRepository.save(product);
    }

    public void saveProductPrice(Product productInForm) {
        Product productInDB = productRepository.findById(productInForm.getId()).get();
        productInDB.setCost(productInForm.getCost());
        productInDB.setPrice(productInForm.getPrice());
        productInDB.setDiscountPercent(productInForm.getDiscountPercent());

        productRepository.save(productInDB);
    }

    public String checkUnique(Integer id, String name) {
        boolean isCreatingNew = (id == null || id == 0);

        Product productByName = productRepository.findByName(name);

        if(isCreatingNew) {
            if(productByName != null) return "Duplicate";
        } else {
            if( productByName != null && productByName.getId() != id) {
                return "Duplicate";
            }
        }
        return "OK";
    }

    public void updateProductEnabledStatus(Integer id, boolean enabled) {
        productRepository.updateEnabledStatus(id, enabled);
    }

    public void delete(Integer id) throws ProductNotFoundException {
        Long countById = productRepository.countById(id);

        if(countById == null || countById == 0) {
            throw new ProductNotFoundException("Could not find any product with ID: " + id);
        }

        productRepository.deleteById(id);
    }

    public Product get(Integer id) throws ProductNotFoundException {
        return productRepository.findById(id).get();
    }

}
