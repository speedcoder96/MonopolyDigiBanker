package rs.de.monopolydigibanker.listener;

import android.view.View;

import rs.de.monopolydigibanker.database.DatabaseHelper;
import rs.de.monopolydigibanker.database.model.Game;
import rs.de.monopolydigibanker.database.model.Player;
import rs.de.monopolydigibanker.fragment.PlayerFragment;

/**
 * Created by Rene on 13.09.2016.
 */
public abstract class ActionButtonListener implements View.OnClickListener, View.OnLongClickListener {

    protected PlayerFragment playerFragment;

    protected Game game;
    protected Player player;

    public ActionButtonListener(PlayerFragment playerFragment, Game game, Player player) {
        this.playerFragment = playerFragment;

        this.game = game;
        this.player = player;
    }

}
