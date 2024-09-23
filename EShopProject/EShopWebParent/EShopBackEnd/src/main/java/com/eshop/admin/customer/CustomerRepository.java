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

    @Query("Select c From Customer c Where CONCAT(c.email, ' ', c.firstName, ' ', c.lastName , ' ', "
    + "c.addressLine1, ' ', c.addressLine2, ' ', c.city, ' ', c.state, ' ', "
    + "c.postalCode, ' ', c.country.nam) Like %?1%")
    public Page<Customer> findAll(String keyWord, Pageable pageable);

    @Query("Update Customer c SET c.enabled = ?2 Where c.id = ?1")
    @Modifying
    public void updateEnabledStatus(Integer id, boolean enabled);

    @Query("Select c From Customer c Where c.email = ?1")
    public Customer findByEmail(String email);

    public Long countById(Integer id);
}
