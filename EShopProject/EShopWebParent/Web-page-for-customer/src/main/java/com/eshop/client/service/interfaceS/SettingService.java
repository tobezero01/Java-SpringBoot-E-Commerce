package com.eshop.client.service.interfaceS;

import com.eshop.client.setting.CurrencySettingBag;
import com.eshop.client.setting.EmailSettingBag;
import com.eshop.client.setting.PaymentSettingBag;
import com.eshop.common.entity.setting.Setting;

import java.util.List;

public interface SettingService {
    List<Setting> getGeneralSettings();
    void saveAll(Iterable<Setting> settings);
    EmailSettingBag getEmailSettings();
    CurrencySettingBag getCurrencySettings();
    PaymentSettingBag getPaymentSettings();
    String getCurrencyCode();
}
