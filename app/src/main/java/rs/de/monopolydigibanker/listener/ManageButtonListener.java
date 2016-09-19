package rs.de.monopolydigibanker.listener;

import android.view.View;

import java.util.ArrayList;

import rs.de.monopolydigibanker.R;
import rs.de.monopolydigibanker.database.DatabaseHelper;
import rs.de.monopolydigibanker.database.model.Event;
import rs.de.monopolydigibanker.database.model.Game;
import rs.de.monopolydigibanker.database.model.Player;
import rs.de.monopolydigibanker.dialog.PayAmountDialog;
import rs.de.monopolydigibanker.fragment.PlayerFragment;
import rs.de.monopolydigibanker.util.Util;

/**
 * Created by Rene on 13.09.2016.
 */
public class ManageButtonListener extends ActionButtonListener implements PayAmountDialog.OnPaymentDoneListener {

    private PayAmountDialog managePaymentDialog;
    private boolean addAmountToPlayer;

    public ManageButtonListener(PlayerFragment playerFragment, Game game, Player player) {
        super(playerFragment, game, player);
    }

    private void setupManagePaymentDialog() {
        managePaymentDialog = new PayAmountDialog(playerFragment.getContext());
        managePaymentDialog.setCurrentPlayer(player);
        managePaymentDialog.setLowerFactorButton(R.string.all_pay_amount_factor1, 1000.0f);
        managePaymentDialog.setHigherFactorButton(R.string.all_pay_amount_factor2, 1000000.0f);
        managePaymentDialog.setPaymentDoneListener(this);
    }

    @Override
    public void onClick(View v) {
        setupManagePaymentDialog();
        addAmountToPlayer = false;
        managePaymentDialog.setAddAmountToPlayer(addAmountToPlayer);
        managePaymentDialog.setDialogTitle(R.string.game_manage_subtract_money_dialog_title);
        managePaymentDialog.setSubmitButtonTitle(R.string.game_manage_subtract_money_pos_button);
        managePaymentDialog.showDialog();
    }

    @Override
    public boolean onLongClick(View v) {
        setupManagePaymentDialog();
        addAmountToPlayer = true;
        managePaymentDialog.setAddAmountToPlayer(addAmountToPlayer);
        managePaymentDialog.setDialogTitle(R.string.game_manage_add_money_dialog_title);
        managePaymentDialog.setSubmitButtonTitle(R.string.game_manage_add_money_pos_button);
        managePaymentDialog.showDialog();
        return true;
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
        int eventId = (addAmountToPlayer)
                ? Event.i(Event.MANAGE_ADD_MONEY_EVENT)
                : Event.i(Event.MANAGE_SUBTRACT_MONEY_EVENT);
        game.newLog(eventId, payAmountValue, player.getId(), Util.isLoggingActivated(playerFragment.getContext()));
        game.setCurrentStateSaved(Game.STATE_UNSAVED);
    }
    
}
