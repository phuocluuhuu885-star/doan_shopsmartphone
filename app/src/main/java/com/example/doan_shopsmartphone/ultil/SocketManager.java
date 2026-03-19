package com.example.doan_shopsmartphone.ultil;

import com.example.doan_shopsmartphone.api.BaseApi;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketManager {
    private static SocketManager instance;
    private Socket mSocket;

    private SocketManager() {
        try {
            // Sử dụng 10.0.2.2 nếu dùng máy ảo Android để kết nối với localhost máy tính
            mSocket = IO.socket("http://10.0.2.2:3000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static synchronized SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    public Socket getSocket() {
        return mSocket;
    }

    public void connect() {
        if (!mSocket.connected()) {
            mSocket.connect();
        }
    }

    public void disconnect() {
        mSocket.disconnect();
    }
}