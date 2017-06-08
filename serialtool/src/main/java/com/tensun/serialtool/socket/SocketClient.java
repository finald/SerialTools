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
    }

    private class ConnetThread implements Runnable {

        @Override
        public void run() {
            while (true) {
                if (mSocket == null || !mSocket.isConnected()) {
                    try {
                        mSocket = new Socket(mIp, mPort);
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

    private class HandlerThread implements Runnable {
        private BufferedReader mBufferedReader;
        private OutputStream mOutputStream;

        public HandlerThread() {
            try {
                mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                mOutputStream = mSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            while (true) {
                try {
                    String line = mBufferedReader.readLine();
                    if (mClient != null)
                        mClient.rxData(line);
                } catch (IOException e) {
                    e.printStackTrace();
                    if (mSocket != null) {
                        try {
                            mSocket.close();
                            mSocket = null;
                        } catch (Exception e1) {
                            mSocket = null;
                        }
                    }
                }
            }
        }
    }
}
