package com.eshop.shoppingCart;

import com.eshop.common.entity.CartItem;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.Product;
import com.eshop.exception.ShoppingCartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ShoppingCartService {
    @Autowired
    private CartItemRepository cartItemRepository;

    public Integer addProductToCart(Integer productId, Integer quantity , Customer customer) throws ShoppingCartException {
        Integer updatedQuantity = quantity;
        Product product = new Product(productId);
        CartItem cartItem = cartItemRepository.findByCustomerAndProduct(customer, product);

        if (cartItem != null) {
            updatedQuantity = cartItem.getQuantity() + quantity;
            if (updatedQuantity > 5) {
                throw new ShoppingCartException("Could not add more " + quantity + " item(s) because there's already " +
                        cartItem.getQuantity() + " item(s) in your shopping cart. Maximum allowed quantity is 5.");
            }
        } else {
            cartItem = new CartItem();
            cartItem.setCustomer(customer);
            cartItem.setProduct(product);
        }
        cartItem.setQuantity(updatedQuantity);
        cartItemRepository.save(cartItem);
        return updatedQuantity;
    }
}
