package rs.de.monopolydigibanker.listener;


import android.view.View;

import java.util.ArrayList;

import rs.de.monopolydigibanker.R;
import rs.de.monopolydigibanker.database.DatabaseHelper;
import rs.de.monopolydigibanker.database.model.Event;
import rs.de.monopolydigibanker.database.model.Game;
import rs.de.monopolydigibanker.database.model.Player;
import rs.de.monopolydigibanker.dialog.PayAmountDialog;
import rs.de.monopolydigibanker.dialog.PlayerSelectionDialog;
import rs.de.monopolydigibanker.fragment.PlayerFragment;
import rs.de.monopolydigibanker.util.Util;

public class TransferButtonListener extends ActionButtonListener implements
        PlayerSelectionDialog.OnSelectListener, PayAmountDialog.OnPaymentDoneListener {

    public TransferButtonListener(PlayerFragment playerFragment, Game game, Player player) {
        super(playerFragment, game, player);
    }

    @Override
    public void onClick(View v) {
        if(player.getBalance() > 0) {
            PlayerSelectionDialog playerSelectionDialog = new PlayerSelectionDialog(playerFragment, game, player, true);
            playerSelectionDialog.setSelectListener(this);
            playerSelectionDialog.setTitle(R.string.game_transfer_money_dialog_title);
            playerSelectionDialog.setPositiveButtonTitle(R.string.game_transfer_money_dialog_pos_button);
            playerSelectionDialog.setNegativeButtonTitle(R.string.game_transfer_money_dialog_neg_button);
            playerSelectionDialog.show();
        } else {
            playerFragment.setTransferButtonEnabled(false);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @Override
    public void onSelect(ArrayList<Player> targetPlayers) {
        PayAmountDialog payAmountDialog = new PayAmountDialog(playerFragment.getContext());
        payAmountDialog.setDialogTitle(String.format(
                playerFragment.getString(R.string.game_transfer_money_dialog_amount_title),
                targetPlayers.size()));
        payAmountDialog.setLowerFactorButton(R.string.all_pay_amount_factor1, 1000.0f);
        payAmountDialog.setHigherFactorButton(R.string.all_pay_amount_factor2, 1000000.0f);
        payAmountDialog.setSubmitButtonTitle(R.string.game_rent_amount_dialog_pos_button);
        payAmountDialog.setPaymentDoneListener(this);

        payAmountDialog.setCurrentPlayer(player);
        payAmountDialog.setTargetPlayers(targetPlayers);

        payAmountDialog.showDialog();
    }

    @Override
    public void onUpdate(Player player) {
        PlayerFragment fragment = playerFragment.findFragment(player);
        if(fragment != null) {
            fragment.updateFragment();
        }
    }

    @Override
    public void onPaymentDone(Player player, ArrayList<Player> targetPlayers, long payAmountValue) {
        int eventId = (targetPlayers.size() == 1)
                ? Event.i(Event.SINGLE_TRANSFER_EVENT)
                : Event.i(Event.MULTIPLE_TRANSFER_EVENT);

        for(Player targetPlayer : targetPlayers) {
            game.newLog(eventId, payAmountValue, player.getId(), targetPlayer.getId(),
                    Util.isLoggingActivated(playerFragment.getContext()));
        }
        game.setCurrentStateSaved(Game.STATE_UNSAVED);
    }
}
