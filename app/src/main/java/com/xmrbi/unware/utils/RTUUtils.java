package com.xmrbi.unware.utils;

import android.os.Handler;
import android.os.Message;

import com.szzk.application.MySerialPort2;
import com.szzk.ttl_libs.TTL_Factory;

import java.io.IOException;

/**
 * RTU方式控灯
 * Created by wzn on 2018/8/8.
 */
public class RTUUtils {
    private MySerialPort2 mMySp;

    class MHandlerLight extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case TTL_Factory.CONNECTION_STATE: {
                    int k = msg.arg1;
                    if (k == 1) {

                    } else {
                    }
                    break;
                }
                case TTL_Factory.CHECKPAGE_RESULT: {
                    int kk = msg.arg1;
                    if (kk == 1) {
                    } else {
                    }
                    break;
                }

            }
        }
    }

    /**
     * @param com_name  串口地址
     * @param baud_rate 比特率
     */
    public void init(String com_name, String baud_rate) {
        mMySp = new MySerialPort2();
        mMySp.openport(com_name, Integer.parseInt(baud_rate), new MHandlerLight());
    }

    /**
     * 控灯
     * @param addrNumber	从机地址(10进制，如0x01即1，0x02即2)
     * @param deCommand		元件值(低电平亮，高电平灭，如"11101"即为4路亮，1235路灭)
     */
    public void controlLight(int addrNumber, String deCommand){
        try {
            mMySp.mOutputStream.write(generateCommand(addrNumber, deCommand));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 生成控灯指令
     * @param addrNumber	从机地址(10进制，如0x01即1，0x02即2)
     * @param deCommand		元件值(低电平亮，高电平灭，如"11101"即为4路亮，1235路灭)
     * @return 16进制byte数组(10位)
     */
    public static byte[] generateCommand(int addrNumber, String deCommand) {
        byte[] data = new byte[10];
        data[0] = (byte) addrNumber;					// 从机地址
        data[1] = 0x0F;									// 功能码
        data[2] = 0x00;									// 起始地址高字节
        data[3] = 0x00;									// 起始地址低字节
        data[4] = 0x00;									// 寄存器数量高字节
        data[5] = 0x05;									// 寄存器数量低字节
        data[6] = 0x01;									// 字节数

        // 生成16进制元件值
        String[] hexStr = { "0", "1", "2", "3", "4", "5", "6", "7", "8",
                "9", "A", "B", "C", "D", "E", "F" };
        int length = deCommand.length();
        int tempLen = length % 4;
        if (tempLen != 0) {
            for (int i = 0; i < 4 - tempLen; i++) {
                deCommand = "0" + deCommand;
            }
        }
        length = deCommand.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length / 4; i++) {
            int num = 0;
            for (int j = i * 4; j < i * 4 + 4; j++) {
                num <<= 1;
                num |= (deCommand.charAt(j) - '0');
            }
            sb.append(hexStr[num]);
        }
        data[7] = Byte.parseByte(sb.toString(), 16);	// 元件值

        // 计算校验值
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        for (i = 0; i < 8; i++) {
            CRC ^= ((int) data[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        String temp = Integer.toHexString(CRC);
        if (temp != null && temp.length() == 4) {
            int high = Integer.parseInt(temp.substring(2, 4), 16);
            int low = Integer.parseInt(temp.substring(0, 2), 16);
            data[8] = (byte) high;						// 校验高字节
            data[9] = (byte) low;						// 检验低字节
        }
        return data;
    }

    public MySerialPort2 getmMySp() {
        return mMySp;
    }

    public void setmMySp(MySerialPort2 mMySp) {
        this.mMySp = mMySp;
    }
}
