package rs.de.monopolydigibanker.listener;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;

import rs.de.monopolydigibanker.R;
import rs.de.monopolydigibanker.database.DatabaseHelper;
import rs.de.monopolydigibanker.database.model.Event;
import rs.de.monopolydigibanker.database.model.Game;
import rs.de.monopolydigibanker.database.model.Player;
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

    public GoButtonListener(PlayerFragment playerFragment, Game game, Player player) {
       super(playerFragment, game, player);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(playerFragment.getContext());

        goMoneyValue = Long.parseLong(preferences.getString(
                playerFragment.getContext().getString(R.string.key_preference_go_money),
                playerFragment.getContext().getString(R.string.value_preference_go_money)));

        goMoneyValueFactor = (preferences.getBoolean(
                playerFragment.getContext().getString(R.string.key_preference_double_go_flag),
                playerFragment.getResources().getBoolean(R.bool.value_preference_double_go_flag))) ? 2 : 1;

        currencyCharacter = preferences.getString(
                playerFragment.getContext().getString(R.string.key_preference_currency),
                playerFragment.getContext().getString(R.string.value_preference_currency));
    }

    @Override
    public void onClick(View v) {
        payGo(goMoneyValue, Event.i(Event.GO_MONEY_EVENT));
    }

    @Override
    public boolean onLongClick(View v) {
        int eventId = (goMoneyValueFactor == 2) ?
                Event.i(Event.DOUBLE_GO_MONEY_EVENT) :
                Event.i(Event.GO_MONEY_EVENT);
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
        goMoneyAcceptDialog.setPositiveButton(R.string.game_go_money_accept_dialog_pos_button);
        goMoneyAcceptDialog.setNegativeButton(R.string.game_go_money_accept_dialog_neg_button);
        goMoneyAcceptDialog.setAcceptDialogListener(new AcceptDialog.OnAcceptDialogListener() {
            @Override
            public void onPositive(AcceptDialog.AcceptDialogInterface acceptDialogInterface) {
                long potentialGoMoneyValue = acceptDialogInterface.getData(0, Long.class);
                int eventId = acceptDialogInterface.getData(1, Integer.class);
                player.addBalance(potentialGoMoneyValue);

                game.newLog(eventId, potentialGoMoneyValue, player.getId(), Util.isLoggingActivated(playerFragment.getContext()));
                game.setCurrentStateSaved(Game.STATE_UNSAVED);

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
