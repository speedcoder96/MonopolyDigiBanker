package rs.de.monopolydigibanker.listener;

import android.content.DialogInterface;
import android.view.View;

import rs.de.monopolydigibanker.database.DatabaseHelper;
import rs.de.monopolydigibanker.fragment.PlayerFragment;

/**
 * Created by Rene on 13.09.2016.
 */
public abstract class ActionButtonListener implements View.OnClickListener, View.OnLongClickListener {

    protected PlayerFragment playerFragment;

    protected DatabaseHelper.Game game;
    protected DatabaseHelper.Player player;

    public ActionButtonListener(PlayerFragment playerFragment, DatabaseHelper.Game game, DatabaseHelper.Player player) {
        this.playerFragment = playerFragment;

        this.game = game;
        this.player = player;
    }

}
