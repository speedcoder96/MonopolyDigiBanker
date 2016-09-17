package rs.de.monopolydigibanker.activity;

import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

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

    private PlayerFragmentPagerAdapter playerFragmentPagerAdapter;

    public GameActivity() {
        source = DatabaseSource.getInstance(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Bundle bundle = getIntent().getBundleExtra(GAME_DATA_BUNDLE_KEY);
        long gameId = bundle.getLong(GAME_ID_KEY);
        source.open();
        game = source.loadGame(gameId, this);
        source.close();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(game.getTitle());
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.ga_vp);
        if (viewPager != null) {
            playerFragmentPagerAdapter = new PlayerFragmentPagerAdapter(getSupportFragmentManager(), game);
            viewPager.setAdapter(playerFragmentPagerAdapter);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.ga_vp_tabs);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
        }

    }

}
