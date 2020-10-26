package com.application.dockers.connection;

import android.os.StrictMode;

import java.io.IOException;
import java.net.Socket;

public class ServerConnection {

    private Socket _socket;

    public ServerConnection()
    {
    }

    public void StartConnection(String adresse, int port)
    {
        try {
            _socket = new Socket("192.168.23.1", 5000);
            System.out.println("done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
