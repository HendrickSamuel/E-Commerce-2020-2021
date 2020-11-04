package com.application.dockers;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.application.dockers.connection.ServerConnection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;

import protocol.IOBREP.DonneeBoatArrived;
import protocol.IOBREP.DonneeLogin;
import protocol.IOBREP.ReponseIOBREP;
import protocol.IOBREP.RequeteIOBREP;

public class AccueilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        TextView tw = this.findViewById(R.id.accueil_username);

        if(this.getIntent().getExtras() != null && this.getIntent().getExtras().containsKey("user"))
            tw.setText(this.getIntent().getExtras().get("user").toString());

        ((Button)this.findViewById(R.id.logs_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.application.dockers.SQLite.SQLiteDataBase.InsertActivity(AccueilActivity.this,
                        "AccueilActivity",
                        Calendar.getInstance().getTime(),
                        "Ouverture des logs");

                Intent intent = new Intent(AccueilActivity.this, LogsActivity.class);
                startActivity(intent);
            }
        });

        ((Button)this.findViewById(R.id.graphics_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.application.dockers.SQLite.SQLiteDataBase.InsertActivity(AccueilActivity.this,
                        "AccueilActivity",
                        Calendar.getInstance().getTime(),
                        "Visualisation des graphiques");

                Intent intent = new Intent(AccueilActivity.this, GraphiquesActivity.class);
                startActivity(intent);
            }
        });

        ((Button)this.findViewById(R.id.graphics_button_one)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.application.dockers.SQLite.SQLiteDataBase.InsertActivity(AccueilActivity.this,
                        "AccueilActivity",
                        Calendar.getInstance().getTime(),
                        "Visualisation des graphiques");

                Intent intent = new Intent(AccueilActivity.this, MeanGraphiqueActivity.class);
                startActivity(intent);
            }
        });

        ((Button)this.findViewById(R.id.graphics_button_two)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.application.dockers.SQLite.SQLiteDataBase.InsertActivity(AccueilActivity.this,
                        "AccueilActivity",
                        Calendar.getInstance().getTime(),
                        "Visualisation des graphiques");

                Intent intent = new Intent(AccueilActivity.this, GraphiquesWeeklyActivity.class);
                startActivity(intent);
            }
        });

        ((Button)this.findViewById(R.id.boat_arrived_button)).setOnClickListener(new View.OnClickListener() {
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
                            DonneeBoatArrived dba = new DonneeBoatArrived(((EditText)findViewById(R.id.input_boat_id)).getText().toString());
                            RequeteIOBREP demande = new RequeteIOBREP(dba);
                            oos.writeObject(demande);
                            oos.flush();

                            ReponseIOBREP rep = (ReponseIOBREP)ois.readObject();
                            System.out.println("Recu: " + rep.getCode());
                            if(rep.getCode() == ReponseIOBREP.OK)
                            {
                                com.application.dockers.SQLite.SQLiteDataBase.InsertActivity(AccueilActivity.this,
                                        "AccueilActivity",
                                        Calendar.getInstance().getTime(),
                                        "Arrivée du bateau " + ((EditText)findViewById(R.id.input_boat_id)).getText().toString());

                                Intent intent = new Intent(AccueilActivity.this, BoatSelectedActivity.class);
                                intent.putExtra("boatId",((DonneeBoatArrived)rep.get_chargeUtile()).getIdContainer());
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(AccueilActivity.this, rep.get_message(), Toast.LENGTH_LONG).show();
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