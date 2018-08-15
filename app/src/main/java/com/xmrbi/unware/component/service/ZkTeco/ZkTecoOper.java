package com.xmrbi.unware.component.service.ZkTeco;

import com.blankj.utilcode.util.LogUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 *  操作
 * Created by wzn on 2018/8/9.
 */
public class ZkTecoOper extends Observable {
    /** 指令ID **/
    private Long cmdId = 0L;

    /** 指令 **/
    public String cmd;

    /** 指令回复 **/
    public String cmdFeedback;

    public boolean isQuery;

    public Map<String, List<Object>> queryListMap;

    public Long getCmdId() {
        return cmdId;
    }

    public ZkTecoOper(Observer o) {
        queryListMap = new HashMap<String, List<Object>>();
        this.addObserver(o);
    }

    public void setChanged() {
        super.setChanged();
    }

    /** 登记用户指纹 **/
    public void enrollFp(String PIN, String FID, String RETRY, String OVERWRITE) {
        StringBuffer dataRecord = new StringBuffer();
        dataRecord.append("PIN=" + PIN).append("\tFID=" + FID)
                .append("\tRETRY=" + RETRY).append("\tOVERWRITE=" + OVERWRITE);
        cmd = generateCmd("ENROLL_FP", null, dataRecord.toString());
    }

    /** 指纹模板：新增/修改 **/
    public void updateFingerTmp(String PIN, String FID, String Size, String Valid, String TMP) {
        StringBuffer dataRecord = new StringBuffer();
        dataRecord.append("PIN=" + PIN).append("\tFID=" + FID)
                .append("\tSize=" + Size).append("\tValid=" + Valid)
                .append("\tTMP=" + TMP);
        cmd = generateCmd("UPDATE", "FINGERTMP", dataRecord.toString());
    }

    /** 指纹模板：删除 **/
    public void deleteFingerTmp(String PIN, String FID) {
        cmd = generateCmd("DELETE", "FINGERTMP", "PIN=" + PIN + "\tFID=" + FID);
    }

    /** 指纹模板：查询 **/
    public List<Object> queryFingerTmp(String PIN, String FID) {
        cmd = generateCmd("QUERY", "FINGERTMP", (FID != null) ? ("PIN=" + PIN + "\tFID=" + FID) : ("PIN=" + PIN));
        isQuery = true;
        List<Object> fpList = null;
        int timeout = 15;
        while (timeout > 0) {
            if (queryListMap.get("FP") != null) {
                fpList = queryListMap.get("FP");
                queryListMap.remove("FP");
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timeout--;
        }
        LogUtils.d("查询消耗时间：" + (15 - timeout));
        isQuery = false;
        return fpList;
    }

    /** 考勤记录： 查询 **/
    public List<Object> queryAttLog(String StartTime, String EndTime) {
        cmd = generateCmd("QUERY", "ATTLOG", "StartTime=" + StartTime + "\tEndTime=" + EndTime);
        isQuery = true;
        List<Object> attLogList = null;
        int timeout = 15;
        while (timeout > 0) {
            if (queryListMap.get("ATTLOG") != null) {
                attLogList = queryListMap.get("ATTLOG");
                queryListMap.remove("ATTLOG");
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timeout--;
        }
        LogUtils.d("查询消耗时间：" + (15 - timeout));
        isQuery = false;
        return attLogList;
    }

    /** 用户信息：查询 **/
    public List<Object> queryUserInfo(String PIN) {
        cmd = generateCmd("QUERY", "USERINFO", "PIN=" + PIN);
        isQuery = true;
        List<Object> userAndFpList = null;
        int timeout = 15;
        while (timeout > 0) {
            if (queryListMap.get("USER") != null) {
                userAndFpList = queryListMap.get("USER");
                queryListMap.remove("USER");
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timeout--;
        }
        while (timeout > 0) {
            if (queryListMap.get("FP") != null) {
                userAndFpList.addAll(queryListMap.get("FP"));
                queryListMap.remove("FP");
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timeout--;
        }
        LogUtils.d("查询消耗时间：" + (15 - timeout));
        isQuery = false;
        return userAndFpList;
    }

    /** 用户信息：新增/修改 **/
    public void updateUserInfo(String PIN, String Name) {
        StringBuffer dataRecord = new StringBuffer();
        dataRecord.append("PIN=" + PIN).append("\tName=" + Name)
                .append("\tPasswd=0").append("\tCard=0")
                .append("\tGrp=1").append("\tTZ=0001000100000000")
                .append("\tPri=0");
        cmd = generateCmd("UPDATE", "USERINFO", dataRecord.toString());
    }

    /** 用户信息：删除 **/
    public void deleteUserInfo(String PIN) {
        cmd = generateCmd("DELETE", "USERINFO", "PIN=" + PIN);
    }

    /** 清除考勤记录 **/
    public void clearLog() {
        cmd = generateCmd("CLEAR", "LOG", null);
    }

    /** 清除全部数据 **/
    public void clearData() {
        cmd = generateCmd("CLEAR", "DATA", null);
    }

    /** 重启 **/
    public void reboot() {
        cmd = generateCmd("REBOOT", null, null);
    }

    /**
     * 生成指令
     * @param operCmd
     * @param tableName
     * @param dataRecord
     */
    private String generateCmd(String operCmd, String tableName, String dataRecord) {
        StringBuffer buff = new StringBuffer();
        buff.append("C:").append(++cmdId).append(":");
        // DATA命令
        if (operCmd.equals("UPDATE") || operCmd.equals("DELETE") || operCmd.equals("QUERY"))
            buff.append("DATA").append(" ");
        buff.append(operCmd);
        if (tableName != null)
            buff.append(" ").append(tableName);
        if (dataRecord != null)
            buff.append(" ").append(dataRecord);
        return buff.toString();
    }
}
