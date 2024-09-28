package com.eshop.setting;

import com.eshop.common.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {
    public List<Country> findAllByOrderByNameAsc();
    @Query("Select c From Country c where c.code = ?1")
    public Country findByCode(String code);
}
