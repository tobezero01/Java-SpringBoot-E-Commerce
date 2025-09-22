package com.eshop.client.service;

import com.eshop.client.helper.VoteResult;
import com.eshop.client.helper.VoteType;
import com.eshop.client.repository.ReviewRepository;
import com.eshop.client.repository.ReviewVoteRepository;
import com.eshop.common.entity.Customer;
import com.eshop.common.entity.Review;
import com.eshop.common.entity.ReviewVote;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewVoteService {

    private final ReviewRepository reviewRepository;
    private final ReviewVoteRepository voteRepository;

    /** Trả về +1 (UP), -1 (DOWN), 0 (NONE) cho vote của customer trên 1 review */
    public Integer getMyVote(Integer reviewId, Integer customerId) {
        ReviewVote vote = voteRepository.findByReviewAndCustomer(reviewId, customerId);
        if (vote == null) return 0;
        return vote.getVotes(); // 1, -1
    }

    /** Bọc counters up/down để AppService/Controller dễ trả về DTO */
    public static final class VoteCounters {
        private final int up;
        private final int down;
        public VoteCounters(int up, int down) { this.up = up; this.down = down; }
        public int getUp() { return up; }
        public int getDown() { return down; }
    }

    private VoteCounters counters(Integer reviewId) {
        int up = Math.toIntExact(voteRepository.countUp(reviewId));
        int down = Math.toIntExact(voteRepository.countDown(reviewId));
        return new VoteCounters(up, down);
    }

    /** Thực hiện vote theo boolean up, tạo/sửa/xóa theo quy tắc toggle */
    public VoteCounters vote(Integer reviewId, Integer customerId, boolean up) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review " + reviewId + " not found"));

        ReviewVote existing = voteRepository.findByReviewAndCustomer(reviewId, customerId);
        if (existing != null) {
            if (existing.isVoteUp() && up) {
                // up lại => undo
                voteRepository.delete(existing);
            } else if (existing.isVoteDown() && !up) {
                // down lại => undo
                voteRepository.delete(existing);
            } else {
                // đổi chiều
                if (up) existing.voteUp(); else existing.voteDown();
                voteRepository.save(existing);
            }
        } else {
            ReviewVote v = new ReviewVote();
            v.setCustomer(new Customer()); v.getCustomer().setId(customerId); // tránh hit DB lần nữa
            v.setReview(review);
            if (up) v.voteUp(); else v.voteDown();
            voteRepository.save(v);
        }
        return counters(reviewId);
    }

    /** Bỏ vote (nếu có) */
    public VoteCounters removeVote(Integer reviewId, Integer customerId) {
        voteRepository.deleteByReviewAndCustomer(reviewId, customerId);
        return counters(reviewId);
    }

    /** <== Giữ lại API cũ để không lỗi ở nơi khác (alias) */
    // doVote/undoVote bản cũ (Customer object + VoteType)
    public VoteCounters doVote(Integer reviewId, Customer customer, com.eshop.client.helper.VoteType voteType) {
        return vote(reviewId, customer.getId(), voteType == com.eshop.client.helper.VoteType.UP);
    }
    public VoteCounters undoVote(Integer reviewId, Customer customer) {
        return removeVote(reviewId, customer.getId());
    }

    /** Đánh dấu review đã vote trong danh sách (để UI render “đã vote”) */
    public void markReviewsVotedForProductByCustomer(List<Review> listReviews, Integer productId, Integer customerId) {
        var listVotes = voteRepository.findByProductAndCustomer(productId, customerId);
        for (ReviewVote v : listVotes) {
            Review voted = v.getReview();
            int idx = listReviews.indexOf(voted);
            if (idx >= 0) {
                Review r = listReviews.get(idx);
                if (v.isVoteUp()) r.setUpVotedByCurrentCustomer(true);
                else if (v.isVoteDown()) r.setDownVotedByCurrentCustomer(true);
            }
        }
    }
}
