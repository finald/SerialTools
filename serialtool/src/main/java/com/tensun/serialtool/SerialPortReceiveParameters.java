package com.tensun.serialtool;


public class SerialPortReceiveParameters {

    /**
     * 与上次接收数据的间隔空闲时间，用于清除缓存
     */
    private int mFreeTime = 2000;

    /**
     * 过滤时间，多长时间内数据完全相同为不需要数据
     */
    private int mFilterTime = 3000;

    public SerialPortReceiveParameters() {
    }

    public SerialPortReceiveParameters(int freeTime, int filterTime) {
        mFreeTime = freeTime;
        mFilterTime = filterTime;
    }

    public int getFreeTime() {
        return mFreeTime;
    }

    public void setFreeTime(int freeTime) {
        mFreeTime = freeTime;
    }

    public int getFilterTime() {
        return mFilterTime;
    }

    public void setFilterTime(int filterTime) {
        mFilterTime = filterTime;
    }
}
