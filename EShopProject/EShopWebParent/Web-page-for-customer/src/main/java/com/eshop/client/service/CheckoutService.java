package com.eshop.client.service;

import com.eshop.client.helper.CheckoutInfo;
import com.eshop.common.entity.CartItem;
import com.eshop.common.entity.ShippingRate;
import com.eshop.common.entity.product.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckoutService {
    private static final int DIM_DIVISOR = 139; // inch

    public CheckoutInfo prepareCheckout(List<CartItem> cartItems, ShippingRate shippingRate) {

        CheckoutInfo checkoutInfo = new CheckoutInfo();

        float productCost = calculateProductCost(cartItems);
        float productTotal = calculateProductTotal(cartItems);
        float shippingCostTotal = (shippingRate != null) ? calculateShippingCost(cartItems, shippingRate) : 0.0f;

        float paymentTotal = productTotal + shippingCostTotal;

        checkoutInfo.setProductCost(productCost);
        checkoutInfo.setProductTotal(productTotal);
        checkoutInfo.setShippingCostTotal(shippingCostTotal);
        checkoutInfo.setPaymentTotal(paymentTotal);
        checkoutInfo.setDeliverDays(shippingRate != null ? shippingRate.getDays() : 0);
        checkoutInfo.setCodSupported(shippingRate != null && shippingRate.isCodSupported());

        return checkoutInfo;
    }

    private float calculateShippingCost(List<CartItem> cartItems, ShippingRate shippingRate) {
        float shippingCostTotal = 0.0f;
        if (shippingRate == null) return 0.0f;

        for (CartItem item : cartItems) {
            Product product = item.getProduct();

            float length = format(product.getLength());
            float width = format(product.getWidth());
            float height = format(product.getHeight());
            float weight = format(product.getWeight());

            float dimWeight = (length * width * height) / DIM_DIVISOR;
            float finalWeight = Math.max(dimWeight, weight);

            float shippingCost = finalWeight * item.getQuantity() * shippingRate.getRate();
            try {
                item.setShippingCost(shippingCost);
            } catch (Throwable ignore) {}

            shippingCostTotal += shippingCost;

        }
        return shippingCostTotal;
    }

    private float calculateProductTotal(List<CartItem> cartItems) {
        float total = 0.0f;
        for (CartItem item : cartItems) total += item.getSubtotal();
        return total;
    }

    private float calculateProductCost(List<CartItem> cartItems) {
        float cost = 0.0f;
        for (CartItem item : cartItems) {
            cost += item.getQuantity()* item.getProduct().getCost();
        }
        return cost;
    }

    private static float format(Float v) {
        return v == null ? 0f : v;
    }


}
