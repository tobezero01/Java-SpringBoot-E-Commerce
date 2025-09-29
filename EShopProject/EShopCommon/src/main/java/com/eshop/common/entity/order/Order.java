package com.eshop.common.entity.order;

import com.eshop.common.entity.Address;
import com.eshop.common.entity.Customer;
import jakarta.persistence.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name" , nullable = false , length = 45)
    private String firstName;

    @Column(name = "last_name" , nullable = false , length = 45)
    private String lastName;

    @Column(name = "phone_number" , nullable = false , length = 15)
    private String phoneNumber;

    @Column(name = "address_line_1" , nullable = false , length = 64)
    private String addressLine1;

    @Column(name = "address_line_2" , length = 64)
    private String addressLine2;

    @Column(nullable = false, length = 45)
    private String city;

    @Column(nullable = false, length = 45)
    private String state;

    @Column(name = "postal_code" , nullable = false, length = 10)
    private String postalCode;

    @Column(nullable = false, length = 45)
    private String country;

    private Date orderTime;

    private float shippingCost;
    private float productCost;
    private float subtotal;
    private float tax;
    private float total;
    private int deliverDays;
    private Date deliverDate;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderDetail> orderDetails = new HashSet<>();

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("updatedTime ASC")
    private List<OrderTrack> orderTracks = new ArrayList<>();

    @Column(name = "order_number", length = 64, nullable = false, unique = true)
    private String orderNumber;

    @Column(name = "note", length = 256)
    private String note;

    @Column(name = "payment_transaction_id", length = 64)
    private String paymentTransactionId;

    @Column(name = "paid_time")
    private Date paidTime;

    @Column(name = "paid_amount")
    private Float paidAmount;

    @Column(name = "paid_currency", length = 12)
    private String paidCurrency;

    public Order() {
    }

    public Order(Integer id, Date orderTime, float productCost, float subtotal, float total) {
        this.id = id;
        this.orderTime = orderTime;
        this.productCost = productCost;
        this.subtotal = subtotal;
        this.total = total;
    }

    public void copyAddressFromCustomer() {
        setFirstName(customer.getFirstName());
        setLastName(customer.getLastName());
        setPhoneNumber(customer.getPhoneNumber());
        setAddressLine1(customer.getAddressLine1());
        setAddressLine2(customer.getAddressLine2());
        setCity(customer.getCity());
        setPostalCode(customer.getPostalCode());
        setState(customer.getState());
        setCountry(customer.getCountry().getName());
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<OrderTrack> getOrderTracks() {
        return orderTracks;
    }

    public void setOrderTracks(List<OrderTrack> orderTracks) {
        this.orderTracks = orderTracks;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public float getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(float shippingCost) {
        this.shippingCost = shippingCost;
    }

    public float getProductCost() {
        return productCost;
    }

    public void setProductCost(float productCost) {
        this.productCost = productCost;
    }

    public float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(float subtotal) {
        this.subtotal = subtotal;
    }

    public float getTax() {
        return tax;
    }

    public void setTax(float tax) {
        this.tax = tax;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public int getDeliverDays() {
        return deliverDays;
    }

    public void setDeliverDays(int deliverDays) {
        this.deliverDays = deliverDays;
    }

    public Date getDeliverDate() {
        return deliverDate;
    }

    public void setDeliverDate(Date deliverDate) {
        this.deliverDate = deliverDate;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Set<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public String getPaymentTransactionId() {
        return paymentTransactionId;
    }

    public void setPaymentTransactionId(String paymentTransactionId) {
        this.paymentTransactionId = paymentTransactionId;
    }

    public Date getPaidTime() {
        return paidTime;
    }

    public void setPaidTime(Date paidTime) {
        this.paidTime = paidTime;
    }

    public Float getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Float paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getPaidCurrency() {
        return paidCurrency;
    }

    public void setPaidCurrency(String paidCurrency) {
        this.paidCurrency = paidCurrency;
    }

    @Transient
    public String getDestination() {
        return city + ", "+ state + ", "+ country;
    }

    public void setOrderDetails(Set<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public void copyShippingAddress(Address address) {
        setFirstName(address.getFirstName());
        setLastName(address.getLastName());
        setPhoneNumber(address.getPhoneNumber());
        setAddressLine1(address.getAddressLine1());
        setAddressLine2(address.getAddressLine2());
        setCity(address.getCity());
        setPostalCode(address.getPostalCode());
        setState(address.getState());
        setCountry(address.getCountry().getName());
    }
    @Transient
    public String getAddress() {
        String address = firstName;

        if (lastName != null && !lastName.isEmpty()) address += " " + lastName;
        if (!addressLine1.isEmpty()) address += ", " + addressLine1;
        if (addressLine2 != null && !addressLine2.isEmpty()) address += " " + addressLine2;
        if (!city.isEmpty()) address += ", " + city;
        if (state != null && !state.isEmpty()) address += " " + state;

        address += ", " +  country;

        if (!postalCode.isEmpty()) address += ". Postal Code : " + postalCode;
        if (!phoneNumber.isEmpty()) address += ". Phone Number : " + phoneNumber;
        return address;
    }




    @Transient
    public String getDeliverDateInForm() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(this.deliverDate);
    }

    public void setDeliverDateInForm(String dataString) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            this.deliverDate = dateFormat.parse(dataString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Transient
    public String getProductNames() {
        String productNames = "";
        productNames = "<ul>";

        for (OrderDetail detail : orderDetails) {
            productNames += "<li>" + detail.getProduct().getShortName() + "</li>";
        }

        productNames += "</ul>";
        return productNames;
    }

    @Transient
    public boolean isReturned() {
        return hasStatus(OrderStatus.RETURNED);
    }
    @Transient
    public boolean isReturnRequested() {
        return hasStatus(OrderStatus.RETURN_REQUESTED);
    }
    @Transient
    public boolean isShipping() {
        return hasStatus(OrderStatus.SHIPPING);
    }
    @Transient
    public boolean isPaid() {
        return hasStatus(OrderStatus.PAID);
    }
    @Transient
    public boolean isPackaged() {
        return hasStatus(OrderStatus.PACKAGED);
    }
    @Transient
    public boolean isCanceled() {
        return hasStatus(OrderStatus.CANCELLED);
    }
    @Transient
    public boolean isProcessing() {
        return hasStatus(OrderStatus.PROCESSING);
    }
    @Transient
    public boolean isPicked() {
        return hasStatus(OrderStatus.PICKED);
    }
    @Transient
    public boolean isRefunded() {
        return hasStatus(OrderStatus.REFUNDED);
    }
    @Transient
    public boolean isDelivered() {
        return hasStatus(OrderStatus.DELIVERED);
    }

    public boolean hasStatus(OrderStatus status) {
        for (OrderTrack aTrack : orderTracks) {
            if (aTrack.getStatus().equals(status) ) {
                return true;
            }
        }
        return false;
    }


}
