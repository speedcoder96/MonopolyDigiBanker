package rs.de.monopolydigibanker.listener;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;

import rs.de.monopolydigibanker.R;
import rs.de.monopolydigibanker.database.DatabaseHelper;
import rs.de.monopolydigibanker.dialog.AcceptDialog;
import rs.de.monopolydigibanker.fragment.PlayerFragment;
import rs.de.monopolydigibanker.util.Util;

/**
 * Created by Rene on 13.09.2016.
 */
public class GoButtonListener extends ActionButtonListener {

    private long goMoneyValue;
    private int goMoneyValueFactor;

    private String currencyCharacter;

    public GoButtonListener(PlayerFragment playerFragment, DatabaseHelper.Game game, DatabaseHelper.Player player) {
       super(playerFragment, game, player);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(playerFragment.getContext());

        goMoneyValue = Long.parseLong(preferences.getString("preference_go_money_key", "2000000"));
        goMoneyValueFactor = (preferences.getBoolean("preference_go_flag_key", false)) ? 2 : 1;

        currencyCharacter = preferences.getString("preference_currency_key", "");
    }

    @Override
    public void onClick(View v) {
       payGo(goMoneyValue);
    }

    @Override
    public boolean onLongClick(View v) {
        payGo(goMoneyValue * goMoneyValueFactor);
        return true;
    }

    private void payGo(long potentialGoMoneyValue) {

        AcceptDialog goMoneyAcceptDialog = new AcceptDialog(playerFragment.getContext());
        goMoneyAcceptDialog.putData(0, potentialGoMoneyValue);
        goMoneyAcceptDialog.setTitle(R.string.game_go_money_accept_dialog_title);
        goMoneyAcceptDialog.setFormattedMessage(R.string.game_go_money_accept_dialog_message,
                Util.punctuatedBalance((potentialGoMoneyValue), currencyCharacter), player.getName());
        goMoneyAcceptDialog.setPositiveButton(R.string.game_go_money_accept_dialog_pos_title);
        goMoneyAcceptDialog.setNegativeButton(R.string.game_go_money_accept_dialog_neg_title);
        goMoneyAcceptDialog.setAcceptDialogListener(new AcceptDialog.OnAcceptDialogListener() {
            @Override
            public void onPositive(AcceptDialog.AcceptDialogInterface acceptDialogInterface) {
                player.addBalance(acceptDialogInterface.get(0, Long.class));
                playerFragment.updateFragment();
                game.setCurrentStateSaved(DatabaseHelper.Game.STATE_UNSAVED);
            }

            @Override
            public void onNegative(AcceptDialog.AcceptDialogInterface acceptDialogInterface) {
                acceptDialogInterface.dismiss();
            }
        });
        goMoneyAcceptDialog.show();

    }


}
