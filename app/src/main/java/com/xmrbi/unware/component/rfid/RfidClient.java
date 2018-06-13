package com.xmrbi.unware.component.rfid;


import com.blankj.utilcode.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class RfidClient {

    /********** 状态 **********/
    /**
     * 已断开
     **/
    public static final int STATUS_DISCONNECTED = 0;
    /**
     * 已连接
     **/
    public static final int STATUS_CONNECTED = 1;
    /********** 状态 **********/

    /**
     * IP
     **/
    private String ip;
    /**
     * 端口
     **/
    private int port;
    /**
     * 套接字
     **/
    private Socket socket;
    /**
     * 状态
     **/
    private Integer status = STATUS_DISCONNECTED;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * 深圳科陆RFID读写器通讯
     **/
    public RfidClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    /**
     * 连接
     *
     * @throws IOException
     * @throws UnknownHostException
     **/
    public boolean connect() throws UnknownHostException, IOException {
        this.disconnect();
        socket = new Socket(ip, port);
        if (socket != null) {
            status = STATUS_CONNECTED;
            return true;
        } else {
            status = STATUS_DISCONNECTED;
            return false;
        }
    }

    /**
     * 断开连接
     *
     * @throws IOException
     **/
    public void disconnect() throws IOException {
        if (socket != null) {
            socket.getInputStream().close();
            socket.getOutputStream().close();
            socket.close();
            status = STATUS_DISCONNECTED;
        }
    }

    /**
     * 停止
     *
     * @throws IOException
     */
    public boolean stop() throws IOException {
        boolean flag = false;
        if (socket != null) {
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();
            DataFormat.sendDataFormat(os, DataFormat.Cmd.STOP);
            DataFormat df = DataFormat.parseDataFormat(is);
            if (df.getDataParam() != null && df.getCtrlWord().equals(DataFormat.PASSIVE + DataFormat.OPER_RFID + DataFormat.CMD_STOP) && df.getDataParam().equals(DataFormat.PARAM_STOP_SUCC))
                flag = true;
        }
        return flag;
    }

    /**
     * 查询读写器能力
     *
     * @throws IOException
     */
    public String[] ability() throws IOException {
        String[] abilitys = new String[4];
        if (socket != null) {
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();
            DataFormat.sendDataFormat(os, DataFormat.Cmd.ABILITY);
            DataFormat df = DataFormat.parseDataFormat(is);
            if (df.getDataParam() != null && df.getCtrlWord().equals(DataFormat.PASSIVE + DataFormat.OPER_RFID + DataFormat.CMD_ABILITY)) {
                String dataParam = df.getDataParam();
                // 最小发射功率(单位：dBm)
                int minPower = Integer.parseInt(dataParam.substring(0, 2), 16);
                // 最大发射功率(单位：dBm)
                int maxPower = Integer.parseInt(dataParam.substring(2, 4), 16);
                // 天线数目
                int antennaCount = Integer.parseInt(dataParam.substring(4, 6), 16);
                int frequenceRangeCount = Integer.parseInt(dataParam.substring(6, 10), 16);
                // 频段列表
                List<String> frequenceRangeList = new ArrayList<String>();
                for (int i = 0; i < frequenceRangeCount; i++) {
                    String frequenceRange = DataFormat.frequenceRangeMap.get(Integer.parseInt(dataParam.substring(10 + (i * 2), 12 + (i * 2)), 16));
                    if (frequenceRange != null)
                        frequenceRangeList.add(frequenceRange);
                }
                int rfidProtocolCount = Integer.parseInt(dataParam.substring(10 + (frequenceRangeCount * 2), 14 + (frequenceRangeCount * 2)), 16);
                // RFID协议列表
                List<String> rfidProtocolList = new ArrayList<String>();
                for (int i = 0; i < rfidProtocolCount; i++) {
                    String rfidProtocol = DataFormat.rfidProtocolMap.get(Integer.parseInt(dataParam.substring(14 + (frequenceRangeCount * 2) + (i * 2), 16 + (frequenceRangeCount * 2) + (i * 2)), 16));
                    if (rfidProtocol != null)
                        rfidProtocolList.add(rfidProtocol);
                }
                System.out.println("<<----------  查询读写器能力  start  ---------->>");
                System.out.println("最小发射功率：" + minPower + "dBm");
                System.out.println("最大发射功率：" + maxPower + "dBm");
                System.out.println("天线数目：" + antennaCount);
                System.out.println("频段列表：");
                for (String frequenceRange : frequenceRangeList) {
                    System.out.println("\t" + frequenceRange);
                }
                System.out.println("RFID协议列表：");
                for (String rfidProtocol : rfidProtocolList) {
                    System.out.println("\t" + rfidProtocol);
                }
                System.out.println("<<----------  查询读写器能力  end  ---------->>");
            }
        }
        return abilitys;
    }

    /**
     * 单次读取EPC
     *
     * @throws IOException
     */
    public List<String> startRead() throws IOException {
        List<String> rfidList = null;
        if (socket != null && !socket.isClosed()) {
            rfidList = new ArrayList<String>();
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();
            DataFormat.sendDataFormat(os, DataFormat.Cmd.EPC12);
            System.out.println("<<----------  单次读取EPC  start  ---------->>");
            while (!Thread.interrupted()) {
                if (socket != null) {
                    try {
                        DataFormat df = DataFormat.parseDataFormat(socket.getInputStream());
                        String ctrlWord = df.getCtrlWord();
                        if(!StringUtils.isEmpty(ctrlWord)){
                            if (ctrlWord.equals(DataFormat.PASSIVE + DataFormat.OPER_RFID + DataFormat.CMD_READ_EPC)) {
                                if (df.getDataParam().equals(DataFormat.PARAM_EPC_START_SUCC))
                                    System.out.println("单次操作开始");
                            } else if (ctrlWord.equals(DataFormat.INITIATIVE + DataFormat.OPER_RFID + DataFormat.FEEDBACK_EPC_TAG)) {
                                String tag = DataFormat.parseEpc6C(df.getDataParam());
                                if (!rfidList.contains(tag))
                                    rfidList.add(tag);
                            } else if (ctrlWord.equals(DataFormat.INITIATIVE + DataFormat.OPER_RFID + DataFormat.FEEDBACK_EPC_END)) {
                                String dataParam = df.getDataParam();
                                if (dataParam.equals(DataFormat.PARAM_EPC_END_SUCC))
                                    System.out.println("单次操作结束");
                                else if (dataParam.equals(DataFormat.PARAM_EPC_END_STOP))
                                    System.out.println("收到停止指令");
                                else if (dataParam.equals(DataFormat.PARAM_EPC_END_FAULT))
                                    System.out.println("硬件故障导致读卡中断");
                                break;
                            } else {
                                System.out.println(df.toString());
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    status = STATUS_DISCONNECTED;
                }
            }
            System.out.println("<<----------  单次读取EPC  end  ---------->>");
        }
        return rfidList;
    }

}