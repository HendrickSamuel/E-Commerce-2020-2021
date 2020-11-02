package com.application.dockers.SQLite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.EditText;

import com.application.dockers.MainActivity;
import com.application.dockers.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SQLiteDataBase {

    public static long InsertActivity(Context context, String activity, Date date, String action)
    {
        DockersActivitySQLiteHelper sqlite = new DockersActivitySQLiteHelper(context);
        SQLiteDatabase db = sqlite.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DockersActivitySQLiteHelper.COLONNE_ACTIVITE, "MainActivity");
        values.put(DockersActivitySQLiteHelper.COLONNE_DATE, new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(date));
        values.put(DockersActivitySQLiteHelper.COLONNE_ACTION, action);

       return db.insert(DockersActivitySQLiteHelper.NOM_TABLE, null, values);
    }
}
