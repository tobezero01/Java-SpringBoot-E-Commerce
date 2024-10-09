package com.eshop.checkout;

import com.eshop.common.entity.CartItem;
import com.eshop.common.entity.ShippingRate;
import com.eshop.common.entity.product.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckoutService {

    private static final int DIM_DIVISOR = 139;

    public CheckoutInfo prepareCheckout(List<CartItem> cartItems, ShippingRate shippingRate) {
        CheckoutInfo checkoutInfo = new CheckoutInfo();
        float productCost = calculateProductCost(cartItems);
        float productTotal = calculateProductTotal(cartItems);
        float shippingCostTotal = calculateShippingCost(cartItems, shippingRate);
        float paymentTotal = productTotal + shippingCostTotal;

        checkoutInfo.setProductCost(productCost);
        checkoutInfo.setProductTotal(productTotal);
        checkoutInfo.setDeliverDays(shippingRate.getDays());
        checkoutInfo.setSodSupported(shippingRate.isCodSupported());
        checkoutInfo.setShippingCostTotal(shippingCostTotal);
        checkoutInfo.setPaymentTotal(paymentTotal);
        return checkoutInfo;
    }

    private float calculateShippingCost(List<CartItem> cartItems, ShippingRate shippingRate) {
        float shippingCostTotal = 0.0f;
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            float dimWeight = (product.getLength()*product.getWeight()*product.getHeight()) / DIM_DIVISOR;
            float finalWeight = product.getWeight() > dimWeight ? product.getWeight() : dimWeight;
            float shippingCost = finalWeight * item.getQuantity() * shippingRate.getRate();

            item.setShippingCost(shippingCost);
            shippingCostTotal += shippingCost;
        }
        return shippingCostTotal;
    }

    private float calculateProductTotal(List<CartItem> cartItems) {
        float total = 0.0f;
        for (CartItem item : cartItems) {
            total += item.getSubtotal() ;
        }
        return total;
    }

    private float calculateProductCost(List<CartItem> cartItems) {
        float cost = 0.0f;
        for (CartItem item : cartItems) {
            cost += item.getQuantity() * item.getProduct().getCost();
        }
        return cost;
    }
}
