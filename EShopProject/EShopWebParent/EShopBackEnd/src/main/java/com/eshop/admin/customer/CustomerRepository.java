package com.eshop.admin.customer;

import com.eshop.common.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    @Query("SELECT c FROM Customer c WHERE CONCAT(LOWER(c.email), ' ', LOWER(c.firstName), ' ', LOWER(c.lastName), ' ', " +
            "LOWER(c.addressLine1), ' ', LOWER(c.addressLine2), ' ', LOWER(c.city), ' ', LOWER(c.state), ' ', " +
            "LOWER(c.postalCode), ' ', LOWER(c.country.name)) LIKE LOWER(CONCAT('%', ?1, '%'))")
    Page<Customer> findAll(String keyWord, Pageable pageable);

    @Modifying
    @Query("UPDATE Customer c SET c.enabled = ?2 WHERE c.id = ?1")
    void updateEnabledStatus(Integer id, boolean enabled);

    @Query("SELECT c FROM Customer c WHERE c.email = ?1")
    Customer findByEmail(String email);

    Long countById(Integer id);
}
