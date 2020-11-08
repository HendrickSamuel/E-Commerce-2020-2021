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
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

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
        DefaultRenderer rendererGlobal = new DefaultRenderer();

        CategorySeries serieStatload = new CategorySeries("Loading Time");

        rendererGlobal.setApplyBackgroundColor(true);
        rendererGlobal.setBackgroundColor(Color.argb(100,50,50,50));
        rendererGlobal.setZoomButtonsVisible(false);
        rendererGlobal.setFitLegend(true);
        rendererGlobal.setLegendTextSize(60);

        for(Docker doc : dockers)
        {
            serieStatload.add(doc.getDockerName(), doc.getSeccondsToLoad());
            SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
            renderer.setColor(colors[dockers.indexOf(doc)]);
            rendererGlobal.addSeriesRenderer(renderer);
        }

        GraphicalView vue = ChartFactory.getPieChartView(this, serieStatload, rendererGlobal);
        LinearLayout layout = (LinearLayout)findViewById(R.id.graphs_one);
        layout.addView(vue, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
    }

    private void openChartTwo(List<Docker> dockers)
    {
        DefaultRenderer rendererGlobal = new DefaultRenderer();

        CategorySeries serieStatunload = new CategorySeries("Unloading Time");

        rendererGlobal.setApplyBackgroundColor(true);
        rendererGlobal.setBackgroundColor(Color.argb(100,50,50,50));
        rendererGlobal.setZoomButtonsVisible(false);
        rendererGlobal.setFitLegend(true);
        rendererGlobal.setLegendTextSize(60);

        for(Docker doc : dockers)
        {
            serieStatunload.add(doc.getDockerName(), doc.getSeccondsToUnload());
            SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
            renderer.setColor(colors[dockers.indexOf(doc)]);
            rendererGlobal.addSeriesRenderer(renderer);
        }

        GraphicalView vue = ChartFactory.getPieChartView(this, serieStatunload, rendererGlobal);
        LinearLayout layout = (LinearLayout)findViewById(R.id.graphs_two);
        layout.addView(vue, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
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
            return sc.SendAndReceiveMessage(req);
        }
    }
}