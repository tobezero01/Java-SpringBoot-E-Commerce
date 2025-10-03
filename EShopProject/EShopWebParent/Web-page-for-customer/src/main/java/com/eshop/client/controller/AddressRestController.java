package com.eshop.client.controller;

import com.eshop.client.dto.AddressDTO;
import com.eshop.client.dto.request.CreateAddressRequest;
import com.eshop.client.dto.request.UpdateAddressRequest;
import com.eshop.client.exception.CustomerNotFoundException;
import com.eshop.client.helper.ControllerHelper;
import com.eshop.client.mapper.AddressMapper;
import com.eshop.client.service.interfaceS.AddressService;
import com.eshop.common.entity.Address;
import com.eshop.common.entity.Customer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressRestController {
    private final AddressService addressService;
    private final ControllerHelper helper;

    /** GET /api/addresses : danh sách địa chỉ của user */
    @GetMapping
    public ResponseEntity<List<AddressDTO>> listMyAddresses() throws CustomerNotFoundException {
        Customer  customer = helper.requireAuthenticatedCustomer();
        List<AddressDTO> dtos = addressService.listAddressBook(customer).stream()
                .map(AddressMapper :: toDto).toList();

        return ResponseEntity.ok(dtos);
    }

    /** GET /api/addresses/default : địa chỉ mặc định của user */
    @GetMapping("/default")
    public ResponseEntity<AddressDTO> getDefault() throws CustomerNotFoundException {
        Customer customer = helper.requireAuthenticatedCustomer();
        Address defaultAddress = addressService.getDefaultAddress(customer);

        return (defaultAddress == null) ? ResponseEntity.noContent().build()
                                        : ResponseEntity.ok(AddressMapper.toDto(defaultAddress));
    }

    /** POST /api/addresses : tạo địa chỉ mới */
    @PostMapping
    public ResponseEntity<AddressDTO> create(@RequestBody @Valid CreateAddressRequest request) throws CustomerNotFoundException {
        Customer customer = helper.requireAuthenticatedCustomer();
        Address address = AddressMapper.fromCreate(request);
        address.setCustomer(customer);
        addressService.save(address);

        if (Boolean.TRUE.equals(request.defaultForShipping())) {
            addressService.setDefaultAddress(address.getId(), customer.getId());
        }

        AddressDTO dto = AddressMapper.toDto(address);

        return ResponseEntity.created(URI.create("/api/addresses/" + address.getId())).body(dto);
    }

    /** PUT /api/addresses/{id} : cập nhật địa chỉ */
    @PutMapping("/{id}")
    public ResponseEntity<AddressDTO> update(@PathVariable Integer id, @RequestBody @Valid UpdateAddressRequest request) throws CustomerNotFoundException {
        Customer customer = helper.requireAuthenticatedCustomer();

        Address existing = addressService.get(id, customer.getId());

        AddressMapper.applyUpdate(existing, request);
        existing.setCustomer(customer);
        addressService.save(existing);

        // Nếu client muốn set default sau update
        if (Boolean.TRUE.equals(request.defaultForShipping())) {
            addressService.setDefaultAddress(existing.getId(), customer.getId());
        }
        return ResponseEntity.ok(AddressMapper.toDto(existing));
    }

    /** DELETE /api/addresses/{id} : xóa địa chỉ */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) throws CustomerNotFoundException {
        Customer customer = helper.requireAuthenticatedCustomer();
        addressService.delete(id, customer.getId());
        return ResponseEntity.ok().build();
    }

    /** PUT /api/addresses/{id}/default : đặt mặc định  */
    @PutMapping("/{id}/default")
    public ResponseEntity<?> setDefault(@PathVariable Integer id) throws CustomerNotFoundException {
        Customer customer = helper.requireAuthenticatedCustomer();
        addressService.setDefaultAddress(id, customer.getId());
        return ResponseEntity.ok().build();
    }
}
