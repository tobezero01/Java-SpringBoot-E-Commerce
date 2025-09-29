package com.eshop.client.controller;

import com.eshop.client.dto.CartSummaryDTO;
import com.eshop.client.dto.request.AddToCartRequest;
import com.eshop.client.dto.request.UpdateQuantityRequest;
import com.eshop.client.dto.response.CartActionResponse;
import com.eshop.client.exception.CustomerNotFoundException;
import com.eshop.client.exception.ProductNotFoundException;
import com.eshop.client.exception.ShoppingCartException;
import com.eshop.client.helper.ControllerHelper;
import com.eshop.client.mapper.CartMapper;
import com.eshop.client.service.AddressService;
import com.eshop.client.service.ShippingRateService;
import com.eshop.client.service.ShoppingCartService;
import com.eshop.common.entity.Address;
import com.eshop.common.entity.CartItem;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.ShippingRate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartRestController {
    private final ShoppingCartService cartService;
    private final ControllerHelper helper;
    private final AddressService addressService;
    private final ShippingRateService shippingRateService;

    /** GET /api/cart : Danh sách item trong giỏ + tổng tạm tính + shippingSupported */
    @GetMapping
    public ResponseEntity<CartSummaryDTO> getCart() throws CustomerNotFoundException {
        Customer customer = helper.requireAuthenticatedCustomer(); // ex 401
        List<CartItem> items = cartService.listCartItems(customer);

        float estimatedTotal = 0f;
        for (CartItem item : items) {
            estimatedTotal += item.getSubtotal();
        }

        //nếu có defaultAddress -> rate theo address, ngược lại theo customer
        Address defaultAddress = addressService.getDefaultAddress(customer);
        ShippingRate rate = (defaultAddress != null)
                ? shippingRateService.getShippingRateForAddress(defaultAddress)
                : shippingRateService.getShippingRateForCustomer(customer);

        boolean shippingSupported = (rate != null);
        return ResponseEntity.ok(new CartSummaryDTO(
                items.stream().map(CartMapper :: toDto).toList(),
                estimatedTotal,
                shippingSupported)
        );
    }

    /** POST /api/cart/items : thêm sản phẩm vào giỏ */
    @PostMapping("/items")
    public ResponseEntity<CartActionResponse> addToCart(@RequestBody AddToCartRequest request) throws ShoppingCartException, CustomerNotFoundException, ProductNotFoundException {
        Customer customer = helper.requireAuthenticatedCustomer();
        Integer updatedQuantity = cartService.addProductToCart(request.productId(), request.quantity(), customer);
        float subtotal = cartService.updateQuantity(request.productId(), updatedQuantity, customer);

        return ResponseEntity.ok(new CartActionResponse(request.productId(), updatedQuantity,
                 subtotal, "Add to cart")
        );
    }

    /** PATCH /api/cart/items/{productId} : cập nhật số lượng */
    @PatchMapping("/items/{productId}")
    public ResponseEntity<CartActionResponse> updateQuantity(@PathVariable Integer productId,
                                             @RequestBody UpdateQuantityRequest request) throws CustomerNotFoundException, ShoppingCartException, ProductNotFoundException {
        Customer customer = helper.requireAuthenticatedCustomer();
        float subtotal = cartService.updateQuantity(productId, request.quantity(), customer);
        return ResponseEntity.ok(new CartActionResponse(productId, request.quantity(), subtotal,
                "Quantity updated")
        );
    }

    /** DELETE /api/cart/items/{productId} : xoá 1 sản phẩm khỏi giỏ */
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartActionResponse> removeItem(@PathVariable Integer productId) throws CustomerNotFoundException {
        Customer customer = helper.requireAuthenticatedCustomer();
        cartService.removeProduct(productId, customer);
        return ResponseEntity.ok(new CartActionResponse(productId, 0, 0f, "Item removed")
        );
    }

    /** DELETE /api/cart : xoá toàn bộ giỏ của current user */
    @DeleteMapping
    public ResponseEntity<CartActionResponse> clearCart() throws CustomerNotFoundException {
        Customer customer = helper.requireAuthenticatedCustomer();
        cartService.deleteByCustomer(customer);
        return ResponseEntity.ok(new CartActionResponse(null, 0, 0f, "Cart cleared"));
    }
}
