package com.xmrbi.unware.data.entity.pick;

import java.io.Serializable;

/**
 * 领料单信息
 * Created by wzn on 2018/5/17.
 */
public class PickOrder implements Serializable {
    private String createTime;
    private int id;
    private String zoneName;
    private String componentCounts;
    private Double totalCount;
    private String bsnum;
    private String expenseItemName;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getComponentCounts() {
        return componentCounts;
    }

    public void setComponentCounts(String componentCounts) {
        this.componentCounts = componentCounts;
    }

    public Double getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(double totalCount) {
        this.totalCount = totalCount;
    }

    public String getBsnum() {
        return bsnum;
    }

    public void setBsnum(String bsnum) {
        this.bsnum = bsnum;
    }

    public String getExpenseItemName() {
        return expenseItemName;
    }

    public void setExpenseItemName(String expenseItemName) {
        this.expenseItemName = expenseItemName;
    }
}
