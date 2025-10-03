package com.eshop.client.service.interfaceS;

import com.eshop.client.exception.ProductNotFoundException;
import com.eshop.client.exception.ShoppingCartException;
import com.eshop.common.entity.CartItem;
import com.eshop.common.entity.Customer;

import java.util.List;

public interface ShoppingCartService {
    Integer addProductToCart(Integer productId, Integer quantity, Customer customer) throws ShoppingCartException, ProductNotFoundException;
    List<CartItem> listCartItems(Customer customer);
    float updateQuantity(Integer productId, Integer quantity, Customer customer) throws ShoppingCartException, ProductNotFoundException;
    void removeProduct(Integer productId, Customer customer);
    void deleteByCustomer(Customer customer);
}
