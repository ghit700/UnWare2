package com.xmrbi.unware.component.service.ZkTeco;

/**
 * 考勤记录
 * Created by wzn on 2018/8/9.
 */
public class ZkTecoAttLog {
    private String Pin;
    private String Time;
    private String Status;
    private String Verify;
    private String Workcode;

    public String getPin() {
        return Pin;
    }

    public void setPin(String pin) {
        Pin = pin;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getVerify() {
        return Verify;
    }

    public void setVerify(String verify) {
        Verify = verify;
    }

    public String getWorkcode() {
        return Workcode;
    }

    public void setWorkcode(String workcode) {
        Workcode = workcode;
    }

    public void parseAttLog(String data) {
        if (data != null && !data.equals("")) {
            String[] params = data.split("\t");
            Pin = params[0];
            Time = params[1];
            Status = params[2];
            Verify = params[3];
            Workcode = params[4];
        }
    }

    public String toString() {
        return "Pin=" + Pin + ",Time=" + Time + ",Status=" + Status + ",Verify=" + Verify + ",Workcode=" + Workcode;
    }
}
