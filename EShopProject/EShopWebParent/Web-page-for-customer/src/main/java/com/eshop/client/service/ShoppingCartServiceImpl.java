package com.eshop.client.service;

import com.eshop.client.exception.CartItemNotFoundException;
import com.eshop.client.exception.ProductNotFoundException;
import com.eshop.client.exception.ShoppingCartException;
import com.eshop.client.repository.CartItemRepository;
import com.eshop.client.repository.ProductRepository;
import com.eshop.client.service.interfaceS.ShoppingCartService;
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
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final static Integer MAX_QTY_PER_ITEM = 10;

    @Override
    public Integer addProductToCart(Integer productId, Integer quantity, Customer customer) throws ShoppingCartException, ProductNotFoundException {
        Integer updatedQuantity = quantity;
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Could not find product with id = " + productId));

        if (!product.isInStock()) {
            throw new ShoppingCartException(
                    "Product is Out of Stock"
            );
        }
        CartItem cartItem = cartItemRepository.findByCustomerAndProduct(customer, product);

        if (cartItem == null) {
            updatedQuantity = quantity;
            if (updatedQuantity > MAX_QTY_PER_ITEM) {
                throw new ShoppingCartException(
                        "Could not add more " + quantity + " item(s). Maximum allowed quantity is " + MAX_QTY_PER_ITEM
                );
            }
            cartItem = new CartItem();
            cartItem.setCustomer(customer);
            cartItem.setProduct(product);
        } else {
            // cộng dồn số lượng
            updatedQuantity = cartItem.getQuantity() + quantity;
            if (updatedQuantity > MAX_QTY_PER_ITEM) {
                throw new ShoppingCartException(
                        "Could not add more " + quantity + " item(s) because there's already " +
                                cartItem.getQuantity() + " item(s) in your shopping cart. Maximum allowed quantity is " + MAX_QTY_PER_ITEM
                );
            }
        }
        cartItem.setQuantity(updatedQuantity);
        cartItemRepository.save(cartItem);
        return updatedQuantity;
    }

    @Override
    public List<CartItem> listCartItems(Customer customer) {
        return cartItemRepository.findByCustomer(customer);
    }

    @Override
    public float updateQuantity(Integer productId, Integer quantity, Customer customer) throws ShoppingCartException, ProductNotFoundException {
        if (quantity < 1 || quantity > MAX_QTY_PER_ITEM) {
            throw new ShoppingCartException("Quantity must be between 1 and " + MAX_QTY_PER_ITEM);
        }

        CartItem item = cartItemRepository.findByCustomerIdAndProductId(customer.getId(), productId)
                .orElseThrow(() -> new CartItemNotFoundException(
                        "Product with id " + productId + " is not in your cart"));

        if (!item.getProduct().isInStock()) {
            throw new ShoppingCartException("Product is Out of Stock");
        }
        if (quantity > 0 )  cartItemRepository.updateQuantity(quantity, customer.getId(), productId);
        float subtotal = item.getProduct().getDiscountPrice() * quantity;
        return subtotal;
    }

    @Override
    public void removeProduct(Integer productId, Customer customer) {
        CartItem item = cartItemRepository.findByCustomerIdAndProductId(customer.getId(), productId)
                .orElseThrow(() -> new CartItemNotFoundException(
                        "Product with id " + productId + " is not in your cart. Can't delete."));
        cartItemRepository.deleteByCustomerAndProduct(customer.getId(), productId);
    }

    @Override
    public void deleteByCustomer(Customer customer) {
        cartItemRepository.deleteByCustomer(customer.getId());
    }
}
