package com.tensun.serialtool.socket;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class SocketClient {


    private String mIp;
    private int mPort;
    private Socket mSocket;
    private IClient mClient;

    public SocketClient(String ip, int port, IClient client) {
        mIp = ip;
        mPort = port;
        mClient = client;
        new Thread(new ConnectThread()).start();
    }

    private class ConnectThread implements Runnable {

        @Override
        public void run() {
            while (true) {
                if (mSocket == null) {
                    try {
                        mSocket = new Socket(mIp, mPort);
                        if (mClient != null)
                            mClient.onSuccessLink();
                        new Thread(new HandlerThread()).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setIp(String ip) {
        mIp = ip;
    }

    public void setPort(int port) {
        mPort = port;
    }

    public void reconnet() {
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSocket = null;
        }
    }

    public void sendMessage(byte[] msg) {
        if (mSocket != null) {
            try {
                mSocket.getOutputStream().write(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private class HandlerThread implements Runnable {
        private BufferedReader mBufferedReader;
        private OutputStream mOutputStream;

        public HandlerThread() {
            try {
                mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream(), "gbk"));
                mOutputStream = mSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    String line = mBufferedReader.readLine();
                    if (line == null) {
                        if (mSocket != null) {
                            try {
                                mSocket.close();
                                mSocket = null;
                            } catch (Exception e) {
                                mSocket = null;
                            }
                            Thread.currentThread().interrupt();
                        }
                        return;
                    } else {
                        if (mClient != null)
                            mClient.rxData(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (mSocket != null) {
                        try {
                            mSocket.close();
                            mSocket = null;
                        } catch (Exception e1) {
                            mSocket = null;
                        }
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }
}
