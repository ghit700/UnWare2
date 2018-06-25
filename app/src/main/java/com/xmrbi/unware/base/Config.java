package com.xmrbi.unware.base;

import android.os.Environment;


import com.xmrbi.unware.R;

import java.io.File;

/**
 * Created by wzn on 2018/3/29.
 */

public class Config {
    /**
     * SD卡的路径
     */
    public static String SD_PATH = Environment.getExternalStorageDirectory()
            .getPath();
    /**
     * log是否显示
     */
    public static boolean IS_OPEN_LOG = true;

    /**
     * 科大讯飞的appid
     */
//    public static String SPEECHCONSTANT_APPID="5ad7567b";
    public static class DB {
        public static String DB_NAME = "UNWARE_DB";
    }

    public static class SP {
        /**
         * 设置的SharedPreferences的名字
         */
        public static String SP_NAME = "UNWARE_SETTING";
        /**
         * 是否设置仓库信息
         */
        public static String SP_IS_SETTING = "is_setting";
        /**
         * 是否是新版上位机
         */
        public final static String SP_IS_NEW = "is_new";
        public final static String SP_SERVER_IP = "server_ip";
        public final static String SP_SERVER_GMMS_IP = "server_gmms_ip";
        /**
         * 领料成功的rfid编码列表
         */
        public final static String SP_RFID_CODES = "rfid_codes";
        /**
         * 海康摄像头通道号
         */
        public final static String SP_CHANNEL = "channel";
    }

    public static class Crash {
        /**
         * 崩溃文件的地址
         */
        public static String CRASH_DIR = SD_PATH + File.separator + "unware" + File.separator + "errorFile";
    }

    public static class FileConfig {
        public static final String PICK_PHOTO_DIR = SD_PATH + File.separator + "unware" + File.separator + "pickPhoto" + File.separator;
    }

    public static class Http {
        /**
         * 超时时间
         */
        public static final int DEFAULT_TIMEOUT = 30;
        /**
         * 服务器地址
         */
//        public static String SERVER_IP = "http://192.168.4.21:8787/";
        public static String SERVER_IP = "http://172.20.60.40:8787/";
        /**
         * gmms地址
         */
        public static  String SERVER_GMMS="http://172.20.60.40:8280/";
//        public static String SERVER_GMMS = "http://172.16.53.226:8080/";
        //        public static final String SERVER_GMMS="http://172.16.53.226:8080/";
        public static final boolean IS_IP_ADDRESS = true;

        /**
         * apk的web地址
         */
        public static String UPDATE_APK_ADDRESS = SERVER_GMMS + "gmms/files/UnWare.apk";
        /**
         * 判断apk是否更新的web文件
         */
        public static String UPDATE_APK_UPDATE_FILE = SERVER_GMMS + "gmms/updateUnWare.xml";
        /**
         * apk本地下载地址目录（创建）
         */
        public static final String UPDATE_APK_FILE_ADDRESS_DIR = SD_PATH + File.separator + "unware" + File.separator + "apk";
        /**
         * apk本地下载地址
         */
        public static final String UPDATE_APK_FILE_ADDRESS = UPDATE_APK_FILE_ADDRESS_DIR + File.separator + "UnWare.apk";
    }


}
