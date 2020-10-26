package com.application.dockers;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.application.dockers.connection.ServerConnection;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((Button)this.findViewById(R.id.login_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(MainActivity.this, AccueilActivity.class);
                //intent.putExtra("user",((EditText)MainActivity.this.findViewById(R.id.login_username)).getText());
                //startActivity(intent);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("test");
                        new ServerConnection().StartConnection("",1);
                        System.out.println("test");
                    }
                }).start();
            }
        });

    }
}
