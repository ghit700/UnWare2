package com.xmrbi.unware.component.service.ZkTeco;

/**
 * 指纹模板
 * Created by wzn on 2018/8/9.
 */
public class ZkTecoFp {
    private String PIN;
    private String FID;
    private String Size;
    private String Valid;
    private String TMP;
    public String getPIN() {
        return PIN;
    }
    public void setPIN(String pIN) {
        PIN = pIN;
    }
    public String getFID() {
        return FID;
    }
    public void setFID(String fID) {
        FID = fID;
    }
    public String getSize() {
        return Size;
    }
    public void setSize(String size) {
        Size = size;
    }
    public String getValid() {
        return Valid;
    }
    public void setValid(String valid) {
        Valid = valid;
    }
    public String getTMP() {
        return TMP;
    }
    public void setTMP(String tMP) {
        TMP = tMP;
    }
    public String toString() {
        return "PIN=" + PIN + ",FID=" + FID + ",Size=" + Size + ",Valid=" + Valid + ",TrueLen=" + TMP.length() + ",TMP=" + TMP;
    }
}
