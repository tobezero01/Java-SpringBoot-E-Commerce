package com.eshop.review;

import com.eshop.common.entity.Customer;
import com.eshop.common.entity.Review;
import com.eshop.common.entity.order.OrderStatus;
import com.eshop.common.entity.product.Product;
import com.eshop.exception.ReviewNotFoundException;
import com.eshop.order.OrderDetailRepository;
import com.eshop.product.ProductRepository;
import com.eshop.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


@Service
@Transactional
public class ReviewService {
    public static final int REVIEWS_PER_PAGE = 5;
    public static final int REVIEWS_BY_PRODUCT_PER_PAGE = 5;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private ProductRepository productRepository;

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
        Sort sort = Sort.by("votes").descending();
        Pageable pageable = PageRequest.of(0,3, sort);
        return reviewRepository.findByProduct(product, pageable);
    }

    public Page<Review> listByProduct(Product product, int pageNum, String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1,REVIEWS_BY_PRODUCT_PER_PAGE, sort );

        return reviewRepository.findByProduct(product, pageable);
    }

    public boolean didCustomerReviewProduct(Customer customer, Integer productId) {
        Long count = reviewRepository.countByCustomerAndProduct(customer.getId(), productId);
        return count > 0;
    }

    public boolean canCustomerReviewProduct(Customer customer, Integer productId) {
        Long count = orderDetailRepository.countByProductAndCustomerAndOrderStatus(productId, customer.getId(), OrderStatus.DELIVERED);
        return count > 0;

    }
    
    public Review save(Review review) {
        review.setReviewTime(new Date());
        Review savedReview = reviewRepository.save(review);
        Integer productId = savedReview.getProduct().getId();

        productRepository.updateReviewCountAndAverageRating(productId);
        return savedReview;
    }
}
