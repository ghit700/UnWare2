package com.xmrbi.unware.component.http;

import com.blankj.utilcode.util.SPUtils;
import com.xmrbi.unware.base.Config;

/**
 * Created by wzn on 2018/4/16.
 */

public class HttpUtils {

    /**
     * 重置服务器地址
     */
    public static void resetServerAddress() {
        Config.Http.SERVER_IP = SPUtils.getInstance(Config.SP.SP_NAME).getString(Config.SP.SP_SERVER_IP);
        Config.Http.SERVER_GMMS = SPUtils.getInstance(Config.SP.SP_NAME).getString(Config.SP.SP_SERVER_GMMS_IP);
        Config.Http.UPDATE_APK_ADDRESS = Config.Http.SERVER_GMMS + "gmms/files/UnWare.apk";
        Config.Http.UPDATE_APK_UPDATE_FILE = Config.Http.SERVER_GMMS + "gmms/updateUnWare.xml";
    }
}
