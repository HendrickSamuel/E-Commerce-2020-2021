package com.application.dockers;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AccueilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        TextView tw = this.findViewById(R.id.accueil_username);
        tw.setText(this.getIntent().getExtras().get("user").toString());

        ((Button)this.findViewById(R.id.accueil_load_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccueilActivity.this, LoadActivity.class);
                startActivity(intent);
            }
        });

        ((Button)this.findViewById(R.id.accueil_unload_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccueilActivity.this, UnloadActivity.class);
                startActivity(intent);
            }
        });
    }
}