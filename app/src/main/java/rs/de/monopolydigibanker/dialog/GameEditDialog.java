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

        long gameId = data.getLong("game_id");
        gameTitle = data.getString("game_title");

        DatabaseSource source = DatabaseSource.getInstance(context);
        source.open();
        game = source.loadGame(gameId);
        source.close();

        setTitle(String.format(context.getString(R.string.game_edit_dialog_title), gameTitle));
        setCancelable(false);

        setPositiveButton(R.string.game_edit_dialog_pos_title, this);
        setNegativeButton(R.string.game_edit_dialog_neg_title, this);

        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        gameTitleEditText = new EditText(context);
        gameTitleEditText.setText(gameTitle);
        linearLayout.addView(gameTitleEditText);

        itemViews = new ArrayList<>();

        ArrayList<DatabaseHelper.Player> players = game.getPlayers();
        for(DatabaseHelper.Player player : players) {
            View itemView = inflater.inflate(R.layout.game_edit_dialog_item_layout, linearLayout, false);

            EditText playerNameEditText = (EditText) itemView.findViewById(R.id.ged_et_player_name);
            playerNameEditText.setTag(R.id.ged_et_player_name_tag, player);
            playerNameEditText.setText(player.getName());

            ImageButton removePlayerImageButton = (ImageButton)itemView.findViewById(R.id.ged_ibtn_player_remove);
            removePlayerImageButton.setOnClickListener(this);
            removePlayerImageButton.setTag(R.id.ged_ibtn_player_remove_tag, player);
            removePlayerImageButton.setTag(R.id.ged_ibtn_player_remove_view, itemView);

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
                    EditText playerNameEditText = (EditText) itemView.findViewById(R.id.ged_et_player_name);
                    if(playerNameEditText.getText().length() > 0) {
                        DatabaseHelper.Player player = (DatabaseHelper.Player) playerNameEditText.getTag(R.id.ged_et_player_name_tag);
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
        DatabaseHelper.Player player = (DatabaseHelper.Player)playerRemoveImageButton.getTag(R.id.ged_ibtn_player_remove_tag);

        AcceptDialog removePlayerAcceptDialog = new AcceptDialog(getContext());
        removePlayerAcceptDialog.putData(0, playerRemoveImageButton);
        removePlayerAcceptDialog.putData(1, player);
        removePlayerAcceptDialog.setTitle(R.string.game_edit_dialog_remove_title);
        removePlayerAcceptDialog.setFormattedMessage(R.string.game_edit_dialog_remove_message, player.getName(), gameTitle);
        removePlayerAcceptDialog.setPositiveButton(R.string.game_edit_dialog_remove_pos);
        removePlayerAcceptDialog.setNegativeButton(R.string.game_edit_dialog_remove_neg);
        removePlayerAcceptDialog.setAcceptDialogListener(new AcceptDialog.OnAcceptDialogListener() {
            @Override
            public void onPositive(AcceptDialog.AcceptDialogInterface acceptDialogInterface) {
                ImageButton playerRemoveImageButton = acceptDialogInterface.get(0, ImageButton.class);
                DatabaseHelper.Player player = acceptDialogInterface.get(1, DatabaseHelper.Player.class);
                View itemView = (View) playerRemoveImageButton.getTag(R.id.ged_ibtn_player_remove_view);
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
        source.updateGame(game);
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
