package com.eshop.admin.brand;

import com.eshop.admin.brand.BrandRepository;
import com.eshop.common.entity.Brand;
import com.eshop.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class BrandRepositoryTests {

    @Autowired
    private BrandRepository brandRepository;

    @Test
    public void testCreateBrand1 (){
        Category laptops = new Category(6);
        Brand dell = new Brand("DELL");

        dell.getCategories().add(laptops);
        dell.setLogo("default");

        Brand saveBrand = brandRepository.save(dell);
        assertThat(saveBrand).isNotNull();
        assertThat(saveBrand.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateBrand2 (){
        Category cellPhones = new Category(4);
        Category tablet = new Category(7);

        Brand apple = new Brand("apple".toUpperCase());
        apple.getCategories().add(tablet);
        apple.getCategories().add(cellPhones);
        apple.setLogo("default");
        Brand saveBrand = brandRepository.save(apple);
        assertThat(saveBrand).isNotNull();
        assertThat(saveBrand.getId()).isGreaterThan(0);
    }

    @Test
    public void testFindAll() {
        Iterable<Brand> brands = brandRepository.findAll();
        brands.forEach(System.out ::println);

        assertThat(brands).isNotEmpty();
    }

    @Test
    public void testFindById() {
        Brand brand = brandRepository.findById(1).get();
        System.out.println(brand);
        assertThat(brand.getName()).isEqualTo("DELL");
    }

    @Test
    public void testUpdate() {
        Brand brand = brandRepository.findById(1).get();
        brand.setName("DELL VOSTRO");

        Brand saveBrand = brandRepository.save(brand);
        System.out.println(saveBrand);
        assertThat(saveBrand.getName()).isEqualTo("DELL VOSTRO");
    }

    @Test
    public void testDelete() {
        Integer id = 2;
        brandRepository.deleteById(id);
        Optional<Brand> res = brandRepository.findById(id);

        assertThat(res.isEmpty());
    }

}
