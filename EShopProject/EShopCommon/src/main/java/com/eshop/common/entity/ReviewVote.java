package com.eshop.common.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "reviews_votes")
public class ReviewVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private static final int VOTE_UP_POINT = 1;
    private static final int VOTE_DOWN_POINT = -1;

    private int votes;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;

    public ReviewVote() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public void voteUp() {
        this.votes = VOTE_UP_POINT;
    }

    public void voteDown() {
        this.votes = VOTE_DOWN_POINT;
    }

    @Override
    public String toString() {
        return "ReviewVote{" +
                "id=" + id +
                ", votes=" + votes +
                ", customer=" + customer.getFullName() +
                ", review=" + review.getId() +
                '}';
    }

    @Transient
    public boolean isVoteUp() {
        return this.votes == VOTE_UP_POINT;
    }

    @Transient
    public boolean isVoteDown() {
        return this.votes == VOTE_DOWN_POINT;
    }
}
