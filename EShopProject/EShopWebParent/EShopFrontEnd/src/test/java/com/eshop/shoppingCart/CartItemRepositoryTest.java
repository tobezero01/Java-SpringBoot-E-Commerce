package com.eshop.shoppingCart;

import com.eshop.common.entity.CartItem;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.registerCustomDateFormat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CartItemRepositoryTest {

    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private TestEntityManager entityManager;

    @Test
    public void testSaveItem() {
        Integer customerId = 1;
        Integer productId = 1;

        Customer customer = entityManager.find(Customer.class, customerId);
        Product product = entityManager.find(Product.class, productId);

        CartItem cartItem = new CartItem();
        cartItem.setCustomer(customer);
        cartItem.setProduct(product);
        cartItem.setQuantity(1);

        CartItem savedCart = cartItemRepository.save(cartItem);

        assertThat(savedCart.getId()).isGreaterThan(0);
    }


    @Test
    public void testSave2Item() {
        Integer customerId = 10;
        Integer productId = 10;

        Customer customer = entityManager.find(Customer.class, customerId);
        Product product = entityManager.find(Product.class, productId);

        CartItem cartItem1 = new CartItem();
        cartItem1.setCustomer(customer);
        cartItem1.setProduct(product);
        cartItem1.setQuantity(1);

        CartItem cartItem2 = new CartItem();
        cartItem2.setCustomer(new Customer(customerId));
        cartItem2.setProduct(new Product(8));
        cartItem2.setQuantity(3);

        Iterable<CartItem> items = cartItemRepository.saveAll(List.of(cartItem1, cartItem2));
        assertThat(items).size().isGreaterThan(0);
    }

    @Test
    public void testFindByCustomer() {
        Integer cusId = 10;
        List<CartItem> listItems = cartItemRepository.findByCustomer(new Customer(cusId));
        listItems.forEach(System.out::println);
        assertThat(listItems.size()).isEqualTo(2);
    }

    @Test
    public void testFindByCustomerAndProduct() {
        Integer cusId = 1;
        Integer productId = 1;
        CartItem item = cartItemRepository.findByCustomerAndProduct(new Customer(cusId) , new Product(productId));
        assertThat(item).isNotNull();
        System.out.println(item);
    }

    // test update quantity


    // test delete by customer and product
}
