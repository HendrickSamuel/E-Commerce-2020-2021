package com.application.dockers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BoatSelectedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boat_selected);

        ((Button)this.findViewById(R.id.accueil_load_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoatSelectedActivity.this, LoadActivity.class);
                startActivity(intent);
            }
        });

        ((Button)this.findViewById(R.id.accueil_unload_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoatSelectedActivity.this, UnloadActivity.class);
                startActivity(intent);
            }
        });

        ((Button)this.findViewById(R.id.boat_left_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BoatSelectedActivity.this, AccueilActivity.class);
                startActivity(intent);
            }
        });
    }
}