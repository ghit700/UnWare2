package com.xmrbi.unware.data.entity.main;

import com.blankj.utilcode.util.StringUtils;

import java.util.Date;

/**
 * Created by wzn on 2018/5/21.
 */

public class Rfid {
    private String code;
    private String name;
    private String brand;
    private String model;
    private String drawerNames;
    private String drawerIds;
    private String amount;
    /**
     * 是否是需要的rfid
     */
    private boolean isNeed;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDrawerNames() {
        return drawerNames;
    }

    public void setDrawerNames(String drawerNames) {
        this.drawerNames = drawerNames;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDrawerIds() {
        return drawerIds;
    }

    public void setDrawerIds(String drawerIds) {
        this.drawerIds = drawerIds;
    }

    public Rfid(String code, String name, boolean isNeed,String drawerIds) {
        this.code = code;
        this.name = name;
        this.isNeed = isNeed;
        this.drawerIds = drawerIds;
    }

    public Rfid(String code) {
        this.code = code;
    }

    public Rfid() {

    }


    public boolean isNeed() {
        return isNeed;
    }

    public void setNeed(boolean need) {
        isNeed = need;
    }

    @Override
    public String toString() {
        if (StringUtils.isEmpty(name)) {
            name = "";
        }
        if (StringUtils.isEmpty(code)) {
            code = "";
        }else{
            code=code.substring(code.indexOf("A"));
        }
        if (isNeed) {
            return "未拿：" + name + "-" + code;
        }
        return "错拿：" + name + "-" + code;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().getName().contains("Rfid") && !StringUtils.isEmpty(code)) {
            return ((Rfid) obj).getCode().equals(code);
        }
        if (obj.getClass().getName().contains("String") && !StringUtils.isEmpty(code)) {
            return obj.equals(code);
        }
        return super.equals(obj);
    }
}
