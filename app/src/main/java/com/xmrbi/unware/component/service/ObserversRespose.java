package com.xmrbi.unware.component.service;

/**
 * 服务返回的实体类
 * Created by wzn on 2018/8/10.
 */
public class ObserversRespose {
    /**
     * 服务调用的类型
     * 1.中控指纹考勤机 刷指纹
     */
    private int type;
    /**
     * 具体返回的实体封装类
     */
    private Object data;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
