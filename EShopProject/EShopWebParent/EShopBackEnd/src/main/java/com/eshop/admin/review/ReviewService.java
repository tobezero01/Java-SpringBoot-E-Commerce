package com.eshop.admin.review;

import com.eshop.admin.brand.BrandRepository;
import com.eshop.admin.exception.ReviewNotFoundException;
import com.eshop.admin.paging.PagingAndSortingHelper;
import com.eshop.common.entity.Brand;
import com.eshop.common.entity.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ReviewService {
    public static final int REVIEW_PER_PAGE = 5;
    @Autowired
    private ReviewRepository reviewRepository;

    public void listByPage(int pageNum , PagingAndSortingHelper helper) {
        Sort sort = Sort.by(helper.getSortField());
        sort = helper.getSortDir().equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum-1, REVIEW_PER_PAGE, sort);
        Page<Review> page = null;
        if(helper.getKeyWord() != null) {
            page = reviewRepository.findAll(helper.getKeyWord() , pageable);
        } else {
            page = reviewRepository.findAll(pageable);
        }
        helper.updateModelAttributes(pageNum, page);
    }

    public Review get(Integer id) throws ReviewNotFoundException {
        try {
            return reviewRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new ReviewNotFoundException("Could not find any review with ID " + id);
        }
    }

    public void save(Review reviewInForm) {
        Review reviewInDB = reviewRepository.findById(reviewInForm.getId()).get();
        reviewInDB.setHeadLine(reviewInForm.getHeadLine());
        reviewInDB.setComment(reviewInForm.getComment());
        reviewRepository.save(reviewInDB);
    }

    public void delete(Integer id) throws ReviewNotFoundException {
        if (!reviewRepository.existsById(id) ) {
            throw new  ReviewNotFoundException("Could not find any review with ID " + id);
        }
        reviewRepository.deleteById(id);
    }
}
