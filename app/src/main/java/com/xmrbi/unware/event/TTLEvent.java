package com.xmrbi.unware.event;

/**
 * Created by wzn on 2018/5/21.
 */

public class TTLEvent {
    //状态 CONNECTION_STATE status=1 串口打开成功否则就是失败 CHECKPAGE_RESULT status=1 有纸 不然就是缺纸
    private int status;
    //TTL_Factory.CONNECTION_STATE TTL_Factory.CHECKPAGE_RESULT
    private int type;

    public TTLEvent(int status, int type) {
        this.status = status;
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
