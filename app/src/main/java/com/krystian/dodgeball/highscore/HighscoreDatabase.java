package com.krystian.dodgeball.highscore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import com.krystian.dodgeball.R;

public class HighscoreDatabase {

    public static final String TABLE_NAME = "HighscoreTable";

    public static final String NAME = "NAME"; //table columns
    public static final String SCORE = "SCORE";
    public static final int NUMBER_OF_SCORES = 10; //how many to store

    private HighscoreDatabaseHelper helper;
    private SQLiteDatabase database;

    public HighscoreDatabase() {
    }

    public SQLiteDatabase accessDatabase(Context context) {
        try {
            helper = new HighscoreDatabaseHelper(context);
            database = helper.getWritableDatabase();
            return database;
        } catch (SQLiteException e) {
            e.printStackTrace();
            Toast.makeText(context, R.string.database_unreachable, Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public boolean checkForHighscore(float gameTime) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME +
                " ORDER BY " + SCORE + " DESC", null);
        int scores = cursor.getCount();
        if(scores >= NUMBER_OF_SCORES) { //highscore full - player needs to beat the weakest score
            cursor.moveToLast();
            float lastScore = cursor.getFloat(2);
            cursor.close();
            return gameTime > lastScore;
        }
        else return true; //no enough records yet - player gets highscore anyway
    }

    public void updateHighscore(String newName, float newScore) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME +
                " ORDER BY " + SCORE + " DESC", null);
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, newName);
        contentValues.put(SCORE, newScore);
        if(cursor.getCount() < NUMBER_OF_SCORES)
            database.insert(TABLE_NAME, null, contentValues);
        else {
            Cursor lastScoreCursor = database.rawQuery("SELECT * FROM " + TABLE_NAME +
                    " ORDER BY " + SCORE + " ASC LIMIT 1", null);
            lastScoreCursor.moveToFirst();
            int rowId = lastScoreCursor.getInt(0);
            database.update(TABLE_NAME, contentValues, "_id = ?", new String[]{String.valueOf(rowId)});
            lastScoreCursor.close();
        }
        cursor.close();
    }


    public void closeDatabase() {
        database.close();
        helper.close();
    }
}
