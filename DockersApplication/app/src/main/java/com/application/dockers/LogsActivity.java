package com.application.dockers;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.application.dockers.SQLite.DockersActivitySQLiteHelper;

import java.util.ArrayList;
import java.util.List;

public class LogsActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);

        List<String> modele = new ArrayList<String>();

        DockersActivitySQLiteHelper sqlite = new DockersActivitySQLiteHelper(LogsActivity.this);
        SQLiteDatabase db = sqlite.getReadableDatabase();
        String[] arr = {DockersActivitySQLiteHelper.COLONNE_ID,DockersActivitySQLiteHelper.COLONNE_ACTION, DockersActivitySQLiteHelper.COLONNE_ACTIVITE, DockersActivitySQLiteHelper.COLONNE_DATE};
        Cursor c = db.query(DockersActivitySQLiteHelper.NOM_TABLE, arr, null, null, null, null, DockersActivitySQLiteHelper.COLONNE_ID );

        c.moveToFirst();
        while (!c.isAfterLast())
        {
            String s = "";
            s += c.getInt(0);
            s += " => " + c.getString(1);
            s += " :: " + c.getString(2);
            s += " :: " + c.getString(3);
            modele.add(s);
            c.moveToNext();
        }
        c.close();

        for(String str : modele)
        {
            System.out.println("test: " + str);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, modele);
        setListAdapter(adapter);

    }
}
