package com.eshop.setting;

import com.eshop.common.entity.setting.Setting;
import com.eshop.common.entity.setting.SettingBag;

import java.util.List;

public class EmailSettingBag extends SettingBag {
    public EmailSettingBag(List<Setting> listSettings) {
        super(listSettings);
    }

    public String getHost() {
        return super.getValue("MAIL_HOST");
    }
    public String getUsername() {
        return super.getValue("MAIL_USERNAME");
    }
    public int getPort() {
        return Integer.valueOf(super.getValue("MAIL_PORT"));
    }
    public String getPassword() {
        return super.getValue("MAIL_PASSWORD");
    }
    public String getSmtpAuth() {
        return super.getValue("SMTP_AUTH");
    }
    public String getSmtpSecured() {
        return super.getValue("SMTP_SECURED");
    }
    public String getMailFrom() {
        return super.getValue("MAIL_FROM");
    }
    public String getCustomerVerifySubject() {
        return super.getValue("CUSTOMER_VERIFY_SUBJECT");
    }
    public String getSenderName() {
        return super.getValue("MAIL_SENDER_NAME");
    }
    public String getCustomerVerifyContent() {
        return super.getValue("CUSTOMER_VERIFY_CONTENT");
    }

}
