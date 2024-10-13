package com.eshop.common.entity.order;

import jakarta.persistence.*;
import org.springframework.data.domain.Page;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "order_track")
public class OrderTrack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 256)
    private String notes;

    private Date updatedTime;

    @Enumerated(EnumType.STRING)
    @Column(length = 45, nullable = false)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Transient
    public String getUpdatedTimeInForm() {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        return format.format(this.updatedTime);
    }

    public void setUpdatedTimeInForm(String dataString) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        try {
            this.updatedTime = format.parse(dataString);
        } catch (ParseException exception) {
            exception.printStackTrace();
        }
    }
}
