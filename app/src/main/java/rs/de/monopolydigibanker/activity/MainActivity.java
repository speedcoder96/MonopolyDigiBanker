package rs.de.monopolydigibanker.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import rs.de.monopolydigibanker.R;
import rs.de.monopolydigibanker.adapter.GameListViewAdapter;
import rs.de.monopolydigibanker.database.DatabaseSource;
import rs.de.monopolydigibanker.dialog.GameAddDialog;
import rs.de.monopolydigibanker.dialog.GameOptionsDialog;
import rs.de.monopolydigibanker.dialog.GameRemoveDialog;

public class MainActivity extends AppCompatActivity implements
        GameAddDialog.OnAddListener, View.OnClickListener, View.OnLongClickListener,
        GameRemoveDialog.OnRemoveListener, GameOptionsDialog.OnOptionSelectionListener {

    private ListView gamesListView;
    private FloatingActionButton gamesAddFab;

    private GameListViewAdapter gameListViewAdapter;

    private DatabaseSource source;

    public MainActivity() {
        source = DatabaseSource.getInstance(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        /**
         * Sets the adapter of the list view in order to show all stored games
         */
        gamesListView = (ListView)findViewById(R.id.games_listview);
        gameListViewAdapter = new GameListViewAdapter(this);
        gamesListView.setAdapter(gameListViewAdapter);

        /**
         * Sets the onClick listener for the "Add Game" button
         */
        gamesAddFab = (FloatingActionButton) findViewById(R.id.games_add_fab);
        gamesAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GameAddDialog(MainActivity.this);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_item_settings:
                Intent settingsIntent = new Intent(this, SettingsPreferenceActivity.class);
                startActivity(settingsIntent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSubmit(String gameTitle, ArrayList<String> playerNames) {
        finish();

        source.open();
        long gameId = source.storeGame(gameTitle, playerNames);
        source.close();

        startGame(gameId);
    }

    private void startGame(long gameId) {
        Intent intent = new Intent(this, GameActivity.class);
        Bundle gameData = new Bundle();
        gameData.putLong(GameActivity.GAME_ID_KEY, gameId);
        intent.putExtra(GameActivity.GAME_DATA_BUNDLE_KEY, gameData);
        startActivity(intent);
    }

    private void removeGame(long gameId) {
        source.open();
        source.removeGame(gameId);
        source.close();
        gameListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        long gameId = Long.parseLong(v.getTag(R.id.game_id_key).toString());
        startGame(gameId);
    }


    @Override
    public boolean onLongClick(View v) {
        long gameId = Long.parseLong(v.getTag(R.id.game_id_key).toString());
        String gameTitle = v.getTag(R.id.game_title_key).toString();


        Bundle data = new Bundle();
        data.putLong("game_id", gameId);
        data.putString("game_title", gameTitle);
        GameOptionsDialog optionsDialog = new GameOptionsDialog(this,
                R.string.game_list_options_dialog_title,
                R.array.game_list_options_dialog,
                data);
        optionsDialog.setOptionSelectionListener(this);
        optionsDialog.show();

        return false;
    }


    @Override
    public void onRemove(long gameId) {
        removeGame(gameId);
    }

    @Override
    public void onSelect(DialogInterface dialog, Bundle data, int option) {
        switch(option) {
            case 0:
                break;
            case 1:
                GameRemoveDialog removeDialog = new GameRemoveDialog(this,
                        data.getLong("game_id"), data.getString("game_title"));
                removeDialog.setRemoveListener(this);
                removeDialog.show();
                break;
        }

    }

    public void onEmptyGameList() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                R.string.info_game_list_empty, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.action_game_list_empty, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GameAddDialog(MainActivity.this);
            }
        });
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }
}
