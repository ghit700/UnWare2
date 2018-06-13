package com.xmrbi.unware.data.entity.deliver;

/**
 * Created by wzn on 2018/6/12.
 */
public class Device {
    private String modelName;
    private String brandName;
    private Double amount;
    private String unit;
    private String assetCode;
    private int id;
    private String componentName;
    private String drawerIds;
    private String drawerNames;
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getDrawerIds() {
        return drawerIds;
    }

    public void setDrawerIds(String drawerIds) {
        this.drawerIds = drawerIds;
    }

    public String getDrawerNames() {
        return drawerNames;
    }

    public void setDrawerNames(String drawerNames) {
        this.drawerNames = drawerNames;
    }
}
