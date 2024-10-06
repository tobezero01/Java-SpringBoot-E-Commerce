package com.eshop.setting;

import com.eshop.common.entity.setting.Setting;
import com.eshop.common.entity.setting.SettingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SettingRepository extends JpaRepository<Setting, String> {

    public List<Setting> findByCategory(SettingCategory category);

    @Query("Select s From Setting s Where s.category = ?1 Or s.category = ?2")
    public List<Setting> findByTwoCategories(SettingCategory catOne, SettingCategory catTwo);
}
