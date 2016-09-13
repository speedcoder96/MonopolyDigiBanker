package rs.de.monopolydigibanker.dialog;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;

import rs.de.monopolydigibanker.database.DatabaseHelper;
import rs.de.monopolydigibanker.fragment.PlayerFragment;

/**
 * Created by Rene on 13.09.2016.
 */
public class PlayerSelectionDialog extends AlertDialog.Builder implements DialogInterface.OnClickListener,
    DialogInterface.OnMultiChoiceClickListener {

    private OnSelectListener selectListener;


    private DatabaseHelper.Game game;
    private ArrayList<DatabaseHelper.Player> targetPlayers;

    private String[] otherPlayersNames;
    private long[] otherPlayersIds;
    private boolean[] selectedPlayers;

    private boolean multiChoice;
    private int positiveButtonTitleId;
    private int negativeButtonTitleId;

    public PlayerSelectionDialog(PlayerFragment currentPlayerFragment, DatabaseHelper.Game game,
                                 DatabaseHelper.Player currentPlayer, boolean multiChoice) {
        super(currentPlayerFragment.getContext());
        this.multiChoice = multiChoice;


        this.game = game;
        this.targetPlayers = new ArrayList<>();

        otherPlayersIds = game.retrieveOtherPlayersIds(currentPlayer);
        otherPlayersNames = game.retrieveOtherPlayersNames(currentPlayer);
        selectedPlayers = new boolean[otherPlayersIds.length];

    }

    public AlertDialog show() {
        if(multiChoice) {
            setPositiveButton(positiveButtonTitleId, this);
            setNegativeButton(negativeButtonTitleId, this);
            setMultiChoiceItems(otherPlayersNames, null, this);
        } else {
            setItems(otherPlayersNames, this);
        }
        setCancelable(true);
        return super.show();
    }

    public void setPositiveButtonTitle(int positiveButtonTitleId) {
        this.positiveButtonTitleId = positiveButtonTitleId;
    }

    public void setNegativeButtonTitle(int negativeButtonTitleId) {
        this.negativeButtonTitleId = negativeButtonTitleId;
    }

    public void setSelectListener(OnSelectListener selectListener) {
        this.selectListener = selectListener;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(!multiChoice) {
            targetPlayers.add(game.retrievePlayerData(otherPlayersIds, which));
            onSelectEvent(targetPlayers);
        } else {
            if(which == DialogInterface.BUTTON_POSITIVE) {
                for(int i = 0; i < selectedPlayers.length; i++) {
                    if(selectedPlayers[i]) {
                        targetPlayers.add(game.retrievePlayerData(otherPlayersIds, i));
                    }
                }
                onSelectEvent(targetPlayers);
            }
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if(multiChoice) {
            selectedPlayers[which] = isChecked;
        }
    }

    private void onSelectEvent(ArrayList<DatabaseHelper.Player> targetPlayers) {
        if(selectListener != null)
            selectListener.onSelect(targetPlayers);
    }

    public static interface OnSelectListener {
        public void onSelect(ArrayList<DatabaseHelper.Player> targetPlayers);
    }


}
