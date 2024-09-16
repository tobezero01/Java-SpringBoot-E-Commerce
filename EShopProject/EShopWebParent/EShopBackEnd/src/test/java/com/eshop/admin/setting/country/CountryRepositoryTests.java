package com.eshop.admin.setting.country;

import com.eshop.admin.setting.CurrencyRepository;
import com.eshop.common.entity.Country;
import com.eshop.common.entity.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CountryRepositoryTests {

    @Autowired
    CountryRepository countryRepository;

    @Test
    public void testCreateCountry() {
        Country country = countryRepository.save(new Country("China", "CN"));
        assertThat(country).isNotNull();
        assertThat(country.getId()).isGreaterThan(0);
    }

    @Test
    public void testListCountry() {
        List<Country> countries = countryRepository.findAllByOrderByNameAsc();
        countries.forEach(System.out::println);
        assertThat(countries.size()).isGreaterThan(0);
    }

    // test update  (test later)

    // test delete  (test later)

    // test getDetail  (test later)
}
