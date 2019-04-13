package com.krystian.dodgeball.highscore;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HighscoreDatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "HighscoreDatabase";
    public static final int DB_VERSION = 1;

    public HighscoreDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + HighscoreDatabase.TABLE_NAME + "(" +
                " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                HighscoreDatabase.NAME + " TEXT," +
                HighscoreDatabase.SCORE + " REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(DB_VERSION < 1) onCreate(db);
    }
}
