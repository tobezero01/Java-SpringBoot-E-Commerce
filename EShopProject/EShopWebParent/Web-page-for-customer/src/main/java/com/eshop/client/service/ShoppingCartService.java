package com.eshop.client.service;

import com.eshop.client.exception.ShoppingCartException;
import com.eshop.client.repository.CartItemRepository;
import com.eshop.client.repository.ProductRepository;
import com.eshop.common.entity.CartItem;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.product.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingCartService {
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    public Integer addProductToCart(Integer productId, Integer quantity, Customer customer) throws ShoppingCartException {
        Integer updatedQuantity = quantity;
        Product product = new Product(productId);
        CartItem cartItem = cartItemRepository.findByCustomerAndProduct(customer, product);

        if (cartItem != null) {
            updatedQuantity = cartItem.getQuantity() + quantity;
            if (updatedQuantity > 5) {
                if (updatedQuantity > 5) {
                    throw new ShoppingCartException("Could not add more " + quantity + " item(s) because there's already " +
                            cartItem.getQuantity() + " item(s) in your shopping cart. Maximum allowed quantity is 5.");
                } else  {
                    cartItem = new CartItem();
                    cartItem.setCustomer(customer);
                    cartItem.setProduct(product);
                }
            }
        }
        cartItem.setQuantity(updatedQuantity);
        cartItemRepository.save(cartItem);
        return updatedQuantity;
    }

    public List<CartItem> listCartItems(Customer customer) {
        return cartItemRepository.findByCustomer(customer);
    }

    public float updateQuantity(Integer productId, Integer quantity, Customer customer) {
        if (quantity > 0)  cartItemRepository.updateQuantity(quantity, customer.getId(), productId);
        Product product = productRepository.findById(productId).get();
        float subtotal = product.getDiscountPrice() * quantity;
        return subtotal;
    }

    public void removeProduct(Integer productId, Customer customer) {
        cartItemRepository.deleteByCustomerAndProduct(customer.getId(), productId);
    }

    public void deleteByCustomer(Customer customer) {
        cartItemRepository.deleteByCustomer(customer.getId());
    }
}
