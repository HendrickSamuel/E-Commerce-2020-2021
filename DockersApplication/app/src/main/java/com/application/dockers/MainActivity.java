package com.application.dockers;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.application.dockers.SQLite.DockersActivitySQLiteHelper;
import com.application.dockers.connection.ServerConnection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
                    @SuppressLint("SimpleDateFormat")
                    @Override
                    public void run() {
                        ServerConnection sc = new ServerConnection();

                        sc.StartConnection("192.168.1.197",5000);

                        DonneeLogin dl = new DonneeLogin(
                                ((EditText)findViewById(R.id.login_username)).getText().toString(),
                                ((EditText)findViewById(R.id.login_userpassword)).getText().toString());
                        RequeteIOBREP demande = new RequeteIOBREP(dl);

                        ReponseIOBREP rep = sc.SendAndReceiveMessage(demande);

                        if(rep.getCode() == ReponseIOBREP.OK)
                        {
                            com.application.dockers.SQLite.SQLiteDataBase.InsertActivity(MainActivity.this,
                                    "MainActivity",
                                    Calendar.getInstance().getTime(),
                                    "Login de " + ((EditText)findViewById(R.id.login_username)).getText().toString());

                            Intent intent = new Intent(MainActivity.this, AccueilActivity.class);
                            intent.putExtra("user",((EditText)MainActivity.this.findViewById(R.id.login_username)).getText());
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            ((TextView)findViewById(R.id.textErreur)).setText(rep.get_message());
                            try {
                                sc.CloseConnection();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });
    }


}
