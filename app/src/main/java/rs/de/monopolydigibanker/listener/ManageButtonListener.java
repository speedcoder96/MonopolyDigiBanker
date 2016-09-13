package rs.de.monopolydigibanker.listener;

import android.view.View;

import rs.de.monopolydigibanker.database.DatabaseHelper;
import rs.de.monopolydigibanker.fragment.PlayerFragment;

/**
 * Created by Rene on 13.09.2016.
 */
public class ManageButtonListener extends ActionButtonListener {

    public ManageButtonListener(PlayerFragment playerFragment, DatabaseHelper.Game game, DatabaseHelper.Player player) {
        super(playerFragment, game, player);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

}
