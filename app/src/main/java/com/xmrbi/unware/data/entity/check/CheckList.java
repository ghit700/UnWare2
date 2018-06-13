package com.xmrbi.unware.data.entity.check;

/**
 * Created by wzn on 2018/6/11.
 */
public class CheckList {
    /**
     * id : 192
     * checkDate : 2018-01-09 00:00:00
     * billNo : PD-2018-01-09-0006
     * storeHouseId : 10
     * storeHouseName : 厦门大桥仓库
     * createDate : 2018-01-09 15:45:05
     */

    private Long id;
    private String checkDate;
    private String billNo;
    private int storeHouseId;
    private String storeHouseName;
    private String createDate;
    private String creator;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(String checkDate) {
        this.checkDate = checkDate;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public int getStoreHouseId() {
        return storeHouseId;
    }

    public void setStoreHouseId(int storeHouseId) {
        this.storeHouseId = storeHouseId;
    }

    public String getStoreHouseName() {
        return storeHouseName;
    }

    public void setStoreHouseName(String storeHouseName) {
        this.storeHouseName = storeHouseName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
