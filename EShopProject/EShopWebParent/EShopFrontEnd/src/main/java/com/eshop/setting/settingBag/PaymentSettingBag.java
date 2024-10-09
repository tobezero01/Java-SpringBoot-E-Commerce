package com.eshop.setting.settingBag;

import com.eshop.common.entity.setting.Setting;
import com.eshop.common.entity.setting.SettingBag;

import java.util.List;

public class PaymentSettingBag extends SettingBag {
    public PaymentSettingBag(List<Setting> listSettings) {
        super(listSettings);
    }

    public String getBaseUrl() {
        return super.getValue("PAYPAL_API_BASE_URL");
    }

    public String getClientId () {
        return super.getValue("PAYPAL_API_CLIENT_ID");
    }

    public String getClientSecret() {
        return super.getValue("PAYPAL_API_CLIENT_SECRET");
    }
}
