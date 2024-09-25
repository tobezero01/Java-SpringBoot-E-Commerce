package com.eshop.admin.brand;

import com.eshop.admin.exception.BrandNotFoundException;
import com.eshop.admin.paging.PagingAndSortingHelper;
import com.eshop.common.entity.Brand;
import com.eshop.common.entity.User;
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
public class BrandService {
    public static final int BRANDS_PER_PAGE = 5;
    @Autowired
    private BrandRepository brandRepository;

    public List<Brand> listAll() {
        return brandRepository.findAll();
    }

    public void listByPage(int pageNum , PagingAndSortingHelper helper) {
        Sort sort = Sort.by(helper.getSortField());
        sort = helper.getSortDir().equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum-1, BRANDS_PER_PAGE, sort);
        Page<Brand> page = null;
        if(helper.getKeyWord() != null) {
            page = brandRepository.findAll(helper.getKeyWord() , pageable);
        } else {
            page = brandRepository.findAll(pageable);
        }
        helper.updateModelAttributes(pageNum, page);
    }

    public Brand save(Brand brand) {
        return brandRepository.save(brand);
    }

    public Brand get(Integer id) throws BrandNotFoundException {
        try {
            return brandRepository.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new BrandNotFoundException("Could not find any brand with ID : " + id);
        }
    }

    public void delete(Integer id) throws BrandNotFoundException {
        Long countById = brandRepository.countById(id);

        if(countById == null || countById == 0) {
            throw new BrandNotFoundException("Could not find any brand with ID : " + id);
        }

        brandRepository.deleteById(id);
    }

    public String checkUnique(Integer id, String name) {
        boolean isCreatingNew = (id == null || id == 0);

        Brand brandByName = brandRepository.findByName(name);

        if(isCreatingNew) {
            if(brandByName != null) return "Duplicate";
        } else {
            if( brandByName != null && brandByName.getId() != id) {
                return "Duplicate";
            }
        }
        return "OK";
    }
}
