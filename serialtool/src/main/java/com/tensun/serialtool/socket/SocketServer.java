package com.tensun.serialtool.socket;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
    interface Server {
        void rxData(String data);
    }

    private ServerSocket mServerSocket;
    private Server mServer;

    public SocketServer(int port, Server server) {
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
                    if (mServer != null)
                        mServer.rxData(line);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (mSocket != null) {
                        try {
                            mSocket.close();
                            mSocket = null;
                        } catch (Exception e) {
                            mSocket = null;
                        }
                    }
                }
            }
        }
    }
}
