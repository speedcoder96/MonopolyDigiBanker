package rs.de.monopolydigibanker.activity;

import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import rs.de.monopolydigibanker.R;
import rs.de.monopolydigibanker.adapter.PlayerFragmentPagerAdapter;
import rs.de.monopolydigibanker.database.DatabaseHelper;
import rs.de.monopolydigibanker.database.DatabaseSource;

/**
 * Created by Rene on 05.09.2016.
 */
public class GameActivity extends AppCompatActivity {

    public static final String GAME_DATA_BUNDLE_KEY = "gamedata";
    public static final String GAME_ID_KEY = "gameid";

    private DatabaseSource source;
    private DatabaseHelper.Game game;

    public GameActivity() {
        source = DatabaseSource.getInstance(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        /**
         * Load game data for game id which refers to the game to play
         */
        Bundle bundle = getIntent().getBundleExtra(GAME_DATA_BUNDLE_KEY);
        long gameId = bundle.getLong(GAME_ID_KEY);
        source.open();
        game = source.loadGame(gameId);
        source.close();


        /**
         * Sets the title of the action bar to the title of the game
         */
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(game.getTitle());
        }

        /**
         * Creates a fragment for every player in order to show the player data separately for each player
         */
        ViewPager viewPager = (ViewPager) findViewById(R.id.game_viewpager);
        if (viewPager != null) {
            viewPager.setAdapter(new PlayerFragmentPagerAdapter(getSupportFragmentManager(), game));
        }

        /**
         * Creates the tabs with a players name
         */
        TabLayout tabLayout = (TabLayout) findViewById(R.id.game_viewpager_tabs);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);

        }

    }

}
