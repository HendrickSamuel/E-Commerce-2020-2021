package com.application.dockers.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DockersActivitySQLiteHelper extends SQLiteOpenHelper
{
    public static final String NOM_TABLE = "activites";

    public static final String COLONNE_ID = "_id";
    public static final String COLONNE_ACTIVITE = "activite";
    public static final String COLONNE_ACTION = "actions";
    public static final String COLONNE_DATE = "dates";

    public static final String DATABASE_NAME = "DockersActivity.db";
    private static final int VERSION = 1;

    public DockersActivitySQLiteHelper(@Nullable Context context)
    {
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {
        System.out.println("ICI");
        db.execSQL("CREATE TABLE " + NOM_TABLE + " (" +
                COLONNE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLONNE_ACTIVITE + " text not null, " +
                COLONNE_DATE + " text not null, " +
                COLONNE_ACTION + " text not null)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.w(DockersActivitySQLiteHelper.class.getName(),
                "Mise à jour de la version " + oldVersion + " à "
                        + newVersion + " - les anciennes données seront perdues");
        db.execSQL("drop table if exists " + NOM_TABLE);
        onCreate(db);
    }
}
