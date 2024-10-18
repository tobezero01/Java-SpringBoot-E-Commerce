package com.eshop.admin.reivew;

import static org.assertj.core.api.Assertions.assertThat;


import com.eshop.admin.review.ReviewRepository;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.Review;
import com.eshop.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    public void testCreateReview() {
        Review review = new Review();
        review.setHeadLine("Great Product!");
        review.setComment("I really loved using this product.");
        review.setRating(5);
        review.setReviewTime(new Date());

        Product product = new Product(1);
        review.setProduct(product);

        Customer customer = new Customer(41);
        review.setCustomer(customer);

        Review savedReview = reviewRepository.save(review);

        assertThat(savedReview).isNotNull();
        assertThat(savedReview.getId()).isGreaterThan(0);
        assertThat(savedReview.getHeadLine()).isEqualTo("Great Product!");
        assertThat(savedReview.getComment()).isEqualTo("I really loved using this product.");
        assertThat(savedReview.getRating()).isEqualTo(5);
        assertThat(savedReview.getProduct().getId()).isEqualTo(1);
        assertThat(savedReview.getCustomer().getId()).isEqualTo(41);
    }


    @Test
    public void testCreateMultipleReviewsForOneCustomer() {
        Customer customer = new Customer(41);

        List<Product> products = new ArrayList<>();
        for (int i = 2; i <= 5; i++) {
            Product product = new Product();
            product.setId(i);
            products.add(product);
        }

        List<Review> reviews = new ArrayList<>();
        for (Product product : products) {
            for (int j = 1; j <= 2; j++) { // Tạo 2 Review cho mỗi Product
                Review review = new Review();
                review.setHeadLine("Review for product " + product.getId());
                review.setComment("This is review #" + j + " for product " + product.getId());
                review.setRating(4 + j % 2);
                review.setReviewTime(new Date());
                review.setProduct(product);
                review.setCustomer(customer);
                reviews.add(review);
            }
        }

        List<Review> savedReviews = reviewRepository.saveAll(reviews);

        assertThat(savedReviews).isNotNull();
        assertThat(savedReviews.size()).isEqualTo(8);

        for (Review review : savedReviews) {
            assertThat(review.getId()).isGreaterThan(0);
            assertThat(review.getCustomer().getId()).isEqualTo(41);
            assertThat(review.getProduct().getId()).isBetween(2, 5);
        }
    }

    @Test
    public void testUpdateReview() {
        Integer id = 2;
        String headLine = "Good Product";
        String comment = "ok";
        Review review = reviewRepository.findById(id).get();
        review.setHeadLine(headLine);
        review.setComment(comment);

        Review savedReview = reviewRepository.save(review);
        assertThat(savedReview.getHeadLine()).isEqualTo(headLine);
        assertThat(savedReview.getComment()).isEqualTo(comment);
    }
}
