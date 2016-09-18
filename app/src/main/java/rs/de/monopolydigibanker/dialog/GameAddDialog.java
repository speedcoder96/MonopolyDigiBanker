package rs.de.monopolydigibanker.dialog;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import rs.de.monopolydigibanker.R;
import rs.de.monopolydigibanker.activity.MainActivity;
import rs.de.monopolydigibanker.util.Util;

/**
 * Created by Rene on 05.09.2016.
 */
public class GameAddDialog extends AlertDialog implements DialogInterface.OnClickListener {

    private static final int MAX_PLAYER_COUNT = 6;

    private LinearLayout inputLinearLayout;
    private EditText gameTitle;
    private ArrayList<EditText> players;

    private Button neutralButton;
    private Button positiveButton;

    private OnAddListener submitListener;

    public GameAddDialog(MainActivity mainActivity) {
        super(mainActivity);

        this.submitListener = mainActivity;

        setTitle(R.string.main_game_add_dialog_title);
        setCancelable(true);

        setButton(AlertDialog.BUTTON_POSITIVE, getContext().getString(R.string.main_game_add_dialog_pos_button), this);
        setButton(AlertDialog.BUTTON_NEGATIVE, getContext().getString(R.string.main_game_add_dialog_neg_button), this);
        setButton(AlertDialog.BUTTON_NEUTRAL, getContext().getString(R.string.main_game_add_dialog_player_button), this);

        inputLinearLayout = new LinearLayout(getContext());
        inputLinearLayout.setOrientation(LinearLayout.VERTICAL);

        gameTitle = new EditText(getContext());
        gameTitle.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        gameTitle.setHint(R.string.main_game_add_dialog_title_hint);
        inputLinearLayout.addView(gameTitle);

        setView(inputLinearLayout);

        players = new ArrayList<EditText>();

        show();

        neutralButton = getButton(AlertDialog.BUTTON_NEUTRAL);
        neutralButton.setText(String.format(getContext().getString(R.string.main_game_add_dialog_player_button), players.size() + 1));
        neutralButton.setOnClickListener(new NeutralButtonListener());

        positiveButton = getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new InputValidator(gameTitle, players));


        showNextPlayer();
        showNextPlayer();

    }

    private void showNextPlayer() {
        EditText playerEditText = new EditText(getContext());
        playerEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        players.add(playerEditText);
        playerEditText.setHint(String.format(getContext().getString(R.string.main_game_add_dialog_player_button), players.size()));
        inputLinearLayout.addView(playerEditText);

        neutralButton.setText(String.format(getContext().getString(R.string.main_game_add_dialog_player_button), players.size() + 1));

        if(players.size() == MAX_PLAYER_COUNT) {
            neutralButton.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which) {
            case AlertDialog.BUTTON_NEGATIVE:
                dismiss();
                break;
        }
    }

    private class NeutralButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            showNextPlayer();
        }
    }

    private class InputValidator implements View.OnClickListener {

        private EditText gameTitle;
        private ArrayList<EditText> players;

        public InputValidator(EditText gameTitle, ArrayList<EditText> players) {
            this.gameTitle = gameTitle;
            this.players = players;
        }

        @Override
        public void onClick(View v) {
            boolean error = false;
            if(gameTitle.getText().toString().length() == 0) {
                gameTitle.setBackgroundColor(Color.RED);
                error = true;
            } else {
                gameTitle.setBackgroundColor(Color.GREEN);
            }
            for(EditText player : players) {
                if(player.getText().toString().length() == 0) {
                    player.setBackgroundColor(Color.RED);
                    error = true;
                } else {
                    player.setBackgroundColor(Color.GREEN);
                }
            }

            if(!error) {
                if(submitListener != null) {
                    submitListener.onSubmit(gameTitle.getText().toString(), Util.toStringValues(players));
                }
                dismiss();
            } else {
                Toast.makeText(getContext(), R.string.main_game_add_dialog_error, Toast.LENGTH_LONG).show();
            }
        }
    }

    public static interface OnAddListener {
        public void onSubmit(String gameTitle, ArrayList<String> playerNames);
    }

}
