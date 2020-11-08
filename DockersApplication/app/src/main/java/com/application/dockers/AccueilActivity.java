package com.application.dockers;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
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

        //Mettre le nom d'utilisateur en haut pour dire coucou
        TextView tw = this.findViewById(R.id.accueil_username);
        if(this.getIntent().getExtras() != null && this.getIntent().getExtras().containsKey("user"))
            tw.setText(this.getIntent().getExtras().get("user").toString());

        //Aller sur l'activité des logs
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

        //redirection graphique 1
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

        //redirection graphique 2
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

        //Redirection graphique 3
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

        //Arrivee d'un bateau
        ((Button)this.findViewById(R.id.boat_arrived_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ServerConnection sc = new ServerConnection();
                        sc.TestConnection(AccueilActivity.this);
                        RequeteIOBREP demande = new RequeteIOBREP(new DonneeBoatArrived(((EditText)findViewById(R.id.input_boat_id)).getText().toString()));

                        ReponseIOBREP rep = sc.SendAndReceiveMessage(demande);

                        System.out.println("Recu: " + rep.getCode());
                        if(rep.getCode() == ReponseIOBREP.OK)
                        {
                            com.application.dockers.SQLite.SQLiteDataBase.InsertActivity(AccueilActivity.this,
                                    "AccueilActivity",
                                    Calendar.getInstance().getTime(),
                                    "Arrivée du bateau " + ((EditText)findViewById(R.id.input_boat_id)).getText().toString());

                            Intent intent = new Intent(AccueilActivity.this, BoatSelectedActivity.class);
                            intent.putExtra("boatId",((EditText)findViewById(R.id.input_boat_id)).getText().toString());
                            intent.putExtra("destination",((EditText)findViewById(R.id.input_destination_id)).getText().toString());
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(AccueilActivity.this, rep.get_message(), Toast.LENGTH_LONG).show();
                        }

                        System.out.println("fin");
                    }
                }).start();
            }
        });
    }

    @Override
    public void onBackPressed() {
        return;
    }
}