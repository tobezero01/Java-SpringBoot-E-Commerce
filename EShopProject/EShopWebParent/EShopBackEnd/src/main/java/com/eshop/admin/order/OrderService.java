package com.eshop.admin.order;

import com.eshop.admin.exception.OrderNotFoundException;
import com.eshop.admin.paging.PagingAndSortingHelper;
import com.eshop.admin.setting.country.CountryRepository;
import com.eshop.common.entity.Country;
import com.eshop.common.entity.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderService {

    private static final int ORDERS_BY_PAGE = 5;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired private CountryRepository countryRepository;

    public void listByPage(int pageNum , PagingAndSortingHelper helper) {
        String sortField = helper.getSortField();
        String sortDir = helper.getSortDir();
        String keyWord = helper.getKeyWord();

        Sort sort = null;
        if ("destination".equals(sortField) ) {
            sort = Sort.by("country").and(Sort.by("state")).and(Sort.by("city"));
        } else {
            sort = Sort.by(sortField);
        }

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum-1, ORDERS_BY_PAGE, sort);
        Page<Order> page = null;
        if(keyWord != null) {
            page = orderRepository.findAll(helper.getKeyWord() , pageable);
        } else {
            page = orderRepository.findAll(pageable);
        }
        helper.updateModelAttributes(pageNum, page);
    }

    public Order get(Integer id) throws OrderNotFoundException {
        return orderRepository.findById(id).get();
    }

    public void delete(Integer id) throws OrderNotFoundException {
        Long count = orderRepository.countById(id);
        if (count == null || count == 0) {
            throw new OrderNotFoundException("Could not find any orders with Id = " + id);
        }
        orderRepository.deleteById(id);
    }

    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public void save(Order orderInForm) {
        Order orderInDB = orderRepository.findById(orderInForm.getId()).get();
        orderInForm.setOrderTime(orderInDB.getOrderTime());
        orderInForm.setCustomer(orderInDB.getCustomer());

        orderRepository.save(orderInForm);
    }
}
