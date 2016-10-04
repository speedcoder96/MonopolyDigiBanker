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


/**
 * Created by Rene on 13.09.2016.
 */
public class RentButtonListener extends ActionButtonListener implements PlayerSelectionDialog.OnSelectListener,
        PayAmountDialog.OnPaymentDoneListener {


    public RentButtonListener(PlayerFragment playerFragment, Game game, Player player) {
        super(playerFragment, game, player);
    }

    @Override
    public void onClick(View v) {
        if(player.getBalance() > 0) {
            PlayerSelectionDialog playerSelectionDialog = new PlayerSelectionDialog(playerFragment, game, player, false);
            playerSelectionDialog.setSelectListener(this);
            playerSelectionDialog.setTitle(R.string.game_rent_transfer_dialog_title);
            playerSelectionDialog.show();
        } else {
            playerFragment.setRentButtonEnabled(false);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }


    @Override
    public void onSelect(ArrayList<Player> targetPlayers) {
        PayAmountDialog payAmountDialog = new PayAmountDialog(playerFragment.getContext());
        payAmountDialog.setDialogTitle(R.string.game_rent_amount_dialog_title);
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
        PlayerFragment targetFragment = playerFragment.findFragment(player);
        if(targetFragment != null) {
            targetFragment.updateFragment();
        }
    }

    @Override
    public void onPaymentDone(Player player, ArrayList<Player> targetPlayers, long payAmountValue) {
        game.newLog(Event.i(Event.PAY_RENT_EVENT), payAmountValue, player.getId(),
                    targetPlayers.get(0).getId(), Util.isLoggingActivated(playerFragment.getContext()));
        game.setCurrentStateSaved(Game.STATE_UNSAVED);
    }
}
