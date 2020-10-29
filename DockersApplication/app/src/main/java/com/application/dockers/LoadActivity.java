package com.application.dockers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.dockers.connection.OnResponseListener;
import com.application.dockers.connection.ServerConnection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import protocol.IOBREP.Container;
import protocol.IOBREP.DonneeGetContainers;
import protocol.IOBREP.DonneeLogin;
import protocol.IOBREP.ReponseIOBREP;
import protocol.IOBREP.RequeteIOBREP;

public class LoadActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        new DownloadFilesTask().execute();
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, ""+v.getId()+" - " + ((Button)v).getText(), Toast.LENGTH_LONG).show();
    }

    private class DownloadFilesTask extends AsyncTask<Void, Void, ReponseIOBREP> {

        protected void onPostExecute(ReponseIOBREP result) {
            if(result.get_chargeUtile() instanceof DonneeGetContainers)
            {
                LinearLayout ll = findViewById(R.id.ll_boat_totreated);

                DonneeGetContainers dgc = (DonneeGetContainers)result.get_chargeUtile();
                int i = 0;
                for(Container cont : dgc.get_containers())
                {
                    Button btn = new Button(LoadActivity.this);
                    btn.setText(cont.getId());
                    btn.setId(i);
                    btn.setOnClickListener(LoadActivity.this);
                    btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    ll.addView(btn);
                    i++;
                }
            }
        }

        @Override
        protected ReponseIOBREP doInBackground(Void... voids) {
            System.out.println("test");
            ServerConnection sc = new ServerConnection();
            System.out.println("connecté");

            try {
                ObjectOutputStream oos = new ObjectOutputStream(sc.get_socket().getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(sc.get_socket().getInputStream());
                DonneeGetContainers dl = new DonneeGetContainers("Paris","FIRST");
                RequeteIOBREP demande = new RequeteIOBREP(dl);
                oos.writeObject(demande);
                oos.flush();

                ReponseIOBREP rep = (ReponseIOBREP)ois.readObject();
                System.out.println("Recu: " + rep.getCode());
                if(rep.getCode() == ReponseIOBREP.OK)
                {
                    return rep;
                }
                else
                {
                    return null;
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}