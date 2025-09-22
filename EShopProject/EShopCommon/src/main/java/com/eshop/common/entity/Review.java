package com.eshop.common.entity;

import com.eshop.common.entity.product.Product;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 126)
    private String headLine;

    @Column(length = 300, nullable = false)
    private String comment;

    private int rating;

    private int votes;

    @Transient
    private boolean upVotedByCurrentCustomer;

    @Transient
    private boolean downVotedByCurrentCustomer;

    @Column(nullable = false)
    private Date reviewTime;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public Review() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHeadLine() {
        return headLine;
    }

    public void setHeadLine(String headLine) {
        this.headLine = headLine;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Date getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(Date reviewTime) {
        this.reviewTime = reviewTime;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", headLine='" + headLine + '\'' +
                ", comment='" + comment + '\'' +
                ", rating=" + rating +
                ", reviewTime=" + reviewTime +
                ", product=" + product.getShortName() +
                ", customer=" + customer.getFullName() +
                '}';
    }

    public boolean isUpVotedByCurrentCustomer() {
        return upVotedByCurrentCustomer;
    }

    public void setUpVotedByCurrentCustomer(boolean upVotedByCurrentCustomer) {
        this.upVotedByCurrentCustomer = upVotedByCurrentCustomer;
    }

    public boolean isDownVotedByCurrentCustomer() {
        return downVotedByCurrentCustomer;
    }

    public void setDownVotedByCurrentCustomer(boolean downVotedByCurrentCustomer) {
        this.downVotedByCurrentCustomer = downVotedByCurrentCustomer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Review review)) return false;

        return getId().equals(review.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
