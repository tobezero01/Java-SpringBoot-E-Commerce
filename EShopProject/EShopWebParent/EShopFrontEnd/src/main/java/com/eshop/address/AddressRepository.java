package com.eshop.address;

import com.eshop.common.entity.Address;
import com.eshop.common.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address , Integer> {

    public List<Address> findByCustomer(Customer customer);

    @Query("Select a from Address a Where a.id = ?1 And a.customer.id = ?2")
    public Address findByIdAndCustomer(Integer addressId , Integer customerId);

    @Query("Delete From Address a Where a.id = ?1 And a.customer.id = ?2")
    @Modifying
    public void deleteByIdAndCustomer(Integer addressId, Integer customerId);
}
