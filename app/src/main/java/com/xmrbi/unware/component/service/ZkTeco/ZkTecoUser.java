package com.xmrbi.unware.component.service.ZkTeco;

/**
 * 用户
 * Created by wzn on 2018/8/9.
 */
public class ZkTecoUser {
    private String PIN;
    private String Name;
    private String Pri;
    private String Passwd;
    private String Card;
    private String Grp;
    private String TZ;
    private String Verify;
    public String getPIN() {
        return PIN;
    }
    public void setPIN(String pIN) {
        PIN = pIN;
    }
    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }
    public String getPri() {
        return Pri;
    }
    public void setPri(String pri) {
        Pri = pri;
    }
    public String getPasswd() {
        return Passwd;
    }
    public void setPasswd(String passwd) {
        Passwd = passwd;
    }
    public String getCard() {
        return Card;
    }
    public void setCard(String card) {
        Card = card;
    }
    public String getGrp() {
        return Grp;
    }
    public void setGrp(String grp) {
        Grp = grp;
    }
    public String getTZ() {
        return TZ;
    }
    public void setTZ(String tZ) {
        TZ = tZ;
    }
    public String getVerify() {
        return Verify;
    }
    public void setVerify(String verify) {
        Verify = verify;
    }
    public String toString() {
        return "PIN=" + PIN + ",Name=" + Name + ",Pri=" + Pri + ",Passwd=" + Passwd + ",Card=" + Card + ",Grp=" + Grp + ",TZ=" + TZ + ",Verify=" + Verify;
    }
}
