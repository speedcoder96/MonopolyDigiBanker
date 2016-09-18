package rs.de.monopolydigibanker.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import rs.de.monopolydigibanker.database.DatabaseHelper;
import rs.de.monopolydigibanker.fragment.PlayerFragment;

/**
 * Created by Rene on 05.09.2016.
 */
public class PlayerFragmentPagerAdapter extends FragmentPagerAdapter {

    private DatabaseHelper.Game game;
    private ArrayList<DatabaseHelper.Player> players;

    public PlayerFragmentPagerAdapter(FragmentManager fragmentManager, DatabaseHelper.Game game) {
        super(fragmentManager);

        this.game = game;
        this.players = game.getPlayers();
    }

    @Override
    public int getCount() {
        return players.size();
    }

    @Override
    public Fragment getItem(int position) {
        return PlayerFragment.newInstance(game, players.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return players.get(position).getName();
    }


}
