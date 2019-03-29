package com.krystian.dodgeball;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class HighscoreListActivity extends ListActivity {

    HighscoreDatabase customDb;
    Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customDb = new HighscoreDatabase();
        cursor = customDb.accessDatabase(this).rawQuery("SELECT * FROM " + HighscoreDatabase.TABLE_NAME +
                " ORDER BY " + HighscoreDatabase.SCORE + " DESC", null);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.highscore_item, cursor,
                new String[]{HighscoreDatabase.NAME, HighscoreDatabase.SCORE},
                new int[]{R.id.highscore_name, R.id.highscore_score}, 0);
        setListAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
        customDb.closeDatabase();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }
}
