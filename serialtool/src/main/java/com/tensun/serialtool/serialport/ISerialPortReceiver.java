package com.tensun.serialtool.serialport;

/**
 * 串口数据接收接口
 */
public interface ISerialPortReceiver<T> {
    /**
     * 原始数据
     *
     * @param data
     */
    void onRawData(T data);

    /**
     * 无过滤完整数据
     *
     * @param data
     */
    void onNoFilterData(T data);

    /**
     * 过滤后完整数据
     *
     * @param data
     */
    void onFilterData(T data);

    /**
     * 垃圾数据，丢弃数据
     *
     * @param data
     */
    void onCrashData(T data);
}
