package com.xmrbi.unware.component.http;

/**
 * 统一解析的判断
 * Created by wzn on 2018/4/17.
 */

public class Response<T> {
    private boolean success;
    private T data;
    private String errorMsg;

    public Response() {
    }

    public Response(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
