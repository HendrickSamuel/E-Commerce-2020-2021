package com.application.dockers.connection;

import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;

import com.application.dockers.AccueilActivity;
import com.application.dockers.MainActivity;
import com.application.dockers.R;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import protocol.IOBREP.DonneeLogin;
import protocol.IOBREP.ReponseIOBREP;
import protocol.IOBREP.RequeteIOBREP;

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

    public void CloseConnection() throws IOException {
        if(_socket != null && !_socket.isClosed())
        {
            _socket.close();
            _socket = null;
        }
    }

    public Socket get_socket() {
        return _socket;
    }

    public ReponseIOBREP SendAndReceiveMessage(RequeteIOBREP requete)
    {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(_socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(_socket.getInputStream());

            oos.writeObject(requete);
            oos.flush();

            ReponseIOBREP rep = (ReponseIOBREP)ois.readObject();
            return rep;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ReponseIOBREP(ReponseIOBREP.NOK, null, e.getMessage());
        }
    }
}
