package com.krystian.dodgeball.highscore;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.krystian.dodgeball.MainActivity;
import com.krystian.dodgeball.R;

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
                new String[]{HighscoreDatabase.NAME, HighscoreDatabase.SCORE}, //show data from database in a listView
                new int[]{R.id.highscore_name, R.id.highscore_score}, 0);

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                String name = cursor.getColumnName(columnIndex);
                if(name.equals(HighscoreDatabase.SCORE)) {
                    TextView points = (TextView) view;
                    points.setText(String.format(getString(R.string.highscore), cursor.getFloat(2))); //show score with 2 decimal places
                    Log.v("Points", points.getText().toString());
                    return true;
                }
                return false;
            }
        });

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
