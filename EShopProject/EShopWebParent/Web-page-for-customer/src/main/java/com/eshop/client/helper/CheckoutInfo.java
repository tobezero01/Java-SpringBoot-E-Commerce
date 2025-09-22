package com.eshop.client.helper;

import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
@RequiredArgsConstructor
public class CheckoutInfo {
    private float productCost;
    private float productTotal;
    private float shippingCostTotal;
    private float paymentTotal;
    private int deliverDays;
    private boolean codSupported;

    @Transient
    public float getProductTotal() {
        return productTotal;
    }

    @Transient
    public float getShippingCostTotal() {
        return shippingCostTotal;
    }

    @Transient
    public float getPaymentTotal() {
        return productTotal + shippingCostTotal;
    }

    @Transient
    public Date getDeliverDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, deliverDays);
        return calendar.getTime();
    }

    @Transient
    public boolean isSodSupported() {
        return codSupported;
    }

    @Transient
    public String getPaymentTotal4Paypal() {
        DecimalFormat format = new DecimalFormat("###,###.##");
        return format.format(paymentTotal);
    }

    public void setSodSupported(boolean sodSupported) {
        this.codSupported = sodSupported;
    }

}
