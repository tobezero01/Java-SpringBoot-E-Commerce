package com.eshop.review;

import com.eshop.common.entity.Customer;
import com.eshop.common.entity.Review;
import com.eshop.common.entity.product.Product;
import com.eshop.exception.ReviewNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.PublicKey;

@Service
@Transactional
public class ReviewService {
    public static final int REVIEWS_PER_PAGE = 5;
    @Autowired
    private ReviewRepository reviewRepository;

    public Page<Review> listByCustomerByPage(Customer customer, String keyWord, int pageNum,
                                             String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum -1, REVIEWS_PER_PAGE, sort);
        if (keyWord != null) {
            return reviewRepository.findByCustomer(customer.getId(), keyWord, pageable);
        }
        return reviewRepository.findByCustomer(customer.getId(), pageable);
    }

    public Review getByCustomerAndId(Customer customer, Integer reviewId) throws ReviewNotFoundException {
        Review review = reviewRepository.findCustomerAndId(customer.getId(), reviewId);
        if (review == null) {
            throw new ReviewNotFoundException("Customer doesn't have any reviews with ID = " + reviewId);
        }
        return review;
    }

    public Page<Review> list3MostRecentReviewByProduct(Product product) {
        Sort sort = Sort.by("reviewTime").descending();
        Pageable pageable = PageRequest.of(0,3, sort);
        return reviewRepository.findByProduct(product, pageable);
    }
}
