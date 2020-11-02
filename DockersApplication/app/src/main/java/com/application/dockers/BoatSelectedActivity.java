package com.application.dockers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.application.dockers.connection.ServerConnection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;

import protocol.IOBREP.DoneeBoatLeft;
import protocol.IOBREP.DonneeBoatArrived;
import protocol.IOBREP.ReponseIOBREP;
import protocol.IOBREP.RequeteIOBREP;

public class BoatSelectedActivity extends AppCompatActivity {
    private String _boatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boat_selected);

        if(this.getIntent().getExtras() != null && this.getIntent().getExtras().containsKey("boatId"))
            this._boatId = this.getIntent().getExtras().get("boatId").toString();
        else
            this._boatId = "2-KEV-123";

        ((Button)this.findViewById(R.id.accueil_load_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoatSelectedActivity.this, LoadActivity.class);
                intent.putExtra("boatId", BoatSelectedActivity.this._boatId);
                startActivity(intent);
            }
        });

        ((Button)this.findViewById(R.id.accueil_unload_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoatSelectedActivity.this, UnloadActivity.class);
                intent.putExtra("boatId", BoatSelectedActivity.this._boatId);
                startActivity(intent);
            }
        });

        ((Button)this.findViewById(R.id.boat_left_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("test");
                        ServerConnection sc = new ServerConnection();

                        try {
                            ObjectOutputStream oos = new ObjectOutputStream(sc.get_socket().getOutputStream());
                            ObjectInputStream ois = new ObjectInputStream(sc.get_socket().getInputStream());
                            DoneeBoatLeft dba = new DoneeBoatLeft(BoatSelectedActivity.this._boatId);
                            RequeteIOBREP demande = new RequeteIOBREP(dba);
                            oos.writeObject(demande);
                            oos.flush();

                            ReponseIOBREP rep = (ReponseIOBREP)ois.readObject();
                            System.out.println("Recu: " + rep.getCode());
                            if(rep.getCode() == ReponseIOBREP.OK)
                            {
                                com.application.dockers.SQLite.SQLiteDataBase.InsertActivity(BoatSelectedActivity.this,
                                        "BoatSelectedActivity",
                                        Calendar.getInstance().getTime(),
                                        "Départ du bateau " + BoatSelectedActivity.this._boatId);

                                Intent intent = new Intent(BoatSelectedActivity.this, AccueilActivity.class);
                                intent.putExtra("boatId", BoatSelectedActivity.this._boatId);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(BoatSelectedActivity.this, rep.get_message(), Toast.LENGTH_LONG).show();
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