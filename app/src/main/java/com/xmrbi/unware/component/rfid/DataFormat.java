package com.xmrbi.unware.component.rfid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wzn on 2018/3/20.
 */

public class DataFormat {
    public static final String CMD_INIT 			= "AA02000000A803";			// 初始化
    public static final String CMD_READ_MULTIPLE	= "AA02100002010171AD";		// 连续EPC读卡

    /** 频段列表 **/
    public static final Map<Integer, String> frequenceRangeMap = new HashMap<Integer, String>();

    /** RFID协议列表 **/
    public static final Map<Integer, String> rfidProtocolMap = new HashMap<Integer, String>();

    static {
        frequenceRangeMap.put(0, "国标 920~925MHz");
        frequenceRangeMap.put(1, "国标 840~845MHz");
        frequenceRangeMap.put(2, "国标 840~845MHz&920~925MHz");
        frequenceRangeMap.put(3, "FCC 902~928MHz");
        frequenceRangeMap.put(4, "ETSI 866~868MHz");

        rfidProtocolMap.put(0, "ISO18000-6C/EPC C1G2");
        rfidProtocolMap.put(1, "ISO18000-6B");
        rfidProtocolMap.put(2, "国标 GB/T 29768-2013");
        rfidProtocolMap.put(3, "国军标 GJB 7383.1-2011");
    }

    /** 帧头(1byte) **/
    public static final String FRAME_HEAD = "AA";

    /** 读写器被动执行 **/
    public static final String PASSIVE = "0";

    /** 读写器主动上传 **/
    public static final String INITIATIVE = "1";

    /** 读写器配置消息 **/
    public static final String OPER_READER = "1";

    /** 重启指令 **/
    public static final String CMD_REBOOT = "0F";

    /** RFID配置消息 **/
    public static final String OPER_RFID = "2";

    /** 停止指令 **/
    public static final String CMD_STOP = "FF";
    public static final String PARAM_STOP_SUCC = "00";

    /** 查询读写器能力 **/
    public static final String CMD_ABILITY = "00";

    /** EPC读标签 **/
    public static final String CMD_READ_EPC = "10";
    public static final String FEEDBACK_EPC_TAG = "00";
    public static final String FEEDBACK_EPC_END = "01";
    public static final String PARAM_EPC_START_SUCC = "00";
    public static final String PARAM_EPC_END_SUCC = "00";
    public static final String PARAM_EPC_END_STOP = "01";
    public static final String PARAM_EPC_END_FAULT = "02";

    public static enum Cmd {
        REBOOT,		/** 重启 **/
        STOP,		/** 停止 **/
        ABILITY, 	/** 查询读写器能力 **/
        EPC1, 		/** 单次读标签(天线1) **/
        EPC2, 		/** 单次读标签(天线2) **/
        EPC12		/** 单次读标签(天线1、天线2) **/
    }

    /** 帧头 **/
    private String frameHead;
    /** 协议控制字 **/
    private String ctrlWord;
    /** 数据长度 **/
    private String dataLen;
    /** 数据参数 **/
    private String dataParam;
    /** 校验码 **/
    private String check;

    public String getFrameHead() {
        return frameHead;
    }
    public void setFrameHead(String frameHead) {
        this.frameHead = frameHead;
    }
    public String getCtrlWord() {
        return ctrlWord;
    }
    public void setCtrlWord(String ctrlWord) {
        this.ctrlWord = ctrlWord;
    }
    public String getDataLen() {
        return dataLen;
    }
    public void setDataLen(String dataLen) {
        this.dataLen = dataLen;
    }
    public String getDataParam() {
        return dataParam;
    }
    public void setDataParam(String dataParam) {
        this.dataParam = dataParam;
    }
    public String getCheck() {
        return check;
    }
    public void setCheck(String check) {
        this.check = check;
    }
    public String toString() {
        return frameHead + ctrlWord + dataLen + dataParam + check;
    }

    /**
     * 发送数据格式
     * @throws IOException
     */
    public static void sendDataFormat(OutputStream os, Cmd cmd) throws IOException {
        StringBuffer rs = new StringBuffer();
        switch (cmd) {
            case REBOOT:
                rs.append(FRAME_HEAD).append(PASSIVE).append(OPER_READER).append(CMD_REBOOT).append("000094CF");
                break;
            case STOP:
                rs.append(FRAME_HEAD).append(PASSIVE).append(OPER_RFID).append(CMD_STOP).append("0000A40F");
                break;
            case ABILITY:
                rs.append(FRAME_HEAD).append(PASSIVE).append(OPER_RFID).append(CMD_ABILITY).append("0000A803");
                break;
            case EPC1:
                rs.append(FRAME_HEAD).append(PASSIVE).append(OPER_RFID).append(CMD_READ_EPC).append("00020100F1A8");
                break;
            case EPC2:
                rs.append(FRAME_HEAD).append(PASSIVE).append(OPER_RFID).append(CMD_READ_EPC).append("00020200FBA8");
                break;
            case EPC12:
                rs.append(FRAME_HEAD).append(PASSIVE).append(OPER_RFID).append(CMD_READ_EPC).append("000203007DAB");
                break;
            default:
                break;
        }
        if (os != null) {
            os.write(hexStringToByteArray(rs.toString()));
            os.flush();
        }
    }

    /**
     * 解析数据格式
     * @throws IOException
     */
    public static DataFormat parseDataFormat(InputStream is) throws IOException {
        DataFormat df = new DataFormat();
        df.setFrameHead(readBytes(is, 1));
        if (df.getFrameHead().equals(FRAME_HEAD)) {
            df.setCtrlWord(readBytes(is, 2));
            df.setDataLen(readBytes(is, 2));
            df.setDataParam(readBytes(is, Integer.parseInt(df.getDataLen(), 16)));
            df.setCheck(readBytes(is, 2));
        }
        return df;
    }

    /**
     * 按字节数读取输入流
     * @throws IOException
     */
    public static String readBytes(InputStream is, int byteSize) throws IOException {
        byte[] bytes = new byte[byteSize];
        is.read(bytes);
        return bytesToHexString(bytes);
    }

    /**
     * 十六进制字符串转单字节
     * @param s
     * @return
     */
    public static byte hexStringToByte(String s) {
        int len = s.length();
        byte b = 0x00;
        if (len < 2)
            return b;
        else
            return (byte) ((Character.digit(s.charAt(0), 16) << 4) + Character.digit(s.charAt(1 + 1), 16));
    }

    /**
     * 十六进制字符串转byte数组
     * @param s
     * @return
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] b = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个字节
            b[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }
        return b;
    }

    /**
     * 单字节转十六进制字符串
     * @param b
     * @return
     */
    public static String byteToHexString(byte b) {
        String hex = Integer.toHexString(b & 0xFF).toUpperCase();
        if (hex.length() == 1)
            return ("0" + hex).toUpperCase();
        else
            return hex.toUpperCase();
    }

    /**
     * byte数组转十六进制字符串
     * @param b
     * @return
     */
    public static String bytesToHexString(byte[] b) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF).toUpperCase();
            if (hex.length() == 1)
                buffer.append('0');
            buffer.append(hex.toUpperCase());
        }
        return buffer.toString();
    }

    /**
     * 8bit转byte
     */
    private byte bitArrayToByte(byte[] bits) {
        if (bits.length != 8)
            return (byte) 0x00;
        return (byte) (
                (bits[7] & 0xFF) |
                        (bits[6] & 0xFF) << 1 |
                        (bits[5] & 0xFF) << 2 |
                        (bits[4] & 0xFF) << 3 |
                        (bits[3] & 0xFF) << 4 |
                        (bits[2] & 0xFF) << 5 |
                        (bits[1] & 0xFF) << 6 |
                        (bits[0] & 0xFF) << 7
        );
    }

    /**
     * 解析EPC/6C标签
     * @param dataParam
     * @return
     */
    public static String parseEpc6C(String dataParam) {
        int tagLen = Integer.parseInt(dataParam.substring(0, 4), 16);
        return dataParam.substring(4, 4 + (tagLen * 2));
    }

}
