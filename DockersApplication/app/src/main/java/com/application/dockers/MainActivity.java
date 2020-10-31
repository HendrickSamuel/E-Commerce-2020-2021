package com.application.dockers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.application.dockers.connection.ServerConnection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import protocol.IOBREP.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((Button)this.findViewById(R.id.login_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("test");
                        ServerConnection sc = new ServerConnection();
                        sc.StartConnection("192.168.1.197",5000);
                        System.out.println("connecté");

                        try {
                            ObjectOutputStream oos = new ObjectOutputStream(sc.get_socket().getOutputStream());
                            ObjectInputStream ois = new ObjectInputStream(sc.get_socket().getInputStream());
                            DonneeLogin dl = new DonneeLogin(
                                    ((EditText)findViewById(R.id.login_username)).getText().toString(),
                                    ((EditText)findViewById(R.id.login_userpassword)).getText().toString());
                            RequeteIOBREP demande = new RequeteIOBREP(dl);
                            oos.writeObject(demande);
                            oos.flush();

                            ReponseIOBREP rep = (ReponseIOBREP)ois.readObject();
                            System.out.println("Recu: " + rep.getCode());
                            if(rep.getCode() == ReponseIOBREP.OK)
                            {
                                Intent intent = new Intent(MainActivity.this, AccueilActivity.class);
                                intent.putExtra("user",((EditText)MainActivity.this.findViewById(R.id.login_username)).getText());
                                startActivity(intent);
                            }
                            else
                            {
                                ((TextView)findViewById(R.id.textErreur)).setText(rep.get_message());
                            }

                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        System.out.println("fin");
                    }
                }).start();
            }
        });

    }
}
