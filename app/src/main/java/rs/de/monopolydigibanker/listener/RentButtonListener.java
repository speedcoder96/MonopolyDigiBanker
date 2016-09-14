package rs.de.monopolydigibanker.listener;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;

import java.util.ArrayList;

import rs.de.monopolydigibanker.R;
import rs.de.monopolydigibanker.database.DatabaseHelper;
import rs.de.monopolydigibanker.dialog.GameAddDialog;
import rs.de.monopolydigibanker.dialog.PayAmountDialog;
import rs.de.monopolydigibanker.dialog.PlayerSelectionDialog;
import rs.de.monopolydigibanker.fragment.PlayerFragment;


/**
 * Created by Rene on 13.09.2016.
 */
public class RentButtonListener extends ActionButtonListener implements PlayerSelectionDialog.OnSelectListener,
        PayAmountDialog.OnPaymentDoneListener {


    public RentButtonListener(PlayerFragment playerFragment, DatabaseHelper.Game game, DatabaseHelper.Player player) {
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
            Snackbar snackbar = Snackbar.make(playerFragment.getActivity().findViewById(android.R.id.content),
                    R.string.info_player_balance_zero, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.action_player_balance_zero, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
            playerFragment.setRentButtonEnabled(false);
        }

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }


    @Override
    public void onSelect(ArrayList<DatabaseHelper.Player> targetPlayers) {
        PayAmountDialog payAmountDialog = new PayAmountDialog(playerFragment.getContext());
        payAmountDialog.setDialogTitle(R.string.game_rent_transfer_dialog_title);
        payAmountDialog.setLowerFactorButton(R.string.game_pay_amount_dialog_factor1, 1000.0f);
        payAmountDialog.setHigherFactorButton(R.string.game_pay_amount_dialog_factor2, 1000000.0f);
        payAmountDialog.setSubmitButtonTitle(R.string.game_rent_amount_dialog_pos_title);
        payAmountDialog.setPaymentDoneListener(this);

        payAmountDialog.setCurrentPlayer(player);
        payAmountDialog.setTargetPlayers(targetPlayers);

        payAmountDialog.showDialog();
    }

    @Override
    public void onUpdate(DatabaseHelper.Player player) {
        PlayerFragment targetFragment = playerFragment.findFragment(player);
        if(targetFragment != null) {
            targetFragment.updateFragment();
        }
    }

    @Override
    public void onPaymentDone() {
        game.setCurrentStateSaved(DatabaseHelper.Game.STATE_UNSAVED);
    }

}
