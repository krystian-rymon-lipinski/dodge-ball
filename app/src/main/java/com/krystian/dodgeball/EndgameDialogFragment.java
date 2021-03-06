package com.krystian.dodgeball;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.krystian.dodgeball.game.GameActivity;
import com.krystian.dodgeball.highscore.HighscoreDatabase;
import com.krystian.dodgeball.highscore.HighscoreListActivity;

public class EndgameDialogFragment extends DialogFragment {

    public static final String IS_HIGHSCORE = "isHighscore"; //data about just finished game
    public static final String TIME = "time";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle result = getArguments();
        boolean resultIsHighscore = result.getBoolean(IS_HIGHSCORE);
        final float time = result.getFloat(TIME);

        if(resultIsHighscore) { //result high enough - show a dialog with editText to get a name for highscore
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.highscore_dialog, null);
            builder.setTitle(R.string.congratulations)
                    .setMessage(getResources().getString(R.string.highscore_result, time/1000))
                    .setView(dialogView)
                    .setPositiveButton(R.string.highscore_done, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText editText = dialogView.findViewById(R.id.editable_highscore_name);
                            String name = editText.getText().toString();
                            HighscoreDatabase customDb = new HighscoreDatabase();
                            customDb.accessDatabase(getContext());
                            customDb.updateHighscore(name, time/1000);
                            customDb.closeDatabase();
                            startActivity(new Intent(getContext(), HighscoreListActivity.class));
                        }
                    });
        } else { //show a dialog with game time
            builder.setTitle(R.string.game_lost)
                    .setMessage(getResources().getString(R.string.game_result, time/1000))
                    .setPositiveButton(R.string.restart, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((GameActivity)getActivity()).startGame();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(getActivity(), MainActivity.class));
                        }
                    });
        }
        return builder.create();
    }
}
