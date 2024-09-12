package com.eshop.admin.setting;

import com.eshop.common.entity.Setting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SettingService {

    @Autowired
    private SettingRepository settingRepository;

    public List<Setting> listAllSettings() {
        return settingRepository.findAll();
    }
}
