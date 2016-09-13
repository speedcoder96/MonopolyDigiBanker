package rs.de.monopolydigibanker.listener;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.View;

import rs.de.monopolydigibanker.R;
import rs.de.monopolydigibanker.database.DatabaseHelper;
import rs.de.monopolydigibanker.fragment.PlayerFragment;
import rs.de.monopolydigibanker.util.Util;

/**
 * Created by Rene on 13.09.2016.
 */
public class GoButtonListener extends ActionButtonListener {

    private long goMoneyValue;
    private int goMoneyValueFactor;

    private String currencyCharacter;

    private AlertDialog.Builder goMoneyAcceptDialogBuilder;

    public GoButtonListener(PlayerFragment playerFragment, DatabaseHelper.Game game, DatabaseHelper.Player player) {
       super(playerFragment, game, player);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(playerFragment.getContext());

        goMoneyValue = Long.parseLong(preferences.getString("preference_go_money_key", "2000000"));
        goMoneyValueFactor = (preferences.getBoolean("preference_go_flag_key", false)) ? 2 : 1;

        goMoneyAcceptDialogBuilder = new AlertDialog.Builder(playerFragment.getContext());
        goMoneyAcceptDialogBuilder.setTitle(R.string.game_go_money_accept_dialog_title);

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

    private void payGo(final long potentialGoMoneyValue) {
        goMoneyAcceptDialogBuilder.setMessage(String.format(
                playerFragment.getString(R.string.game_go_money_accept_dialog_message),
                Util.punctuatedBalance((potentialGoMoneyValue), currencyCharacter), player.getName()));

        goMoneyAcceptDialogBuilder.setPositiveButton(R.string.game_go_money_accept_dialog_pos_title,
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                player.addBalance(potentialGoMoneyValue);
                playerFragment.updateFragment();
                game.setCurrentStateSaved(DatabaseHelper.Game.STATE_UNSAVED);
            }
        });
        goMoneyAcceptDialogBuilder.setNegativeButton(R.string.game_go_money_accept_dialog_neg_title,
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        goMoneyAcceptDialogBuilder.show();
    }


}
