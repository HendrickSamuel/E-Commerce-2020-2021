package com.application.dockers;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.application.dockers.connection.ServerConnection;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.HashMap;
import java.util.List;

import protocol.IOBREP.Day;
import protocol.IOBREP.DonneeGetLoadUnloadStats;
import protocol.IOBREP.DonneeGetLoadUnloadStatsWeekly;
import protocol.IOBREP.ReponseIOBREP;
import protocol.IOBREP.RequeteIOBREP;
import protocol.IOBREP.Week;

public class GraphiquesWeeklyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphiques_weekly);

        new GetGraphOne().execute();
    }

    private void openChartOne(List<Week> weeks){

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        HashMap<String, XYSeries> valeurs = new HashMap<>();

        for(Week week : weeks)
        {
            for(int i = 0; i < week.getDestinations().size(); i++)
            {
                XYSeries serie;
                if(valeurs.containsKey(week.getDestinations().get(i)))
                {
                     serie = valeurs.get(week.getDestinations().get(i));
                }
                else
                {
                    serie = new XYSeries(week.getDestinations().get(i));
                    valeurs.put(week.getDestinations().get(i), serie);
                }

                serie.add(week.getWeekNumber(), week.getLoadedContainers().get(i)); // faire pareil pour les déchargés
            }
        }

        for(String key : valeurs.keySet())
        {
            dataset.addSeries(valeurs.get(key));
        }

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


        for(Week week : weeks)
        {
            multiRenderer.addXTextLabel(weeks.indexOf(week), "week-" + week.getWeekNumber());
        }

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

        multiRenderer.addSeriesRenderer(incomeRenderer);
        multiRenderer.addSeriesRenderer(expenseRenderer);

        GraphicalView gv = ChartFactory.getBarChartView(getBaseContext(), dataset, multiRenderer, BarChart.Type.DEFAULT);
        LinearLayout layout = (LinearLayout)findViewById(R.id.graphs_one);
        layout.addView(gv, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
    }

    private class GetGraphOne extends AsyncTask<Void, Void, ReponseIOBREP>
    {

        @Override
        protected void onPostExecute(ReponseIOBREP reponseIOBREP) {
            if(reponseIOBREP.getCode() == 200)
            {
                if(reponseIOBREP.get_chargeUtile() instanceof DonneeGetLoadUnloadStatsWeekly)
                {
                    DonneeGetLoadUnloadStatsWeekly rep = (DonneeGetLoadUnloadStatsWeekly)reponseIOBREP.get_chargeUtile();
                    openChartOne(rep.getWeeks());
                }
            }
            else
            {
                Toast.makeText(GraphiquesWeeklyActivity.this, reponseIOBREP.get_message(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected ReponseIOBREP doInBackground(Void... voids) {
            ServerConnection sc = new ServerConnection();
            RequeteIOBREP req = new RequeteIOBREP(new protocol.IOBREP.DonneeGetLoadUnloadStatsWeekly());
            return sc.SendAndReceiveMessage(req);
        }
    }
}