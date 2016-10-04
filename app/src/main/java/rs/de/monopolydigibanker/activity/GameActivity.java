package rs.de.monopolydigibanker.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import rs.de.monopolydigibanker.R;
import rs.de.monopolydigibanker.adapter.PlayerFragmentPagerAdapter;
import rs.de.monopolydigibanker.database.DatabaseSource;
import rs.de.monopolydigibanker.database.model.Game;
import rs.de.monopolydigibanker.database.model.Player;
import rs.de.monopolydigibanker.database.model.MonopolyGame;
import rs.de.monopolydigibanker.dialog.StreetSelectionDialog;

/**
 * Created by Rene on 05.09.2016.
 */
public class GameActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {


    private DatabaseSource source;
    private Game game;
    private int currentSelectedPlayerIndex;

    public GameActivity() {
        source = DatabaseSource.getInstance(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Bundle bundle = getIntent().getBundleExtra(getString(R.string.key_all_game_data));
        long gameId = bundle.getLong(getString(R.string.key_all_game_id));
        source.open();
        game = source.loadGame(gameId, this);
        source.close();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(game.getTitle());
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager_game);
        if (viewPager != null) {
            PlayerFragmentPagerAdapter playerFragmentPagerAdapter =
                    new PlayerFragmentPagerAdapter(this, getSupportFragmentManager(), game);
            viewPager.setAdapter(playerFragmentPagerAdapter);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout_game);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.setOnTabSelectedListener(this);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(MonopolyGame.isVariantSelected(this)) {
            getMenuInflater().inflate(R.menu.game_buy_settings, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.item_game_buy:
                Player currentSelectedPlayer = game.getPlayers().get(currentSelectedPlayerIndex);
                openBuyOptions(currentSelectedPlayer);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openBuyOptions(Player currentSelectedPlayer) {

        StreetSelectionDialog streetSelectionDialog = new StreetSelectionDialog(this);

        /**
         * TODO einen Straßenselektionsdialog öffnen,
         * TODO alle freien, nicht gekauften Straßen anzeigen
         * TODO PayAmountDialog öffnen mit fixem Betrag für die Straße
         * TODO nach Bestätigung Konto belasten und Straße dem Spieler zuordnen
         */
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        currentSelectedPlayerIndex = tab.getPosition();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

}
