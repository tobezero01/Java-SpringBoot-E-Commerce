package com.eshop.admin.setting;

import com.eshop.common.entity.Setting;
import com.eshop.common.entity.SettingCategory;
import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettingRepository extends JpaRepository<Setting, String> {
    public List<Setting> findByCategory(SettingCategory category);
}
