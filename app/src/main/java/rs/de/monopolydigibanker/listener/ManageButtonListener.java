package rs.de.monopolydigibanker.listener;

import android.view.View;

import rs.de.monopolydigibanker.R;
import rs.de.monopolydigibanker.database.DatabaseHelper;
import rs.de.monopolydigibanker.dialog.PayAmountDialog;
import rs.de.monopolydigibanker.fragment.PlayerFragment;

/**
 * Created by Rene on 13.09.2016.
 */
public class ManageButtonListener extends ActionButtonListener implements PayAmountDialog.OnPaymentDoneListener {

    private PayAmountDialog managePaymentDialog;

    public ManageButtonListener(PlayerFragment playerFragment, DatabaseHelper.Game game, DatabaseHelper.Player player) {
        super(playerFragment, game, player);
    }

    private void setupManagePaymentDialog() {
        managePaymentDialog = new PayAmountDialog(playerFragment.getContext());
        managePaymentDialog.setCurrentPlayer(player);
        managePaymentDialog.setLowerFactorButton(R.string.game_pay_amount_dialog_factor1, 1000.0f);
        managePaymentDialog.setHigherFactorButton(R.string.game_pay_amount_dialog_factor2, 1000000.0f);
        managePaymentDialog.setPaymentDoneListener(this);
    }

    @Override
    public void onClick(View v) {
        setupManagePaymentDialog();
        managePaymentDialog.setAddAmountToPlayer(true);
        managePaymentDialog.setDialogTitle(R.string.game_manage_add_money_dialog_title);
        managePaymentDialog.setSubmitButtonTitle(R.string.game_manage_add_money_pos_title);
        managePaymentDialog.showDialog();
    }

    @Override
    public boolean onLongClick(View v) {
        setupManagePaymentDialog();
        managePaymentDialog.setAddAmountToPlayer(false);
        managePaymentDialog.setDialogTitle(R.string.game_manage_subtract_money_dialog_title);
        managePaymentDialog.setSubmitButtonTitle(R.string.game_manage_subtract_money_pos_title);
        managePaymentDialog.showDialog();
        return true;
    }

    @Override
    public void onUpdate(DatabaseHelper.Player player) {
        PlayerFragment fragment = playerFragment.findFragment(player);
        if(fragment != null) {
            fragment.updateFragment();
        }
    }

    @Override
    public void onPaymentDone() {
        game.setCurrentStateSaved(DatabaseHelper.Game.STATE_UNSAVED);
    }
}
