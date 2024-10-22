package com.eshop.admin.review;

import com.eshop.admin.exception.ReviewNotFoundException;
import com.eshop.admin.paging.PagingAndSortingHelper;
import com.eshop.admin.paging.PagingAndSortingParam;
import com.eshop.common.entity.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    String defaultRedirect = "redirect:/reviews/page/1?sortField=reviewTime&sortDir=desc";

    @GetMapping("/reviews")
    public String listFirstPage() {
        return defaultRedirect;
    }

    @GetMapping("/reviews/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum,
                             @PagingAndSortingParam(listName = "listReviews") PagingAndSortingHelper helper

    ) {
        reviewService.listByPage(pageNum, helper);
        return "reviews/reviews";
    }

    @GetMapping("/reviews/detail/{id}")
    public String viewReview(@PathVariable("id") Integer id, Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            Review review = reviewService.get(id);
            model.addAttribute("review", review);
            return "reviews/review_detail_modal";
        } catch (ReviewNotFoundException e) {
            redirectAttributes.addFlashAttribute(e.getMessage());
            return defaultRedirect;
        }
    }

    @GetMapping("/reviews/edit/{id}")
    public String editReview(@PathVariable("id") Integer id, Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            Review review = reviewService.get(id);
            model.addAttribute("review", review);
            model.addAttribute("pageTitle",String.format( "Edit review (ID : %d)", id));
            return "reviews/review_form";
        } catch (ReviewNotFoundException e) {
            redirectAttributes.addFlashAttribute(e.getMessage());
            return defaultRedirect;
        }
    }

    @PostMapping("/reviews/save")
    public String save(Review reviewInForm , RedirectAttributes redirectAttributes) {
        reviewService.save(reviewInForm);
        redirectAttributes.addFlashAttribute("message", "The review ID " + reviewInForm.getId() + " has been save successfully" );
        return defaultRedirect;
    }
    @GetMapping("/reviews/delete/{id}")
    public String delete(@PathVariable("id") Integer id,
                         RedirectAttributes redirectAttributes) {
        try {
            reviewService.delete(id);
            redirectAttributes.addFlashAttribute("message", "The review ID " + id+ " has been deleted successfully" );
        } catch (ReviewNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage() );
        }
        return defaultRedirect;
    }






}
