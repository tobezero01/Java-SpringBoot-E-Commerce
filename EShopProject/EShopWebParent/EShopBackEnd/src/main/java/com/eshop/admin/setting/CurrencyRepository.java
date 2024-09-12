package com.eshop.admin.setting;

import com.eshop.common.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Integer> {

    public List<Currency> findAllByOrderByNameAsc();
}
