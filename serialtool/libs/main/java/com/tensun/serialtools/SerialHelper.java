package com.tensun.serialtools;



import android.serialport.SerialPort;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class SerialHelper {

    /**
     * 从串口读取数据数组
     * @param serialPort
     * @return
     */
    public static byte[] readBytes(SerialPort serialPort) {
        byte[] bytes = new byte[1024];
        int b = 0;
        try {
            b = serialPort.getInputStream().read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Arrays.copyOfRange(bytes, 0, b);
    }

    /**
     * 从串口读取一个字节数据
     * @param serialPort
     * @return
     */
    public static byte readByte(SerialPort serialPort) {
        byte b = 0;
        try {
            b = (byte) serialPort.getInputStream().read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

    /**
     * 往串口输出数据
     * @param serialPorts
     * @param buffer byte[]
     */
    public static void write(SerialPort serialPorts, byte[] buffer) {
        try {
            serialPorts.getOutputStream().write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 关闭串口
     * @param serialPort
     */
    public static void close(SerialPort serialPort) {
        if (serialPort != null)
            serialPort.close();
    }


    /**
     * 打开指定名称 波特率串口
     * @param device
     * @param baudRate
     * @return
     */
    public static SerialPort open(String device, int baudRate) {
        SerialPort serialPort = null;
        int count = 3;
        while (count-- > 0) {
            try {
                serialPort = new SerialPort(new File(device), baudRate);
                if (serialPort != null) {
                    break;
                }
            } catch (SecurityException e) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                continue;
            } catch (IOException e) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                continue;
            }
        }
        return serialPort;
    }
}
