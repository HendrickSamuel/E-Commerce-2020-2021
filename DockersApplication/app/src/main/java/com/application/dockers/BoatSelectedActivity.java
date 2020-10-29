package com.application.dockers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import protocol.IOBREP.DonneeBoatArrived;

public class BoatSelectedActivity extends AppCompatActivity {
    private String _boatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boat_selected);

        this._boatId = this.getIntent().getExtras().get("boatId").toString();

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
                Intent intent = new Intent(BoatSelectedActivity.this, AccueilActivity.class);
                intent.putExtra("boatId", BoatSelectedActivity.this._boatId);
                startActivity(intent);
            }
        });
    }
}