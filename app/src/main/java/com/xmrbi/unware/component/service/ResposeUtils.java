package com.xmrbi.unware.component.service;

/**
 * Created by wzn on 2018/8/9.
 */
public class ResposeUtils {
    /**
     * 请求返回封装
     * @param data
     * @param charsetName
     * @return
     */
    public static NanoHTTPD.Response responseData(String data, String charsetName) {
        if (charsetName == null)
            return new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, "text/plain", data);
        else
            return new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, "text/plain", data, charsetName);
    }
}
