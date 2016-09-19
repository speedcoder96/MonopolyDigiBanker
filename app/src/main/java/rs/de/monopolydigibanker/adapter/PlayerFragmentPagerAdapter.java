package rs.de.monopolydigibanker.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import java.util.ArrayList;

import rs.de.monopolydigibanker.database.model.Game;
import rs.de.monopolydigibanker.database.model.Player;
import rs.de.monopolydigibanker.fragment.PlayerFragment;

/**
 * Created by Rene on 05.09.2016.
 */
public class PlayerFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context context;

    private Game game;
    private ArrayList<Player> players;

    public PlayerFragmentPagerAdapter(Context context, FragmentManager fragmentManager, Game game) {
        super(fragmentManager);
        this.context = context;

        this.game = game;
        this.players = game.getPlayers();
    }

    @Override
    public int getCount() {
        return players.size();
    }

    @Override
    public Fragment getItem(int position) {
        return PlayerFragment.newInstance(context, game, players.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return players.get(position).getName();
    }


}
