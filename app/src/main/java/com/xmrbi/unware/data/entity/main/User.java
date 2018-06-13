package com.xmrbi.unware.data.entity.main;

import java.io.Serializable;

/**
 * Created by wzn on 2018/5/16.
 */
public class User implements Serializable {

    private Long id;

    private String name;

    /**
     * 指纹机id
     */
    private Long flag;

    private Long lesseeId;
    /**
     * 是否是仓管
     */
    private boolean isKeeper;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getFlag() {
        return flag;
    }

    public void setFlag(Long flag) {
        this.flag = flag;
    }

    public Long getLesseeId() {
        return lesseeId;
    }

    public void setLesseeId(Long lesseeId) {
        this.lesseeId = lesseeId;
    }

    public boolean isKeeper() {
        return isKeeper;
    }

    public void setKeeper(boolean keeper) {
        isKeeper = keeper;
    }
}
