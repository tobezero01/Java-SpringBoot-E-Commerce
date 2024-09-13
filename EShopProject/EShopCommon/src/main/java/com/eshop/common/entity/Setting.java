package com.eshop.common.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "settings")
public class Setting {

    @Id
    @Column(name = "`key`",nullable = false, length = 128)
    private String key;

    @Column(nullable = false, length = 1024)
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(length = 45, nullable = false)
    private SettingCategory category;

    public Setting() {
    }

    public Setting(String key) {
        this.key = key;
    }

    public Setting(String key, String value, SettingCategory category) {
        this.key = key;
        this.value = value;
        this.category = category;
    }

    public Setting(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public SettingCategory getCategory() {
        return category;
    }

    public void setCategory(SettingCategory category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(o == null) return false;
        if(getClass() != o.getClass()) return false;
        Setting other = (Setting) o;
        if(key == null) {
            if(other.key != null) {
                return false;
            }
        }else if (!key.equals(other.key)){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prima = 31;
        int result = 1;
        result = prima * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }
}
