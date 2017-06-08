package com.tensun.serialtool.socket;


import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketServer {

    private final String TAG = "SocketServer";
    private ServerSocket mServerSocket;
    private IServer mServer;

    private List<Socket> mSocketList = new ArrayList<>();

    public SocketServer(int port, IServer server) {
        try {
            mServerSocket = new ServerSocket(port);
            new Thread(new initThread()).start();
            mServer = server;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class initThread implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    Socket client = mServerSocket.accept();
                    HandlerThread handlerThread = new HandlerThread(client);
                    mSocketList.add(client);
                    new Thread(handlerThread).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendAllMessage(String message) {
        for (int i = 0; i < mSocketList.size(); i++) {
            try {
                mSocketList.get(i).getOutputStream().write(message.getBytes("gbk"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class HandlerThread implements Runnable {
        private Socket mSocket;
        private BufferedReader mBufferedReader;
        private OutputStream mOutputStream;

        public HandlerThread(Socket socket) {
            mSocket = socket;
            try {
                mBufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "gbk"));
                mOutputStream = socket.getOutputStream();
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
                            mSocketList.remove(mSocket);
                            try {
                                mSocket.close();
                                mSocket = null;
                            } catch (Exception e1) {
                                mSocket = null;
                            }
                            Thread.currentThread().interrupt();
                        }
                    } else {
                        if (mServer != null)
                            mServer.rxData(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (mSocket != null) {
                        mSocketList.remove(mSocket);
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
            Log.d(TAG, "客户端线程关闭");
        }
    }
}
