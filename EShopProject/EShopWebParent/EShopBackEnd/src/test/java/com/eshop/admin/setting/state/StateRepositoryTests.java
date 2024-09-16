package com.eshop.admin.setting.state;

import com.eshop.admin.setting.country.CountryRepository;
import com.eshop.common.entity.Country;
import com.eshop.common.entity.State;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class StateRepositoryTests {

    @Autowired
    StateRepository stateRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void testCreateStateInVietnam() {
        Integer id = 242;
        Country country = entityManager.find(Country.class, id);

        State state = stateRepository.save(new State("Hai Duong",country) );
        assertThat(state).isNotNull();
        assertThat(state.getId()).isGreaterThan(0);
    }


    // test update  (test later)

    // test delete  (test later)

    // test getDetail  (test later)
}
