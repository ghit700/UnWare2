package com.xmrbi.unware.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

/**
 * EIO控制器工具类
 * @author chj
 * @description 控制数据：FF00-接通或为低电平，0000-断开或高电平
 */
public class EioUtils {

	public static final byte HIGH_LEVEL = (byte) 0xFF;
	public static final byte LOW_LEVEL = (byte) 0x00;
	public static final byte LIGHT_ON = (byte) 0xFF;
	public static final byte LIGHT_OFF = (byte) 0x00;

	/** TCP设备通讯端口 **/
	public static final int PORT = 502;


	/**
	 * 控门
	 * @param ip
	 * @param port
	 * @param OpenOrCloseChannel
	 * @param pauseChannel
	 */
	public static void ctrlDoor(String ip, Integer port, Integer OpenOrCloseChannel, Integer pauseChannel) {
		Socket socket = connect(ip, port != null ? port : PORT);
		if (pauseChannel != null) {
			EioUtils.ctrlSingleOutput(socket, pauseChannel, HIGH_LEVEL);
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			EioUtils.ctrlSingleOutput(socket, pauseChannel, LOW_LEVEL);
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		}
		EioUtils.ctrlSingleOutput(socket, OpenOrCloseChannel, HIGH_LEVEL);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		EioUtils.ctrlSingleOutput(socket, OpenOrCloseChannel, LOW_LEVEL);
		disconnect(socket);
	}

	/**
	 * 控制多盏灯
	 * @param ip			IO设备ip
	 * @param port			IO设备端口
	 * @param channels		控制输出口
	 * @param onOff			控制亮灭
	 */
	public static void ctrlMultipleLight(String ip, Integer port, List<Integer> channels, boolean onOff) {
		Socket socket = connect(ip, port != null ? port : PORT);
		for (Integer channel : channels) {
			EioUtils.ctrlSingleOutput(socket, channel, onOff ? LIGHT_ON : LIGHT_OFF);
		}
		disconnect(socket);
	}

	/**
	 * 控制多盏灯
	 * @param ip			IO设备ip
	 * @param port			IO设备端口
	 * @param channels		控制输出口
	 * @param onOff			控制亮灭
	 */
	public static void ctrlMultipleLight(String ip, Integer port, Integer[] channels, boolean onOff) {
		Socket socket = connect(ip, port != null ? port : PORT);
		for (int i = 0; i < channels.length; i++) {
			EioUtils.ctrlSingleOutput(socket, channels[i], onOff ? LIGHT_ON : LIGHT_OFF);
		}
		disconnect(socket);
	}
	/**
	 * 控制多盏灯
	 * @param ip			IO设备ip
	 * @param port			IO设备端口
	 * @param lightChannels 亮灯数组
	 * @param darkChannels  灭灯数组
	 */
	public static void ctrlMultipleLight(String ip, Integer port, List<Integer> lightChannels,  List<Integer> darkChannels) {
		Socket socket = connect(ip, port != null ? port : PORT);
		for (int i = 0; i < darkChannels.size(); i++) {
			EioUtils.ctrlSingleOutput(socket, darkChannels.get(i),  LIGHT_OFF );
		}
		for (int i = 0; i < lightChannels.size(); i++) {
			EioUtils.ctrlSingleOutput(socket, lightChannels.get(i),  LIGHT_ON );
		}
		disconnect(socket);
	}

	/**
	 * 控制单路输出
	 * Modbus报文
	 * 传输ID		5字节		默认：0x00
	 * 数据长度		1字节
	 * 子设备ID		1字节		默认：0x01
	 * 功能码		1字节		控制单路开关量输出：0x05
	 * 寄存器地址	2字节		单路(十进制：30；十六进制：0x1E，起始)
	 * 控制数据		2字节		0xFF 0x00.接通()，0x00 0x00.关
	 */
	public static void ctrlSingleOutput(Socket socket, int channel, byte ctrl) {
		byte[] send = new byte[12];
		send[5] = (byte) 0x06;
		send[6] = (byte) 0x01;
		send[7] = (byte) 0x05;
		send[9] = (byte) (30 - 1 + channel);
		send[10] = ctrl;
		byte[] receive = null;
		try {
			DataOutputStream output = new DataOutputStream(socket.getOutputStream());
			output.write(send);
			output.flush();
			DataInputStream input = new DataInputStream(socket.getInputStream());
			receive = new byte[12];
			input.read(receive);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Socket connect(String ip, int port) {
		Socket socket = null;
		try {
			socket = new Socket(ip, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return socket;
	}

	private static void disconnect(Socket socket) {
		if (socket != null) {
			try {
				socket.getOutputStream().close();
				socket.getInputStream().close();
				socket.close();
			} catch (IOException e) {}
		}
	}

	/**
	 * boolean数组转byte
	 * @param array
	 * @return
	 */
	public static byte booleanArrayToByte(boolean[] array) {
		if (array != null && array.length > 0) {
			byte b = 0;
			for (int i = 7; i >= 0; i--) {
				if (array[i])
					b += (1 << i);
			}
			return b;
		}
		return 0;
	}

	/**
	 * byte数组转16进制字符串
	 * @param array
	 * @return
	 */
	public static String byteArrayToHexString(byte[] array) {
		StringBuffer buffer = new StringBuffer();
		if (array != null && array.length > 0) {
			for (int i = 0; i < array.length; i++) {
				String hex = Integer.toHexString(array[i] & 0xFF);
				if (hex.length() == 1)
					buffer.append('0');
				buffer.append(hex.toUpperCase());
			}
		}
		return buffer.toString();
	}

}
