package com.application.dockers;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.application.dockers.connection.ServerConnection;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.achartengine.*;
import org.achartengine.model.*;
import org.achartengine.renderer.*;

import java.util.ArrayList;
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

        BarChart barchart = findViewById(R.id.barChart);
        barchart.setPinchZoom(false);
        barchart.setDrawBarShadow(false);
        barchart.setDrawGridBackground(false);

        XAxis xAxis = barchart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
    }

    private void openChart(List<Day> days)
    {
        int startYear = days.get(0).getDay();

        float groupSpace = 0.08f;
        float barSpace = 0.06f; // x4 DataSet
        float barWidth = 0.4f; // x4 DataSet
        int groupCount = 2;

        BarChart barchart = findViewById(R.id.barChart);

        ArrayList<BarEntry> entrees = new ArrayList<>();
        ArrayList<BarEntry> sorties = new ArrayList<>();

        for(Day day : days)
        {
            entrees.add(new BarEntry(day.getDay(), day.getContainersDecharges()));
            sorties.add(new BarEntry(day.getDay(), day.getContainersCharges()));
        }

        BarDataSet dataSetEntrees = new BarDataSet(entrees, "containers Decharges");
        dataSetEntrees.setColors(Color.rgb(130, 130, 230));
        dataSetEntrees.setValueTextColor(Color.BLACK);
        dataSetEntrees.setValueTextSize(16f);
        dataSetEntrees.setValueFormatter(new LargeValueFormatter());
        dataSetEntrees.setValueTypeface(Typeface.DEFAULT);

        BarDataSet dataSetSorties = new BarDataSet(sorties, "containers Charges");
        dataSetSorties.setColors(Color.rgb(220, 80, 80));
        dataSetSorties.setValueTextColor(Color.BLACK);
        dataSetSorties.setValueTextSize(16f);
        dataSetSorties.setValueFormatter(new LargeValueFormatter());
        dataSetSorties.setValueTypeface(Typeface.DEFAULT);

        BarData bardata = new BarData(dataSetEntrees, dataSetSorties);

        barchart.setFitBars(true);
        barchart.setData(bardata);
        barchart.getDescription().setText("Description");
        barchart.animateY(2000);

        barchart.getXAxis().setAxisMinimum(startYear);
        barchart.getBarData().setBarWidth(barWidth);

        barchart.getXAxis().setAxisMaximum(startYear + barchart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        barchart.groupBars(startYear, groupSpace, barSpace);
        barchart.invalidate();
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
            sc.TestConnection(GraphiquesActivity.this);
            RequeteIOBREP req = new RequeteIOBREP(new protocol.IOBREP.DonneeGetLoadUnloadStats());
            return sc.SendAndReceiveMessage(GraphiquesActivity.this, req);
        }
    }


}