package com.application.dockers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.application.dockers.connection.ServerConnection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import protocol.IOBREP.Container;
import protocol.IOBREP.DonneeEndContainerIn;
import protocol.IOBREP.DonneeEndContainerOut;
import protocol.IOBREP.DonneeGetContainers;
import protocol.IOBREP.DonneeHandleContainerIn;
import protocol.IOBREP.DonneeHandleContainerOut;
import protocol.IOBREP.ReponseIOBREP;
import protocol.IOBREP.RequeteIOBREP;

public class UnloadActivity extends AppCompatActivity implements View.OnClickListener {

    private String _boatId;

    private int selectedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unload);

        this._boatId = this.getIntent().getExtras().get("boatId").toString();
        ((Button)this.findViewById(R.id.button_unloading_done)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UnloadActivity.UnLoadContainerDoneTask().execute();
            }
        });

        new UnloadActivity.GetContainersTask().execute();
    }

    @Override
    public void onClick(View v) {
        new UnloadActivity.UnLoadContainerTask().execute(((Button)v).getText().toString());
        selectedId = v.getId();
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
                    Button btn = new Button(UnloadActivity.this);
                    btn.setText(cont.getId());
                    btn.setId(i);
                    btn.setOnClickListener(UnloadActivity.this);
                    btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    ll.addView(btn);
                    i++;
                }
            }
        }

        @Override
        protected ReponseIOBREP doInBackground(Void... voids) {
            ServerConnection sc = new ServerConnection();
            sc.TestConnection(UnloadActivity.this);

            try {
                ObjectOutputStream oos = new ObjectOutputStream(sc.get_socket().getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(sc.get_socket().getInputStream());
                DonneeGetContainers dl = new DonneeGetContainers("parc","FIRST", "IN");
                dl.setIdBateau(UnloadActivity.this._boatId);
                RequeteIOBREP demande = new RequeteIOBREP(dl);
                oos.writeObject(demande);
                oos.flush();

                ReponseIOBREP rep = (ReponseIOBREP)ois.readObject();
                System.out.println("Recu liste: " + rep.getCode());
                return rep;

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class UnLoadContainerTask extends AsyncTask<String, Void, ReponseIOBREP>{

        @Override
        protected void onPostExecute(ReponseIOBREP result) {
            if(result.getCode() == 200)
            {
                if(result.get_chargeUtile() instanceof DonneeHandleContainerIn)
                {
                    DonneeHandleContainerIn dhci = (DonneeHandleContainerIn)result.get_chargeUtile();
                    LinearLayout llok = findViewById(R.id.ll_boat_treated);
                    View v = UnloadActivity.this.findViewById(selectedId);

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
            sc.TestConnection(UnloadActivity.this);
            RequeteIOBREP demande = new RequeteIOBREP(new DonneeHandleContainerIn(strings[0]));

            return sc.SendAndReceiveMessage(demande);
        }
    }

    private class UnLoadContainerDoneTask extends AsyncTask<Void, Void, ReponseIOBREP>{

        @Override
        protected void onPostExecute(ReponseIOBREP result) {
            if(result.getCode() == 200)
            {
                Intent intent = new Intent(UnloadActivity.this, BoatSelectedActivity.class);
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
            sc.TestConnection(UnloadActivity.this);
            RequeteIOBREP demande = new RequeteIOBREP(new DonneeEndContainerIn(UnloadActivity.this._boatId));

            return sc.SendAndReceiveMessage(demande);
        }
    }
}