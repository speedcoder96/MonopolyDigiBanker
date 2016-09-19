package rs.de.monopolydigibanker.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import rs.de.monopolydigibanker.R;
import rs.de.monopolydigibanker.adapter.GameListViewAdapter;
import rs.de.monopolydigibanker.database.DatabaseHelper;
import rs.de.monopolydigibanker.database.DatabaseSource;
import rs.de.monopolydigibanker.database.model.Game;
import rs.de.monopolydigibanker.database.model.Player;
import rs.de.monopolydigibanker.dialog.AcceptDialog;
import rs.de.monopolydigibanker.dialog.GameAddDialog;
import rs.de.monopolydigibanker.dialog.GameEditDialog;
import rs.de.monopolydigibanker.dialog.OptionsDialog;
import rs.de.monopolydigibanker.util.Util;

public class MainActivity extends AppCompatActivity implements
        GameAddDialog.OnAddListener, View.OnClickListener, View.OnLongClickListener,
        OptionsDialog.OnOptionSelectionListener,
        GameEditDialog.OnEditDoneListener {

    private static final int OPTION_EDIT = 0;
    private static final int OPTION_REMOVE = 1;
    private static final int OPTION_SHARE = 2;
    private static final int OPTION_RESET = 3;
    private static final int OPTION_EXPORT = 4;

    private GameListViewAdapter gameListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        ListView gamesListView = (ListView)findViewById(R.id.listview_main_games);
        gameListViewAdapter = new GameListViewAdapter(this);
        gamesListView.setAdapter(gameListViewAdapter);

        FloatingActionButton gamesAddFab = (FloatingActionButton) findViewById(R.id.fab_main_add);
        gamesAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GameAddDialog(MainActivity.this);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.item_main_settings:
                Intent settingsIntent = new Intent(this, SettingsPreferenceActivity.class);
                startActivity(settingsIntent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        long gameId = Long.parseLong(v.getTag(R.id.tag_all_game_id).toString());
        startGame(gameId);
    }


    @Override
    public boolean onLongClick(View v) {
        long gameId = Long.parseLong(v.getTag(R.id.tag_all_game_id).toString());
        String gameTitle = v.getTag(R.id.tag_all_game_title).toString();

        Bundle data = new Bundle();
        data.putLong(getString(R.string.key_all_game_id), gameId);
        data.putString(getString(R.string.key_all_game_title), gameTitle);

        OptionsDialog optionsDialog = new OptionsDialog(this);
        optionsDialog.setData(data);
        optionsDialog.setTitle(R.string.game_list_options_dialog_title);
        optionsDialog.setItems(R.array.main_game_item_options);
        optionsDialog.setOptionSelectionListener(this);
        optionsDialog.show();

        return true;
    }

    @Override
    public void onSubmit(String gameTitle, ArrayList<String> playerNames) {
        finish();
        DatabaseSource source = DatabaseSource.getInstance(this);
        source.open();
        long gameId = source.storeGame(gameTitle, playerNames);
        source.close();
        startGame(gameId);
    }


    @Override
    public void onEditDone() {
        gameListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSelect(DialogInterface dialog, Bundle data, int option) {
        long gameId = data.getLong(getString(R.string.key_all_game_id));
        String gameTitle = data.getString(getString(R.string.key_all_game_title));

        switch(option) {
            /**
             * OPTION EDIT: The option of editing the games title and manage its players.
             */
            case OPTION_EDIT:

                GameEditDialog editDialog = new GameEditDialog(this, data);
                editDialog.setEditDoneListener(this);
                editDialog.show();
                break;
            /**
             * OPTION REMOVE: The option of removing the game from the database.
             */
            case OPTION_REMOVE:

                AcceptDialog removeGameAcceptDialog = new AcceptDialog(this);
                removeGameAcceptDialog.putData(0, gameId);
                removeGameAcceptDialog.setFormattedTitle(R.string.main_game_remove_dialog_title, gameTitle);
                removeGameAcceptDialog.setFormattedMessage(R.string.main_game_remove_dialog_message, gameTitle);
                removeGameAcceptDialog.setPositiveButton(R.string.main_game_remove_dialog_pos_button);
                removeGameAcceptDialog.setNegativeButton(R.string.main_game_remove_dialog_neg_button);
                removeGameAcceptDialog.setAcceptDialogListener(new AcceptDialog.OnAcceptDialogListener() {

                    @Override
                    public void onPositive(AcceptDialog.AcceptDialogInterface acceptDialogInterface) {
                        removeGame(acceptDialogInterface.getData(0, Long.class));
                    }

                    @Override
                    public void onNegative(AcceptDialog.AcceptDialogInterface acceptDialogInterface) {
                        acceptDialogInterface.dismiss();
                    }
                });
                removeGameAcceptDialog.show();
                break;
            /**
             * OPTION SHARE: The option of sharing the game via bluetooth to another device.
             */
            case OPTION_SHARE:

                Toast.makeText(this, "Out of Order, yet!", Toast.LENGTH_LONG).show();
                break;
            /**
             * OPTION RESET: The option of resetting the games state to the current set setting values.
             */
            case OPTION_RESET:

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                long defaultBalance = Long.parseLong(preferences.getString(
                        getString(R.string.key_preference_default_balance),
                        getString(R.string.value_preference_default_balance)));
                AcceptDialog resetGameAcceptDialog = new AcceptDialog(this);
                resetGameAcceptDialog.putData(0, gameId);
                resetGameAcceptDialog.putData(1, defaultBalance);
                resetGameAcceptDialog.setTitle(R.string.main_game_reset_dialog_title);
                resetGameAcceptDialog.setFormattedMessage(
                        R.string.main_game_reset_dialog_message, gameTitle, Util.punctuatedBalance(defaultBalance,
                                preferences.getString(getString(R.string.key_preference_currency),
                                        getString(R.string.value_preference_currency))));
                resetGameAcceptDialog.setPositiveButton(R.string.main_game_reset_dialog_pos_button);
                resetGameAcceptDialog.setNegativeButton(R.string.main_game_reset_dialog_neg_button);
                resetGameAcceptDialog.setAcceptDialogListener(new AcceptDialog.OnAcceptDialogListener() {
                    @Override
                    public void onPositive(AcceptDialog.AcceptDialogInterface acceptDialogInterface) {
                        long gameId = acceptDialogInterface.getData(0, Long.class);
                        long defaultBalance = acceptDialogInterface.getData(1, Long.class);
                        resetGame(gameId, defaultBalance);
                        acceptDialogInterface.dismiss();
                    }

                    @Override
                    public void onNegative(AcceptDialog.AcceptDialogInterface acceptDialogInterface) {
                        acceptDialogInterface.dismiss();
                    }
                });
                resetGameAcceptDialog.show();
                break;
            /**
             * OPTION EXPORT: The option of exporting the game to a special file for exchange.
             */
            case OPTION_EXPORT:

                Toast.makeText(this, "Export Available", Toast.LENGTH_LONG).show();
                break;
        }

    }

    public void onEmptyGameList() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                R.string.main_info_game_list_empty, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.main_action_game_list_empty, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GameAddDialog(MainActivity.this);
            }
        });
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }

    private void startGame(long gameId) {
        Intent intent = new Intent(this, GameActivity.class);
        Bundle gameData = new Bundle();
        gameData.putLong(getString(R.string.key_all_game_id), gameId);
        intent.putExtra(getString(R.string.key_all_game_data), gameData);
        startActivity(intent);
    }

    private void removeGame(long gameId) {
        DatabaseSource source = DatabaseSource.getInstance(this);
        source.open();
        source.removeGame(gameId);
        source.close();
        gameListViewAdapter.notifyDataSetChanged();
    }

    private void resetGame(long gameId, long defaultBalance) {
        DatabaseSource source = DatabaseSource.getInstance(this);
        source.open();
        Game game = source.loadGame(gameId, this);
        for(Player player : game.getPlayers()) {
            player.setBalance(defaultBalance);
            source.updatePlayer(player);
        }
        source.removeLogs(gameId);
        source.close();
    }

}
