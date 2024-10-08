package com.eshop.shoppingCart;

import com.eshop.common.entity.CartItem;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    public List<CartItem> findByCustomer(Customer customer);

    public CartItem findByCustomerAndProduct(Customer customer,Product product );

    @Modifying
    @Query("Update CartItem c Set c.quantity = ?1 Where c.customer.id = ?2 And c.product.id = ?3")
    public void updateQuantity(Integer quantity, Integer customerId , Integer productId);

    @Modifying
    @Query("Delete From CartItem c Where c.customer.id = ?1 And c.product.id = ?2")
    public void deleteByCustomerAndProduct(Integer customerId, Integer productId);

    @Query("DELETE CartItem c WHERE c.customer.id = ?1 ")
    @Modifying
    public void deleteByCustomer(Integer customerId);

}
