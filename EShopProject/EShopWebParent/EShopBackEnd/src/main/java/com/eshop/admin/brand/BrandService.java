package com.eshop.admin.brand;

import com.eshop.common.entity.Brand;
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

    public Page<Brand> listByPage(int pageNum, String sortField, String sortDir, String keyWord) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1 , BRANDS_PER_PAGE, sort);
        if(keyWord != null) {
            return brandRepository.findAll(keyWord, pageable);
        }
        return brandRepository.findAll(pageable);
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
