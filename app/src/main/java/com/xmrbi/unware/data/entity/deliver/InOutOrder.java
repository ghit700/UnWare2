package com.xmrbi.unware.data.entity.deliver;

import com.xmrbi.unware.data.entity.main.StoreHouse;
import com.xmrbi.unware.data.entity.main.User;
import com.xmrbi.unware.data.entity.main.Useunit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wzn on 2018/8/4.
 */
public class InOutOrder {
    /**
     * id
     */
    private Long id;

    /**
     * 出入库日期
     */
    private String inOutDate;

    /**
     * 出入库:true.入库 false.出库
     */
    private Boolean inOrOut;

    /**
     * 编号:出库单首字母CK,入库单RK,退库单TK加年月日八位数加三位流水号每天从一开始
     */
    private String code;

    /**
     * 发票号码
     */
    private String billCode;

    /**
     * 入账状态
     */
    private Boolean billStatus;


    /**
     * 仓管员 用户
     */
    private User storeHouseUser;

    /**
     * 创建人 用户
     */
    private User creator;

    /**
     * 出入库类型
     */
    private String inOutType;

    /**
     * 备注
     */
    private String remark;
    /**
     * 总价
     */
    private Double totalPrice;


    private List<InOutOrderList> inOutOrderListList = new ArrayList<InOutOrderList>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInOutDate() {
        return inOutDate;
    }

    public void setInOutDate(String inOutDate) {
        this.inOutDate = inOutDate;
    }

    public Boolean getInOrOut() {
        return inOrOut;
    }

    public void setInOrOut(Boolean inOrOut) {
        this.inOrOut = inOrOut;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    public Boolean getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(Boolean billStatus) {
        this.billStatus = billStatus;
    }

    public User getStoreHouseUser() {
        return storeHouseUser;
    }

    public void setStoreHouseUser(User storeHouseUser) {
        this.storeHouseUser = storeHouseUser;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getInOutType() {
        return inOutType;
    }

    public void setInOutType(String inOutType) {
        this.inOutType = inOutType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<InOutOrderList> getInOutOrderListList() {
        return inOutOrderListList;
    }

    public void setInOutOrderListList(List<InOutOrderList> inOutOrderListList) {
        this.inOutOrderListList = inOutOrderListList;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
