package com.tensun.serialtool.serialport;


import android.serialport.SerialPort;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;


public class SerialPortReceiveThread implements Runnable {
    private final String TAG = "SerialPortReceiveThread";

    private ISerialPortReceiver mSerialPortReceiver;
    private volatile boolean mStop = false;
    private SerialPort mSerialPort;
    private long mReceiveTime = 0;
    private LinkedList<String> mReceivedList = new LinkedList<String>();
    private byte[] mReceiveBuffer;


    private SerialPortReceiveParameters mParameters;

    public SerialPortReceiveThread(SerialPort serialPort) {
        mSerialPort = serialPort;
        mParameters = new SerialPortReceiveParameters();
    }

    public SerialPortReceiveThread(SerialPort serialPort, ISerialPortReceiver serialPortReceiver) {
        mSerialPort = serialPort;
        mSerialPortReceiver = serialPortReceiver;
        mParameters = new SerialPortReceiveParameters();
    }

    @Override
    public void run() {
        while (!mStop) {
            try {
                byte[] bytes = new byte[0];
                bytes = SerialHelper.readBytes(mSerialPort);
                mReceiveBuffer = ByteUtil.appendByte(mReceiveBuffer, bytes);
                if (mSerialPortReceiver != null)
                    mSerialPortReceiver.onRawData(ByteUtil.bytesToHexString(bytes));
                if (mReceiveBuffer.length >= 3) {
                    int index = 0;
                    int state = 1;
                    byte[] cacheBuffer = null;
                    for (int i = 0; i < mReceiveBuffer.length; i++) {
                        byte b = mReceiveBuffer[i];
                        switch (state) {
                            case 1:
                                if (b == -2)
                                    state = 2;
                                else {
                                    state = 1;
                                    i = -1;
                                    if (mSerialPortReceiver != null)
                                        mSerialPortReceiver.onCrashData(String.format("%02X", mReceiveBuffer[0]), 1);
                                    mReceiveBuffer = Arrays.copyOfRange(mReceiveBuffer, 1, mReceiveBuffer.length);
                                }
                                break;
                            case 2:
                                index = 0;
                                int length = Integer.valueOf(Integer.toHexString(b & 0xFF), 16);

                                if (length <= 0 || length >= 100) {
                                    state = 1;
                                    i = -1;
                                    if (mSerialPortReceiver != null)
                                        mSerialPortReceiver.onCrashData(String.format("%02X", mReceiveBuffer[0]), 2);
                                    mReceiveBuffer = Arrays.copyOfRange(mReceiveBuffer, 1, mReceiveBuffer.length);
                                } else {
                                    cacheBuffer = new byte[length + 3];
                                    cacheBuffer[index++] = (byte) 0xFE;
                                    cacheBuffer[index++] = b;
                                    state = 3;
                                }
                                break;
                            case 3:
                                cacheBuffer[index++] = b;
                                state = 3;
                                if (index == Integer.valueOf(Integer.toHexString(cacheBuffer[1] & 0xFF), 16) + 2) {
                                    state = 4;
                                }
                                break;
                            case 4:
                                cacheBuffer[index] = b;
                                if (b == 0x55) {
                                    int data_length = cacheBuffer[1] & 0xFF;
                                    if (mSerialPortReceiver != null)
                                        mSerialPortReceiver.onNoFilterData(ByteUtil.bytesToHexString(mReceiveBuffer, data_length + 3));
                                    long now = System.currentTimeMillis();
                                    if (now - mReceiveTime >= mParameters.getFreeTime())
                                        mReceivedList.clear();
                                    mReceiveTime = System.currentTimeMillis();
                                    String data = ByteUtil.bytesToHexString(mReceiveBuffer, data_length + 3);
                                    if ((!SerialPortDataUtils.filterData(mReceivedList, data, mParameters.getFilterTime())) && data.startsWith("FE") && data.endsWith("55")) {
                                        if (mSerialPortReceiver != null)
                                            mSerialPortReceiver.onFilterData(data);
                                    }
                                    mReceiveBuffer = Arrays.copyOfRange(mReceiveBuffer, data_length, mReceiveBuffer.length);
                                } else {
                                    if (mSerialPortReceiver != null)
                                        mSerialPortReceiver.onCrashData(String.format("%02X", mReceiveBuffer[0]), 3);
                                    mReceiveBuffer = Arrays.copyOfRange(mReceiveBuffer, 1, mReceiveBuffer.length);
                                    i = -1;
                                    cacheBuffer = null;
                                }
                                state = 1;
                                break;
                            default:
                                state = 1;
                                break;
                        }
                    }
                }
                Thread.sleep(2);
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e(TAG, e.getStackTrace().toString());
                break;
            } catch (InterruptedException e) {
                Log.e(TAG, e.getStackTrace().toString());
                break;
            } catch (StringIndexOutOfBoundsException e) {
                Log.e(TAG, e.getStackTrace().toString());
                break;
            } catch (IOException e) {
                Log.e(TAG, e.getStackTrace().toString());
                break;
            }
        }
        if (mSerialPortReceiver != null)
            mSerialPortReceiver.onComplete();
    }

    public void setSerialPortReceiver(ISerialPortReceiver serialPortReceiver) {
        mSerialPortReceiver = serialPortReceiver;
    }

    public void setStop(boolean stop) {
        mStop = stop;
    }

    public SerialPortReceiveParameters getParameters() {
        return mParameters;
    }

    public void setParameters(SerialPortReceiveParameters parameters) {
        mParameters = parameters;
    }
}
