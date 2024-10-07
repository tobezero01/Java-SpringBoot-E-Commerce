package com.eshop.checkout;

import jakarta.persistence.Transient;

import java.util.Calendar;
import java.util.Date;

public class CheckoutInfo {

    private float productCost;
    private float productTotal;
    private float shippingCostTotal;
    private float paymentMethod;
    private int deliverDays;
    private boolean sodSupported;

    public float getProductCost() {
        return productCost;
    }

    public void setProductCost(float productCost) {
        this.productCost = productCost;
    }

    @Transient
    public float getProductTotal() {
        return productTotal;
    }

    public void setProductTotal(float productTotal) {
        this.productTotal = productTotal;
    }

    @Transient
    public float getShippingCostTotal() {
        return shippingCostTotal;
    }

    @Transient
    public float getPaymentTotal() {
        return productTotal + shippingCostTotal;
    }

    public void setShippingCostTotal(float shippingCostTotal) {
        this.shippingCostTotal = shippingCostTotal;
    }

    public float getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(float paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public int getDeliverDays() {
        return deliverDays;
    }

    public void setDeliverDays(int deliverDays) {
        this.deliverDays = deliverDays;
    }

    @Transient
    public Date getDeliverDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, deliverDays);
        return calendar.getTime();
    }

    @Transient
    public boolean isSodSupported() {
        return sodSupported;
    }

    public void setSodSupported(boolean sodSupported) {
        this.sodSupported = sodSupported;
    }
}
