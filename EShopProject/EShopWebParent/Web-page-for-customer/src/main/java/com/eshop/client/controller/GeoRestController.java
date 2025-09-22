package com.eshop.client.controller;

import com.eshop.client.dto.CountryDTO;
import com.eshop.client.dto.StateDTO;
import com.eshop.client.repository.CountryRepository;
import com.eshop.client.repository.StateRepository;
import com.eshop.common.entity.Country;
import com.eshop.common.entity.State;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/geo")
@RequiredArgsConstructor
public class GeoRestController {

    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;

    @GetMapping("/countries")
    public List<CountryDTO> countries() {
        return countryRepository.findAllByOrderByNameAsc()
                .stream().map(c -> new CountryDTO(c.getId(), c.getName(), c.getCode()))
                .toList();
    }

    @GetMapping("/countries/{id}/states")
    public List<StateDTO> statesByCountry(@PathVariable Integer id) {
        List<State> states = stateRepository.findByCountryOrderByNameAsc(new Country(id));
        return states.stream().map(s -> new StateDTO(s.getId(), s.getName())).toList();
    }
}