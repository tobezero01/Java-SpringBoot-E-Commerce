package com.eshop.setting;

import com.eshop.common.entity.Country;
import com.eshop.common.entity.State;
import com.eshop.setting.repository.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class StateRestController {

    @Autowired
    StateRepository stateRepository;

    @GetMapping("/settings/list_states_by_country/{id}")
    public List<StateDTO> listByCountry(@PathVariable("id") Integer countryId) {
        List<State> listStates = stateRepository.findByCountryOrderByNameAsc(new Country(countryId));
        return listStates.stream()
                .map(state -> new StateDTO(state.getId(), state.getName()))
                .collect(Collectors.toList());
    }

}
