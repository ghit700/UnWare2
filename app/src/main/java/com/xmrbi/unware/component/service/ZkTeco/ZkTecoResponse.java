package com.xmrbi.unware.component.service.ZkTeco;


import com.blankj.utilcode.util.LogUtils;
import com.xmrbi.unware.component.service.NanoHTTPD;
import com.xmrbi.unware.component.service.ObserversRespose;
import com.xmrbi.unware.component.service.ResposeUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wzn on 2018/8/9.
 */
public class ZkTecoResponse {
    private ZkTecoOper mOper;

    public ZkTecoResponse(ZkTecoOper mOper) {
        this.mOper = mOper;
    }

    /**
     * 设备主动获取命令
     */
    public NanoHTTPD.Response getrequest(NanoHTTPD.IHTTPSession session) {
        NanoHTTPD.Response response = ResposeUtils.responseData("OK", null);
        Map<String, String> params = session.getParms();
        if (params.get("INFO") == null) {
            //Log.i("ZSS","getrequest-指纹机获取指令");
            if (mOper.cmd != null) {
                response = ResposeUtils.responseData(mOper.cmd, "GB2312");    // 执行指令
                mOper.cmd = null;
            }
        }
        return response;
    }

    /**
     * 设备上传数据以及初始化设备设置
     */
    public NanoHTTPD.Response cdata(NanoHTTPD.IHTTPSession session) {
        NanoHTTPD.Response response = ResposeUtils.responseData("OK", null);
        Map<String, String> params = session.getParms();
        if (params.get("options") != null) {
            response = initData(params.get("SN"));                        // 初始化数据
        } else if (params.get("table") != null) {
            if (params.get("table").equals("ATTLOG"))
                response = attLog(session);                                // 上传考勤记录
            else if (params.get("table").equals("OPERLOG"))
                response = operLog(session);                            // 上传操作记录
        }
        return response;
    }

    /**
     * 设备上传操作记录
     *
     * @param session
     * @return
     */
    private NanoHTTPD.Response operLog(NanoHTTPD.IHTTPSession session) {
        List<String> rowList = null;
        try {
            rowList = this.requestOperateData(session);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        List<Object> userList = null;
        List<Object> fpList = null;
        for (String operLog : rowList) {
            if (operLog.startsWith("USER")) {
                if (userList == null)
                    userList = new ArrayList<Object>();
                String[] rows = operLog.split("\n");
                for (int i = 0; i < rows.length; i++) {
                    List<Object> tempList = this.parseUserList(rows[i]);
                    if (tempList != null)
                        userList.addAll(tempList);
                }
            } else if (operLog.startsWith("FP")) {
                if (fpList == null)
                    fpList = new ArrayList<Object>();
                String[] rows = operLog.split("\n");
                for (int i = 0; i < rows.length; i++) {
                    List<Object> tempList = this.parseFpList(rows[i]);
                    if (tempList != null)
                        fpList.addAll(tempList);
                }
            } else {
                System.out.println("操作记录：" + operLog);
            }
        }
        if (userList != null)
            mOper.queryListMap.put("USER", userList);
        if (fpList != null)
            mOper.queryListMap.put("FP", fpList);
        return ResposeUtils.responseData("OK:" + rowList.size(), null);
    }

    /**
     * 获取操作上传请求实体
     *
     * @param session
     * @throws UnsupportedEncodingException
     */
    private List<String> requestOperateData(NanoHTTPD.IHTTPSession session) throws UnsupportedEncodingException {
        List<String> rowList = new ArrayList<String>();
        Map<String, String> headers = session.getHeaders();
        String contentLen = headers.get("content-length");
        if (contentLen != null && contentLen.length() > 0) {
            InputStream input = session.getInputStream();
            byte[] bytes = new byte[Integer.parseInt(contentLen)];
            try {
                input.read(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String total = new String(bytes, "GB2312");// 按GB2312解码
            String[] lines = total.split("\r\n");
            for (int i = 0; i < lines.length; i++) {
                if (lines[i] != null && !lines[i].equals("")) {
                    rowList.add(lines[i]);
                }
            }
        }
        return rowList;
    }

    /**
     * 解析用户数据
     *
     * @param data
     * @return
     */
    private List<Object> parseUserList(String data) {
        List<Object> userList = null;
        while (data != null && data.startsWith("USER")) {
            ZkTecoUser user = new ZkTecoUser();
            int pinStart = data.indexOf("PIN=");
            int nameStart = data.indexOf("Name=");
            int priStart = data.indexOf("Pri=");
            int passwdStart = data.indexOf("Passwd=");
            int cardStart = data.indexOf("Card=");
            int grpStart = data.indexOf("Grp=");
            int tzStart = data.indexOf("TZ=");
            int verifyStart = data.indexOf("Verify=");
            user.setPIN(data.substring(pinStart + 4, nameStart - 1));
            user.setName(data.substring(nameStart + 5, priStart - 1));
            user.setPasswd(data.substring(passwdStart + 7, cardStart - 1));
            user.setCard(data.substring(cardStart + 5, grpStart - 1));
            user.setGrp(data.substring(grpStart + 4, tzStart - 1));
            user.setTZ(data.substring(tzStart + 3, verifyStart - 1));
            user.setVerify(data.substring(verifyStart + 7, verifyStart + 8));
            if (userList == null)
                userList = new ArrayList<Object>();
            userList.add(user);
            if (data.length() > (verifyStart + 8 + 1))
                data = data.substring(verifyStart + 8 + 1);
            else
                data = null;
        }
        return userList;
    }

    /**
     * 解析指纹数据
     *
     * @param data
     * @return
     */
    private List<Object> parseFpList(String data) {
        List<Object> fpList = null;
        while (data != null && data.startsWith("FP")) {
            ZkTecoFp fp = new ZkTecoFp();
            int pinStart = data.indexOf("PIN=");
            int fidStart = data.indexOf("FID=");
            int sizeStart = data.indexOf("Size=");
            int validStart = data.indexOf("Valid=");
            int tmpStart = data.indexOf("TMP=");
            fp.setPIN(data.substring(pinStart + 4, fidStart - 1));
            fp.setFID(data.substring(fidStart + 4, sizeStart - 1));
            String Size = data.substring(sizeStart + 5, validStart - 1);
            fp.setSize(Size);
            fp.setValid(data.substring(validStart + 6, tmpStart - 1));
            fp.setTMP(data.substring(tmpStart + 4, tmpStart + 4 + Integer.parseInt(Size)));
            if (fpList == null)
                fpList = new ArrayList<Object>();
            fpList.add(fp);
            if (data.length() > (tmpStart + 4 + Integer.parseInt(Size) + 1))
                data.substring(tmpStart + 4 + Integer.parseInt(Size) + 1);
            else
                data = null;
        }
        return fpList;
    }

    /**
     * 设备上传考勤记录
     *
     * @param session
     * @return
     */
    private NanoHTTPD.Response attLog(NanoHTTPD.IHTTPSession session) {
        List<String> rowList = null;
        try {
            rowList = this.requestAllLogData(session);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        List<Object> attLogList = new ArrayList<Object>();
        for (String attLog : rowList) {
            // 坑，查询考勤记录时，换行居然用\n
            String[] rows = attLog.split("\n");
            for (int i = 0; i < rows.length; i++) {
                ZkTecoAttLog log = new ZkTecoAttLog();
                log.parseAttLog(rows[i]);
                attLogList.add(log);
            }
        }
        if (attLogList.size() > 0) {
            if (mOper.isQuery) {                                    // 查询触发
                mOper.queryListMap.put("ATTLOG", attLogList);
            } else {                                            // 刷指纹触发
                for (Object log : attLogList) {
                    ZkTecoAttLog temp = (ZkTecoAttLog) log;
                    mOper.setChanged();
                    ObserversRespose respose=new ObserversRespose();
                    respose.setType(1) ;
                    respose.setData(temp);
                    mOper.notifyObservers(respose);
                }
            }
        }
        return ResposeUtils.responseData("OK:" + rowList.size(), null);
    }

    /**
     * 获取考勤记录请求实体
     *
     * @param session
     * @throws UnsupportedEncodingException
     */
    private List<String> requestAllLogData(NanoHTTPD.IHTTPSession session) throws UnsupportedEncodingException {
        List<String> rowList = new ArrayList<String>();
        Map<String, String> headers = session.getHeaders();
        String contentLen = headers.get("content-length");
        if (contentLen != null && contentLen.length() > 0) {
            InputStream input = session.getInputStream();
            byte[] bytes = new byte[Integer.parseInt(contentLen)];
            try {
                input.read(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String total = new String(bytes, "GB2312");// 按GB2312解码
            String[] lines = total.split("\r\n");
            for (int i = 0; i < lines.length; i++) {
                if (lines[i] != null && !lines[i].equals("")) {
                    rowList.add(lines[i]);
                }
            }
        }
        return rowList;
    }

    /**
     * 初始化设备的设置
     *
     * @param sn
     * @return
     */
    private NanoHTTPD.Response initData(String sn) {
        LogUtils.d("初始化...");
        StringBuffer buff = new StringBuffer("GET OPTION FROM: " + sn + "\r\n");
        buff.append("ATTLOGStamp=None\r\n");
        buff.append("OPERLOGStamp=None\r\n");
        buff.append("ATTPHOTOStamp=None\r\n");
        buff.append("ErrorDelay=30\r\n");
        buff.append("Delay=10\r\n");
        buff.append("TransTimes=00:00;14:05\r\n");
        buff.append("TransInterval=1\r\n");
        // 该参数的修改需谨慎，有可能导致上传考勤记录不稳定
        buff.append("TransFlag=TransData AttLog\r\n");
        buff.append("TimeZone=8\r\n");
        buff.append("Realtime=1\r\n");
        buff.append("Encrypt=None\r\n");
        buff.append("ServerVer=2.2.14\r\n");
        return ResposeUtils.responseData(buff.toString(), null);
    }

}
