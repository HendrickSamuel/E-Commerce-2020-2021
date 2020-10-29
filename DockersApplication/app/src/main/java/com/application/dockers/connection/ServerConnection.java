package com.application.dockers.connection;

import java.io.IOException;
import java.net.Socket;

public class ServerConnection {

    private static Socket _socket;

    public ServerConnection()
    {
    }

    public void StartConnection(String adresse, int port)
    {
        try {
            _socket = new Socket(adresse, port);
            System.out.println("done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket get_socket() {
        return _socket;
    }
}
