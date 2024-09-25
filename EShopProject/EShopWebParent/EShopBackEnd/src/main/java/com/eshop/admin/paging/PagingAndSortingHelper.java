package com.eshop.admin.paging;

import com.eshop.admin.user.UserService;
import com.eshop.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

public class PagingAndSortingHelper {
    private ModelAndViewContainer model;
    private String listName;
    private String sortField;
    private String sortDir;
    private String keyWord;


    public PagingAndSortingHelper(ModelAndViewContainer model, String listName, String sortField, String sortDir, String keyWord) {
        this.model = model;
        this.listName = listName;
        this.sortField = sortField;
        this.sortDir = sortDir;
        this.keyWord = keyWord;
    }

    public void updateModelAttributes(int pageNum, Page<?> page) {
        List<?> listItems = page.getContent();
        int pageSize = page.getSize();
        long startCount = (pageNum-1)* pageSize + 1;
        long endCount = startCount + pageSize -1;

        if(endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("currentPage",pageNum);
        model.addAttribute("startCount",startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute(listName, listItems);
    }

    public String getSortField() {
        return sortField;
    }

    public String getSortDir() {
        return sortDir;
    }

    public String getKeyWord() {
        return keyWord;
    }
}
