package com.application.dockers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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
import protocol.IOBREP.DonneeEndContainerOut;
import protocol.IOBREP.DonneeGetContainers;
import protocol.IOBREP.DonneeHandleContainerIn;
import protocol.IOBREP.DonneeHandleContainerOut;
import protocol.IOBREP.DonneeLogin;
import protocol.IOBREP.ReponseIOBREP;
import protocol.IOBREP.RequeteIOBREP;

public class LoadActivity extends AppCompatActivity implements View.OnClickListener {
    private String _boatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        this._boatId = this.getIntent().getExtras().get("boatId").toString();
        ((Button)this.findViewById(R.id.button_loading_done)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoadContainerDoneTask().execute();
            }
        });

        new GetContainersTask().execute();
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, ""+v.getId()+" - " + ((Button)v).getText(), Toast.LENGTH_LONG).show();
        new LoadContainerTask().execute(((Button)v).getText().toString());
    }

    private class GetContainersTask extends AsyncTask<Void, Void, ReponseIOBREP> {

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
                DonneeGetContainers dl = new DonneeGetContainers("Paris","FIRST", "OUT");
                RequeteIOBREP demande = new RequeteIOBREP(dl);
                oos.writeObject(demande);
                oos.flush();

                ReponseIOBREP rep = (ReponseIOBREP)ois.readObject();
                System.out.println("Recu: " + rep.getCode());
                return rep;

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class LoadContainerTask extends AsyncTask<String, Void, ReponseIOBREP>{

        @Override
        protected void onPostExecute(ReponseIOBREP result) {
            if(result.getCode() == 200)
            {
                if(result.get_chargeUtile() instanceof DonneeHandleContainerOut)
                {
                    DonneeHandleContainerOut dhco = (DonneeHandleContainerOut)result.get_chargeUtile();
                    LinearLayout ll = findViewById(R.id.ll_boat_totreated);
                    LinearLayout llok = findViewById(R.id.ll_boat_treated);
                    View v = LoadActivity.this.findViewById(0);

                    ViewGroup parent = (ViewGroup) v.getParent();

                    if (parent != null) {
                        parent.removeView(v);
                    }

                    llok.addView(v);
                    v.setOnClickListener(null);
                }
            }
            else
            {
                System.out.println(result.get_message());
            }

        }

        @Override
        protected ReponseIOBREP doInBackground(String... strings) {
            ServerConnection sc = new ServerConnection();
            System.out.println("connecté");

            try {
                ObjectOutputStream oos = new ObjectOutputStream(sc.get_socket().getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(sc.get_socket().getInputStream());
                DonneeHandleContainerOut dhco = new DonneeHandleContainerOut(strings[0]);
                RequeteIOBREP demande = new RequeteIOBREP(dhco);
                oos.writeObject(demande);
                oos.flush();

                ReponseIOBREP rep = (ReponseIOBREP)ois.readObject();
                System.out.println("Recu: " + rep.getCode());
                return rep;

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class LoadContainerDoneTask extends AsyncTask<Void, Void, ReponseIOBREP>{

        @Override
        protected void onPostExecute(ReponseIOBREP result) {
            if(result.getCode() == 200)
            {
                Intent intent = new Intent(LoadActivity.this, BoatSelectedActivity.class);
                startActivity(intent);
            }
            else
            {
                System.out.println(result.get_message());
            }

        }

        @Override
        protected ReponseIOBREP doInBackground(Void... voids) {
            ServerConnection sc = new ServerConnection();
            System.out.println("connecté");

            try {
                ObjectOutputStream oos = new ObjectOutputStream(sc.get_socket().getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(sc.get_socket().getInputStream());
                DonneeEndContainerOut deco = new DonneeEndContainerOut(LoadActivity.this._boatId);
                RequeteIOBREP demande = new RequeteIOBREP(deco);
                oos.writeObject(demande);
                oos.flush();

                ReponseIOBREP rep = (ReponseIOBREP)ois.readObject();
                System.out.println("Recu: " + rep.getCode());
                return rep;

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}