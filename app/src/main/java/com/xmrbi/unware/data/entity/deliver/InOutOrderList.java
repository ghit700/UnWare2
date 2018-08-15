package com.xmrbi.unware.data.entity.deliver;

/**
 * Created by wzn on 2018/8/4.
 */
public class InOutOrderList {
    /**
     * id
     */
    private Long id;

    /**
     * 数量
     */
    private java.lang.Double amount;

    /**
     * 备注
     */
    private java.lang.String remark;

    /**
     * 货架Ids
     */
    private java.lang.String drawerIds;

    /**
     * 货架名称
     */
    private java.lang.String drawerName;



    /**
     *  设备
     */
    private Device device;


    /**
     * 锁定的用户设备
     */
    private Long lockUserDeviceId;
    /**
     * 锁定的仓库设备
     */
    private Long lockStoreDeviceId;



    /**
     * 单价
     */
    private java.lang.Double price = 0d;



    /**
     * 领用人
     */
    private String pickUser;

    /**
     * 领用时间
     */
    private Long pickDate;

    private String componentName;
    private String modelName;
    private String brandName;

    /**
     * 设备编码
     */
    private String sequenceCode;
    /**
     * 是否扫描
     */
    private boolean isScan;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDrawerIds() {
        return drawerIds;
    }

    public void setDrawerIds(String drawerIds) {
        this.drawerIds = drawerIds;
    }

    public String getDrawerName() {
        return drawerName;
    }

    public void setDrawerName(String drawerName) {
        this.drawerName = drawerName;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Long getLockUserDeviceId() {
        return lockUserDeviceId;
    }

    public void setLockUserDeviceId(Long lockUserDeviceId) {
        this.lockUserDeviceId = lockUserDeviceId;
    }

    public Long getLockStoreDeviceId() {
        return lockStoreDeviceId;
    }

    public void setLockStoreDeviceId(Long lockStoreDeviceId) {
        this.lockStoreDeviceId = lockStoreDeviceId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPickUser() {
        return pickUser;
    }

    public void setPickUser(String pickUser) {
        this.pickUser = pickUser;
    }

    public Long getPickDate() {
        return pickDate;
    }

    public void setPickDate(Long pickDate) {
        this.pickDate = pickDate;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getSequenceCode() {
        return sequenceCode;
    }

    public void setSequenceCode(String sequenceCode) {
        this.sequenceCode = sequenceCode;
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

    public boolean isScan() {
        return isScan;
    }

    public void setScan(boolean scan) {
        isScan = scan;
    }
}
