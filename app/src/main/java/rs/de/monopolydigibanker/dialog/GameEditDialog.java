package rs.de.monopolydigibanker.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;

import rs.de.monopolydigibanker.R;
import rs.de.monopolydigibanker.database.DatabaseHelper;
import rs.de.monopolydigibanker.database.DatabaseSource;

/**
 *             2016 created by Rene
 * Project:    MonopolyDigiBanker
 * Package:    rs.de.monopolydigibanker.dialog
 * Class:      GameEditDialog
 *
 */
public class GameEditDialog extends AlertDialog.Builder implements DialogInterface.OnClickListener, View.OnClickListener {

    private LinearLayout linearLayout;

    private EditText gameTitleEditText;
    private ArrayList<View> itemViews;

    private String gameTitle;

    private DatabaseHelper.Game game;

    private OnEditDoneListener editDoneListener;

    public GameEditDialog(Context context, Bundle data) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        long gameId = data.getLong(context.getString(R.string.key_all_game_id));
        gameTitle = data.getString(context.getString(R.string.key_all_game_title));

        DatabaseSource source = DatabaseSource.getInstance(context);
        source.open();
        game = source.loadGame(gameId, context);
        source.close();

        setTitle(String.format(context.getString(R.string.main_game_edit_dialog_title), gameTitle));
        setCancelable(false);

        setPositiveButton(R.string.main_game_edit_pos_button, this);
        setNegativeButton(R.string.main_game_edit_neg_button, this);

        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        gameTitleEditText = new EditText(context);
        gameTitleEditText.setText(gameTitle);
        linearLayout.addView(gameTitleEditText);

        itemViews = new ArrayList<>();

        ArrayList<DatabaseHelper.Player> players = game.getPlayers();
        for(DatabaseHelper.Player player : players) {
            View itemView = inflater.inflate(R.layout.item_gameeditdialog, linearLayout, false);

            EditText playerNameEditText = (EditText) itemView.findViewById(R.id.edittext_gameeditdialog_playername);
            playerNameEditText.setTag(R.id.tag_player_remove_name, player);
            playerNameEditText.setText(player.getName());

            ImageButton removePlayerImageButton = (ImageButton)itemView.findViewById(R.id.imagebutton_gameeditdialog_remove);
            removePlayerImageButton.setOnClickListener(this);
            removePlayerImageButton.setTag(R.id.tag_player_remove_button, player);
            removePlayerImageButton.setTag(R.id.tag_player_remove_view, itemView);

            linearLayout.addView(itemView);
            itemViews.add(itemView);
        }

        setView(linearLayout);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which) {
            case DialogInterface.BUTTON_POSITIVE:
                for(View itemView : itemViews) {
                    EditText playerNameEditText = (EditText) itemView.findViewById(R.id.edittext_gameeditdialog_playername);
                    if(playerNameEditText.getText().length() > 0) {
                        DatabaseHelper.Player player = (DatabaseHelper.Player) playerNameEditText.getTag(R.id.tag_player_remove_name);
                        player.setName(playerNameEditText.getText().toString());
                        updatePlayer(player);
                    }
                }
                if(gameTitleEditText.getText().length() > 0) {
                    game.setTitle(gameTitleEditText.getText().toString());
                    updateGame(game);
                }
                dialog.dismiss();
                onEditDoneEvent();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                dialog.dismiss();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        ImageButton playerRemoveImageButton = (ImageButton)v;
        DatabaseHelper.Player player = (DatabaseHelper.Player)playerRemoveImageButton.getTag(R.id.tag_player_remove_button);

        AcceptDialog removePlayerAcceptDialog = new AcceptDialog(getContext());
        removePlayerAcceptDialog.putData(0, playerRemoveImageButton);
        removePlayerAcceptDialog.putData(1, player);
        removePlayerAcceptDialog.setTitle(R.string.main_game_edit_dialog_remove_title);
        removePlayerAcceptDialog.setFormattedMessage(R.string.main_game_edit_dialog_remove_message, player.getName(), gameTitle);
        removePlayerAcceptDialog.setPositiveButton(R.string.main_game_edit_dialog_remove_pos_button);
        removePlayerAcceptDialog.setNegativeButton(R.string.main_game_edit_dialog_remove_neg_button);
        removePlayerAcceptDialog.setAcceptDialogListener(new AcceptDialog.OnAcceptDialogListener() {
            @Override
            public void onPositive(AcceptDialog.AcceptDialogInterface acceptDialogInterface) {
                ImageButton playerRemoveImageButton = acceptDialogInterface.getData(0, ImageButton.class);
                DatabaseHelper.Player player = acceptDialogInterface.getData(1, DatabaseHelper.Player.class);
                View itemView = (View) playerRemoveImageButton.getTag(R.id.tag_player_remove_view);
                linearLayout.removeView(itemView);
                itemViews.remove(itemView);
                removePlayer(player);
            }

            @Override
            public void onNegative(AcceptDialog.AcceptDialogInterface acceptDialogInterface) {
                acceptDialogInterface.dismiss();
            }
        });
        removePlayerAcceptDialog.show();

    }

    private void updateGame(DatabaseHelper.Game game) {
        DatabaseSource source = DatabaseSource.getInstance(getContext());
        source.open();
        source.updateGameTitle(game);
        source.close();
    }

    private void updatePlayer(DatabaseHelper.Player player) {
        DatabaseSource source = DatabaseSource.getInstance(getContext());
        source.open();
        source.updatePlayer(player);
        source.close();
    }

    private void removePlayer(DatabaseHelper.Player player) {
        DatabaseSource source = DatabaseSource.getInstance(getContext());
        source.open();
        source.removePlayer(player);
        source.close();
    }

    private void onEditDoneEvent() {
        if(editDoneListener != null) {
            editDoneListener.onEditDone();
        }
    }

    public void setEditDoneListener(OnEditDoneListener editDoneListener) {
        this.editDoneListener = editDoneListener;
    }

    public static interface OnEditDoneListener {
        public void onEditDone();
    }

}
