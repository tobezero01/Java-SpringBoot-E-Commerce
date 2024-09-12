package com.eshop.admin.setting;

import com.eshop.common.entity.Currency;
import com.eshop.common.entity.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class SettingController {
    @Autowired
    private SettingService settingService;

    @Autowired
    private CurrencyRepository currencyRepository;

    @GetMapping("/settings")
    public String listAll(Model model) {
        List<Setting> listSettings = settingService.listAllSettings();
        List<Currency> listCurrencies = currencyRepository.findAllByOrderByNameAsc();

        model.addAttribute("listCurrencies", listCurrencies);
        for (Setting setting : listSettings) {
            model.addAttribute(setting.getKey(), setting.getValue());
        }
        return "settings/settings";
    }
}
