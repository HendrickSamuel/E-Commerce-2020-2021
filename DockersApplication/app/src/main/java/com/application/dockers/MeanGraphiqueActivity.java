package com.application.dockers;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.application.dockers.connection.ServerConnection;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.util.ArrayList;
import java.util.List;

import protocol.IOBREP.Docker;
import protocol.IOBREP.DonneeGetLoadUnloadStats;
import protocol.IOBREP.DonneeGetLoadUnloadTime;
import protocol.IOBREP.ReponseIOBREP;
import protocol.IOBREP.RequeteIOBREP;

public class MeanGraphiqueActivity extends AppCompatActivity {

    int[] colors = new int[] {
            Color.rgb(227, 121, 15),
            Color.rgb(227, 121, 227),
            Color.rgb(127, 121, 127),
            Color.rgb(255, 121, 127),
            Color.rgb(127, 255, 127)};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mean_graphique);

        new GetGraphOne().execute();
    }

    private void openChartOne(List<Docker> dockers)
    {
        PieChart piechart = findViewById(R.id.graphs_one);
        ArrayList<PieEntry> entrees = new ArrayList<>();

        for(Docker doc : dockers)
        {
            entrees.add(new PieEntry((int)doc.getSeccondsToLoad(), doc.getDockerName()));
            System.out.println(doc.getDockerName() + " - " + doc.getSeccondsToLoad());
        }

        PieDataSet pieDataSet = new PieDataSet(entrees, "Temps en entrée");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        piechart.setData(pieData);
        piechart.getDescription().setEnabled(false);
        piechart.setCenterText("Temps entree en seccondes");
        piechart.animate();
        piechart.notifyDataSetChanged();
        piechart.invalidate();
    }

    private void openChartTwo(List<Docker> dockers)
    {
        PieChart piechart = findViewById(R.id.graphs_two);
        ArrayList<PieEntry> entrees = new ArrayList<>();

        for(Docker doc : dockers)
        {
            entrees.add(new PieEntry((float)doc.getSeccondsToUnload(), doc.getDockerName()));
        }

        PieDataSet pieDataSet = new PieDataSet(entrees, "Temps en sortie");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        piechart.setData(pieData);
        piechart.getDescription().setEnabled(false);
        piechart.setCenterText("Temps sortie en seccondes");
        piechart.animate();
        piechart.notifyDataSetChanged();
        piechart.invalidate();
    }

    private class GetGraphOne extends AsyncTask<Void, Void, ReponseIOBREP>
    {
        @Override
        protected void onPostExecute(ReponseIOBREP reponseIOBREP) {
            if(reponseIOBREP.getCode() == 200)
            {
                if(reponseIOBREP.get_chargeUtile() instanceof DonneeGetLoadUnloadTime)
                {
                    DonneeGetLoadUnloadTime rep = (DonneeGetLoadUnloadTime)reponseIOBREP.get_chargeUtile();
                    openChartOne(rep.getDockers());
                    openChartTwo(rep.getDockers());
                }
            }
            else
            {
                Toast.makeText(MeanGraphiqueActivity.this, reponseIOBREP.get_message(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected ReponseIOBREP doInBackground(Void... voids) {
            ServerConnection sc = new ServerConnection();
            sc.TestConnection(MeanGraphiqueActivity.this);
            RequeteIOBREP req = new RequeteIOBREP(new protocol.IOBREP.DonneeGetLoadUnloadTime());
            return sc.SendAndReceiveMessage(MeanGraphiqueActivity.this, req);
        }
    }
}