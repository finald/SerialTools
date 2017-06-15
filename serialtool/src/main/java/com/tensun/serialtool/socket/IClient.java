package com.tensun.serialtool.socket;

public interface IClient {
    void rxData(String data);

    void onConnected();

    void onClosed();
}
