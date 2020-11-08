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
    private String destination;
    private String order;

    private int selectedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        this._boatId = this.getIntent().getExtras().get("boatId").toString();
        this.destination = this.getIntent().getExtras().get("destination").toString();
        this.order = this.getIntent().getExtras().get("order").toString();

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
        new LoadContainerTask().execute(((Button)v).getText().toString());
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
            ServerConnection sc = new ServerConnection();
            sc.TestConnection(LoadActivity.this);

            DonneeGetContainers dl = new DonneeGetContainers(LoadActivity.this.destination,LoadActivity.this.order, "OUT");
            RequeteIOBREP demande = new RequeteIOBREP(dl);

            return sc.SendAndReceiveMessage(demande);

        }
    }

    private class LoadContainerTask extends AsyncTask<String, Void, ReponseIOBREP>{

        @Override
        protected void onPostExecute(ReponseIOBREP result) {
            if(result.getCode() == 200)
            {
                if(result.get_chargeUtile() instanceof DonneeHandleContainerOut)
                {
                    LinearLayout llok = findViewById(R.id.ll_boat_treated);
                    View v = LoadActivity.this.findViewById(selectedId);

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
            sc.TestConnection(LoadActivity.this);

            RequeteIOBREP demande = new RequeteIOBREP(new DonneeHandleContainerOut(strings[0]));
            return sc.SendAndReceiveMessage(demande);

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
            sc.TestConnection(LoadActivity.this);

            RequeteIOBREP demande = new RequeteIOBREP(new DonneeEndContainerOut(LoadActivity.this._boatId));
            return sc.SendAndReceiveMessage(demande);
        }
    }
}