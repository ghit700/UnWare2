package com.xmrbi.unware.data.entity.main;

import java.util.Date;

public class StoreHouseDevice {

    /** 硬盘录像机 **/
    public static final Long DVR = 1L;
    /** 指纹机 **/
    public static final Long FINGER_PRINTS = 2L;
    /** 安防报警主机 **/
    public static final Long ALARM_HOST = 3L;
    /** 网络摄像头**/
    public static final Long WEB_CAM = 4L;
    /** 人脸识别服务器 **/
    public static final Long FACE_RECOGNITION = 5L;
    /** 门禁控制器 **/
    public static final Long ACCESS_CONTROLLER = 6L;
    /** 上位机 **/
    public static final Long UPPER_MONITOR = 7L;
    /** RFID **/
    public static final Long RFID = 8L;
    /** 灯控 **/
    public static final Long LIGHT_CTRL = 9L;

    /**
     * id
     */
    private Long id;

    /**
     * 仓库id
     */
    private Long storeHouseId;

    /**
     * 名称
     */
    private String name;

    /**
     * 类型
     * 1.硬盘录像机；2.指纹机；3.安防报警主机；4.网络摄像机；5.人脸识别服务器；6.门禁控制器；7.上位机；8.RFID；9.灯光控制器
     */
    private Long deviceType;

    /**
     * 内网ip
     */
    private String ipIn;

    /**
     * 内网端口号
     */
    private Integer portIn;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 控制编号
     * IO控灯：输出口编号(如：25，范围：1~32)
     * RTU控灯：控制板编号-继电器编号(如：10-3)
     * IO控门：开门输出口编号-关门输出口编号-暂停输出口编号(如：1-2-3，注：如果只有开关门功能，只需前两个参数即可，如：1-2)
     */
    private String ctrlNumber;

    /**
     * 摄像头通道号
     */
    private Integer channel;

    /**
     * 是否关键设备
     */
    private Boolean isPrimary;

    /**
     * 货架id
     */
    private String drawerIds;

    /**
     * 货架名称
     */
    private String drawerNames;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStoreHouseId() {
        return storeHouseId;
    }

    public void setStoreHouseId(Long storeHouseId) {
        this.storeHouseId = storeHouseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Long deviceType) {
        this.deviceType = deviceType;
    }

    public String getIpIn() {
        return ipIn;
    }

    public void setIpIn(String ipIn) {
        this.ipIn = ipIn;
    }

    public Integer getPortIn() {
        return portIn;
    }

    public void setPortIn(Integer portIn) {
        this.portIn = portIn;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCtrlNumber() {
        return ctrlNumber;
    }

    public void setCtrlNumber(String ctrlNumber) {
        this.ctrlNumber = ctrlNumber;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public String getDrawerIds() {
        return drawerIds;
    }

    public void setDrawerIds(String drawerIds) {
        this.drawerIds = drawerIds;
    }

    public String getDrawerNames() {
        return drawerNames;
    }

    public void setDrawerNames(String drawerNames) {
        this.drawerNames = drawerNames;
    }

}
