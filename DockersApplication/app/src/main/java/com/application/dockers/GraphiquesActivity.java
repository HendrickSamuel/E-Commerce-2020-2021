package com.application.dockers;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.application.dockers.connection.ServerConnection;

import org.achartengine.*;
import org.achartengine.chart.BarChart;
import org.achartengine.model.*;
import org.achartengine.renderer.*;

import java.util.List;

import protocol.IOBREP.Day;
import protocol.IOBREP.DonneeGetLoadUnloadStats;
import protocol.IOBREP.ReponseIOBREP;
import protocol.IOBREP.RequeteIOBREP;

public class GraphiquesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphiques);
        new GetGraphOne().execute();

    }

    private void openChart(List<Day> days){
        XYSeries chargedContainers = new XYSeries("Containers Charges");
        XYSeries unchargedContainers = new XYSeries("Containers Decharges");

        for(Day day : days)
        {
            chargedContainers.add(days.indexOf(day), day.getContainersCharges());
            unchargedContainers.add(days.indexOf(day), day.getContainersDecharges());
        }

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        dataset.addSeries(chargedContainers);
        dataset.addSeries(unchargedContainers);

        XYSeriesRenderer incomeRenderer = new XYSeriesRenderer();
        incomeRenderer.setColor(Color.rgb(130, 130, 230));
        incomeRenderer.setFillPoints(true);
        incomeRenderer.setLineWidth(4);
        incomeRenderer.setDisplayChartValues(true);

        XYSeriesRenderer expenseRenderer = new XYSeriesRenderer();
        expenseRenderer.setColor(Color.rgb(220, 80, 80));
        expenseRenderer.setFillPoints(true);
        expenseRenderer.setLineWidth(4);
        expenseRenderer.setDisplayChartValues(true);

        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setXLabels(0); // mise en 0 x

        multiRenderer.setChartTitle("Input VS Output"); // au dessus et illisible
        multiRenderer.setXTitle("Jours"); // en dessous et illisible
        multiRenderer.setYTitle("Quantité de containers"); // à gauche et illisible
        multiRenderer.setZoomButtonsVisible(true);

        multiRenderer.setAxisTitleTextSize(32);
        multiRenderer.setChartTitleTextSize(40);
        multiRenderer.setLabelsTextSize(30);
        multiRenderer.setLegendTextSize(30);
        multiRenderer.setMargins(new int[] { 60, 80, 30, 0 });

        multiRenderer.setBackgroundColor(Color.argb(100,50,50,50));
        multiRenderer.setXAxisMax(10);
        multiRenderer.setYAxisMax(10);
        multiRenderer.setXAxisMin(-1);
        multiRenderer.setYAxisMin(0);


        for(Day day : days)
        {
            multiRenderer.addXTextLabel(days.indexOf(day), day.getDay());
        }

        multiRenderer.addSeriesRenderer(incomeRenderer);
        multiRenderer.addSeriesRenderer(expenseRenderer);

        GraphicalView gv = ChartFactory.getBarChartView(getBaseContext(), dataset, multiRenderer, BarChart.Type.DEFAULT);
        LinearLayout layout = (LinearLayout)findViewById(R.id.graphs);
        layout.addView(gv, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
    }

    private class GetGraphOne extends AsyncTask<Void, Void, ReponseIOBREP>
    {

        @Override
        protected void onPostExecute(ReponseIOBREP reponseIOBREP) {
            if(reponseIOBREP.getCode() == 200)
            {
                if(reponseIOBREP.get_chargeUtile() instanceof DonneeGetLoadUnloadStats)
                {
                    DonneeGetLoadUnloadStats rep = (DonneeGetLoadUnloadStats)reponseIOBREP.get_chargeUtile();
                    openChart(rep.getJours());
                }
            }
            else
            {
                Toast.makeText(GraphiquesActivity.this, reponseIOBREP.get_message(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected ReponseIOBREP doInBackground(Void... voids) {
            ServerConnection sc = new ServerConnection();
            RequeteIOBREP req = new RequeteIOBREP(new protocol.IOBREP.DonneeGetLoadUnloadStats());
            return sc.SendAndReceiveMessage(req);
        }
    }


}