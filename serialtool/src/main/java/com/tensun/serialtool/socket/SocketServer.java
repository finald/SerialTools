package com.tensun.serialtool.socket;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {

    private ServerSocket mServerSocket;
    private IServer mServer;

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
                    new Thread(handlerThread).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                mBufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                mOutputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            while (true) {
                try {
                    String line = mBufferedReader.readLine();
                    if (line == null) {
                        if (mSocket != null) {
                            try {
                                mSocket.close();
                                mSocket = null;
                            } catch (Exception e1) {
                                mSocket = null;
                            }
                        }
                    } else {
                        if (mServer != null)
                            mServer.rxData(line);
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
                    }
                }
            }
        }
    }
}
