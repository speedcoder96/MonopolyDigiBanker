package rs.de.monopolydigibanker.listener;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;

import rs.de.monopolydigibanker.R;
import rs.de.monopolydigibanker.activity.SettingsPreferenceActivity;
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

        goMoneyValue = Long.parseLong(preferences.getString(SettingsPreferenceActivity.SETTING_GO_MONEY, "2000000"));
        goMoneyValueFactor = (preferences.getBoolean(SettingsPreferenceActivity.SETTING_GO_MONEY_FLAG, false)) ? 2 : 1;

        currencyCharacter = preferences.getString(SettingsPreferenceActivity.SETTING_CURRENCY_CHAR, "");
    }

    @Override
    public void onClick(View v) {
        payGo(goMoneyValue, DatabaseHelper.Event.i(DatabaseHelper.Event.GO_MONEY_EVENT));
    }

    @Override
    public boolean onLongClick(View v) {
        int eventId = (goMoneyValueFactor == 2) ?
                DatabaseHelper.Event.i(DatabaseHelper.Event.DOUBLE_GO_MONEY_EVENT) :
                DatabaseHelper.Event.i(DatabaseHelper.Event.GO_MONEY_EVENT);
        payGo(goMoneyValue * goMoneyValueFactor, eventId);
        return true;
    }

    private void payGo(long potentialGoMoneyValue, int eventId) {

        AcceptDialog goMoneyAcceptDialog = new AcceptDialog(playerFragment.getContext());
        goMoneyAcceptDialog.putData(0, potentialGoMoneyValue);
        goMoneyAcceptDialog.putData(1, eventId);

        goMoneyAcceptDialog.setTitle(R.string.game_go_money_accept_dialog_title);
        goMoneyAcceptDialog.setFormattedMessage(R.string.game_go_money_accept_dialog_message,
                Util.punctuatedBalance((potentialGoMoneyValue), currencyCharacter), player.getName());
        goMoneyAcceptDialog.setPositiveButton(R.string.game_go_money_accept_dialog_pos_title);
        goMoneyAcceptDialog.setNegativeButton(R.string.game_go_money_accept_dialog_neg_title);
        goMoneyAcceptDialog.setAcceptDialogListener(new AcceptDialog.OnAcceptDialogListener() {
            @Override
            public void onPositive(AcceptDialog.AcceptDialogInterface acceptDialogInterface) {
                long potentialGoMoneyValue = acceptDialogInterface.getData(0, Long.class);
                int eventId = acceptDialogInterface.getData(1, Integer.class);
                player.addBalance(potentialGoMoneyValue);

                game.newLog(eventId, potentialGoMoneyValue, player.getId(), Util.isLoggingActivated(playerFragment.getContext()));
                game.setCurrentStateSaved(DatabaseHelper.Game.STATE_UNSAVED);

                playerFragment.updateFragment();
            }

            @Override
            public void onNegative(AcceptDialog.AcceptDialogInterface acceptDialogInterface) {
                acceptDialogInterface.dismiss();
            }
        });
        goMoneyAcceptDialog.show();

    }


}
