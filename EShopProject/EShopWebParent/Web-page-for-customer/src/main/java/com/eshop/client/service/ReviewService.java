package com.eshop.client.service;

import com.eshop.client.exception.ReviewNotFoundException;
import com.eshop.client.repository.OrderDetailRepository;
import com.eshop.client.repository.ProductRepository;
import com.eshop.client.repository.ReviewRepository;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.Review;
import com.eshop.common.entity.order.OrderStatus;
import com.eshop.common.entity.product.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


@Service
@RequiredArgsConstructor
public class ReviewService {
    public static final int REVIEWS_PER_PAGE = 5;
    public static final int REVIEWS_BY_PRODUCT_PER_PAGE = 5;

    private final ReviewRepository reviewRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;

    /* ================== LIST BY CUSTOMER ================== */
    public Page<Review> listByCustomerByPage(Customer customer, String keyword, int pageNum,
                                             String sortField, String sortDir) {
        String sf = (sortField == null || sortField.isBlank()) ? "reviewTime" : sortField;
        Sort sort = Sort.by(sf);
        sort = "asc".equalsIgnoreCase(sortDir) ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(Math.max(0, pageNum - 1), REVIEWS_PER_PAGE, sort);
        if (keyword != null && !keyword.isBlank()) {
            return reviewRepository.findByCustomer(customer.getId(), keyword, pageable);
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

    /** 3 review mới nhất của sản phẩm (sắp xếp theo thời gian) */
    public Page<Review> list3MostRecentReviewByProduct(Product product) {
        Sort sort = Sort.by("reviewTime").descending();
        Pageable pageable = PageRequest.of(0, 3, sort);
        return reviewRepository.findByProduct(product, pageable);
    }

    /** Overload theo productId + page/size/sortKey (newest | helpful) */
    public Page<Review> listByProduct(Integer productId, int page, int size, String sortKey) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        Sort sort;
        if ("helpful".equalsIgnoreCase(sortKey)) {
            // nếu entity có trường tổng vote (vd: votes / upVotes - downVotes), chỉnh lại tên cột cho đúng
            sort = Sort.by("votes").descending().and(Sort.by("reviewTime").descending());
        } else {
            // mặc định newest
            sort = Sort.by("reviewTime").descending();
        }
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), Math.max(1, size), sort);
        return reviewRepository.findByProduct(product, pageable);
    }

    public boolean didCustomerReviewProduct(Customer customer, Integer productId) {
        Long count = reviewRepository.countByCustomerAndProduct(customer.getId(), productId);
        return count != null && count > 0;
    }

    /** Chỉ khi đã mua & đã giao (DELIVERED) */
    public boolean canCustomerReviewProduct(Customer customer, Integer productId) {
        Long count = orderDetailRepository
                .countByProductAndCustomerAndOrderStatus(productId, customer.getId(), OrderStatus.DELIVERED);
        return count != null && count > 0;
    }

    public Review createReview(Customer me, Integer productId, int rating, String comment) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        Review r = new Review();
        r.setProduct(product);
        r.setCustomer(me);
        r.setRating(rating);
        r.setComment(comment);
        r.setReviewTime(new Date());
        Review saved = reviewRepository.save(r);
        // cập nhật thống kê sản phẩm
        productRepository.updateReviewCountAndAverageRating(productId);
        return saved;
    }

    public Review updateReview(Customer me, Integer reviewId, int rating, String comment) {
        Review own = reviewRepository.findCustomerAndId(me.getId(), reviewId);
        if (own == null) throw new IllegalArgumentException("Review not found or not owned by current user");
        own.setRating(rating);
        own.setComment(comment);
        own.setReviewTime(new Date());
        Review saved = reviewRepository.save(own);
        productRepository.updateReviewCountAndAverageRating(saved.getProduct().getId());
        return saved;
    }

    public void deleteReview(Customer me, Integer reviewId) {
        Review own = reviewRepository.findCustomerAndId(me.getId(), reviewId);
        if (own == null) throw new IllegalArgumentException("Review not found or not owned by current user");
        Integer productId = own.getProduct().getId();
        reviewRepository.delete(own);
        productRepository.updateReviewCountAndAverageRating(productId);
    }

}
