package com.eshop.admin.shippingrate;

import com.eshop.admin.exception.CustomerNotFoundException;
import com.eshop.admin.exception.ShippingRateAlreadyExistsException;
import com.eshop.admin.exception.ShippingRateNotFoundException;
import com.eshop.admin.paging.PagingAndSortingHelper;
import com.eshop.admin.setting.country.CountryRepository;
import com.eshop.common.entity.Brand;
import com.eshop.common.entity.Country;
import com.eshop.common.entity.ShippingRate;
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
public class ShippingRateService {
    public static final int RATES_PER_PAGE = 7;
    @Autowired
    private ShippingRateRepository shippingRateRepository;

    @Autowired
    private CountryRepository countryRepository;

    public void listByPage(int pageNum , PagingAndSortingHelper helper) {
        Sort sort = Sort.by(helper.getSortField());
        sort = helper.getSortDir().equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum-1, RATES_PER_PAGE, sort);
        Page<ShippingRate> page = null;
        if(helper.getKeyWord() != null) {
            page = shippingRateRepository.findAll(helper.getKeyWord() , pageable);
        } else {
            page = shippingRateRepository.findAll(pageable);
        }
        helper.updateModelAttributes(pageNum, page);
    }

    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public void save(ShippingRate rateInForm) throws ShippingRateAlreadyExistsException {
        ShippingRate rateInDb = shippingRateRepository.findByCountryAndState(
                rateInForm.getCountry().getId() , rateInForm.getState());
        boolean foundExistingRateInNewMode = rateInForm.getId() == null && rateInDb != null;
        boolean foundDifferentExistingRateInNewMode = rateInForm.getId() != null && rateInDb != null;

        if (foundExistingRateInNewMode || foundDifferentExistingRateInNewMode) {
            throw new ShippingRateAlreadyExistsException("There 's a rate for the destination " +
                    rateInForm.getCountry().getName() + " , " + rateInForm.getState());
        }
        shippingRateRepository.save(rateInForm);
    }

    public ShippingRate get(Integer id) throws ShippingRateNotFoundException {
        try {
            return shippingRateRepository.findById(id).get();
        } catch (NoSuchElementException exception) {
            throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
        }
    }

    public void updateCODSupport(Integer id , boolean codSupported) throws ShippingRateNotFoundException {
        Long count = shippingRateRepository.countById(id);

        if (count == null && count == 0) {
            throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
        }
        shippingRateRepository.updateCODSupport(id, codSupported);
    }

    public void delete(Integer id ) throws ShippingRateNotFoundException {
        Long count = shippingRateRepository.countById(id);
        if (count == null && count == 0) {
            throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
        }
        shippingRateRepository.deleteById(id);
    }
}
