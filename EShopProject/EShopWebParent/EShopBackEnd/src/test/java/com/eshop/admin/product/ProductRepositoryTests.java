package com.eshop.admin.product;

import com.eshop.common.entity.Brand;
import com.eshop.common.entity.Category;
import com.eshop.common.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateProduct() {
        Brand brand = entityManager.find(Brand.class, 1);

        Category category = entityManager.find(Category.class, 15);

        Product product = new Product();
        product.setName("Samsung galaxy");
        product.setAlias("ss");
        product.setShortDescription("Good smart Phone");
        product.setFullDescription("This is very good smartphone with full feature");

        product.setEnabled(true);
        product.setInStock(true);

        product.setBrand(brand);
        product.setCategory(category);

        product.setPrice(45);
        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());

        Product savedProduct  = productRepository.save(product);

        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isGreaterThan(0);
    }


    @Test
    public void testListAllProducts() {
        Iterable<Product> iterableProduct = productRepository.findAll();
        iterableProduct.forEach(System.out :: println);
    }

    @Test
    public void testGetProduct() {
        Integer id = 1;
        Product product = productRepository.findById(id).get();
        System.out.println(product);

        assertThat(product).isNotNull();
    }

    @Test
    public void testUpdateProduct() {
        Integer id = 1;
        Product product = productRepository.findById(id).get();
        product.setName("DUCNHUAD");
        Product savedProduct = productRepository.save(product);
        System.out.println(product);

        assertThat(product.getName()).isEqualTo("DUCNHUAD");
    }

    @Test
    public void testDeleteProduct() {
        Integer id = 2;
        productRepository.deleteById(id);

        Optional<Product> res = productRepository.findById(id);

        assertThat(!res.isPresent());
    }

    @Test
    public void testSaveProductWithImages() {
        Integer id =1;
        Product product = productRepository.findById(id).get();

        product.setMainImage("main_images");
        product.addExtraImage("extra_images_1");
        product.addExtraImage("extra_images_2");

        Product saveProduct = productRepository.save(product);

        assertThat(saveProduct.getImages().size()).isEqualTo(2);

    }

}
