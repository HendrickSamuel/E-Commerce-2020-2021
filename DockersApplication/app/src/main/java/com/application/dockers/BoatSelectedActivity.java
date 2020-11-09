package com.application.dockers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boat_selected);

        if(this.getIntent().getExtras() != null && this.getIntent().getExtras().containsKey("boatId"))
            LocalInfos.boatId = this.getIntent().getExtras().get("boatId").toString();

        if(this.getIntent().getExtras() != null && this.getIntent().getExtras().containsKey("destination"))
            LocalInfos.destination = this.getIntent().getExtras().get("destination").toString();

        ((Switch)this.findViewById(R.id.switch1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isEnabled())
                    LocalInfos.order = "FIRST";
                else
                    LocalInfos.order = "RAND";
            }
        });

        ((Button)this.findViewById(R.id.accueil_load_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoatSelectedActivity.this, LoadActivity.class);
                intent.putExtra("boatId", LocalInfos.boatId);
                intent.putExtra("destination", LocalInfos.destination);
                intent.putExtra("order", LocalInfos.order);
                startActivity(intent);
            }
        });

        ((Button)this.findViewById(R.id.accueil_unload_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoatSelectedActivity.this, UnloadActivity.class);
                intent.putExtra("boatId", LocalInfos.boatId);
                startActivity(intent);
            }
        });

        ((Button)this.findViewById(R.id.boat_left_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BoatLeft();
            }
        });
    }

    @Override
    public void onBackPressed() {
        BoatLeft();
    }

    private void BoatLeft()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ServerConnection sc = new ServerConnection();
                sc.TestConnection(BoatSelectedActivity.this);

                RequeteIOBREP demande = new RequeteIOBREP(new DoneeBoatLeft(LocalInfos.boatId));
                ReponseIOBREP rep = sc.SendAndReceiveMessage(BoatSelectedActivity.this, demande);

                System.out.println("Recu: " + rep.getCode());
                if(rep.getCode() == ReponseIOBREP.OK)
                {
                    com.application.dockers.SQLite.SQLiteDataBase.InsertActivity(BoatSelectedActivity.this,
                            "BoatSelectedActivity",
                            Calendar.getInstance().getTime(),
                            "Départ du bateau " + LocalInfos.boatId);

                    Intent intent = new Intent(BoatSelectedActivity.this, AccueilActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(BoatSelectedActivity.this, rep.get_message(), Toast.LENGTH_LONG).show();
                }

            }
        }).start();
    }

}