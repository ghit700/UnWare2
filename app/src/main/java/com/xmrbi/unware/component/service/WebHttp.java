package com.xmrbi.unware.component.service;

import com.xmrbi.unware.base.Config;
import com.xmrbi.unware.component.service.ZkTeco.ZkTecoOper;
import com.xmrbi.unware.component.service.ZkTeco.ZkTecoResponse;

import java.util.Map;
import java.util.Observer;

/**
 * 服务器接收请求
 * Created by wzn on 2018/8/9.
 */
public class WebHttp extends NanoHTTPD {

    private ZkTecoOper mOper;
    private ZkTecoResponse mZkTecoResponse;


    public WebHttp(Observer o) {
        super(Config.Http.HTTP_SERVICE_PORT);
        mOper = new ZkTecoOper(o);
        mZkTecoResponse = new ZkTecoResponse(mOper);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Response response = ResposeUtils.responseData("OK", null);
        switch (session.getUri()) {
            case "/iclock/cdata"://设备主动上传数据和初始化
                response = mZkTecoResponse.cdata(session);
                break;
            case "/iclock/getrequest"://设备每隔（*秒）主动向服务器读取命令
                response = mZkTecoResponse.getrequest(session);
                break;
        }
        return response;
    }


}
