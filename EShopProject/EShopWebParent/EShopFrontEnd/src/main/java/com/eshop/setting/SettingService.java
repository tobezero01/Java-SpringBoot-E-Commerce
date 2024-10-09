package com.eshop.setting;

import com.eshop.common.entity.Currency;
import com.eshop.common.entity.setting.Setting;
import com.eshop.common.entity.setting.SettingCategory;
import com.eshop.setting.repository.CurrencyRepository;
import com.eshop.setting.repository.SettingRepository;
import com.eshop.setting.settingBag.CurrencySettingBag;
import com.eshop.setting.settingBag.EmailSettingBag;
import com.eshop.setting.settingBag.PaymentSettingBag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SettingService {

    @Autowired
    private SettingRepository settingRepository;

    @Autowired
    private CurrencyRepository currencyRepository;


    public List<Setting> getGeneralSettings() {
        return settingRepository.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
    }

    public void saveAll(Iterable<Setting> settings) {
        settingRepository.saveAll(settings);
    }

    public EmailSettingBag getEmailSettings() {
        List<Setting> settings = settingRepository.findByCategory(SettingCategory.MAIL_SERVER);
        settings.addAll(settingRepository.findByCategory(SettingCategory.MAIL_TEMPLATES));

        return new EmailSettingBag(settings);
    }

    public CurrencySettingBag getCurrencySettings() {
        List<Setting> settings = settingRepository.findByCategory(SettingCategory.CURRENCY);
        return new CurrencySettingBag(settings);
    }

    public PaymentSettingBag getPaymentSettings() {
        List<Setting> settings = settingRepository.findByCategory(SettingCategory.PAYMENT);
        return new PaymentSettingBag(settings);
    }

    public String getCurrencyCode() {
        Setting setting = settingRepository.findByKey("CURRENCY_ID");
        Integer currencyId = Integer.parseInt(setting.getValue());
        Currency currency = currencyRepository.findById(currencyId).get();
        return currency.getCode();
    }
}
