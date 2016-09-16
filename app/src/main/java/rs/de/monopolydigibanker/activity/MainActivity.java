package rs.de.monopolydigibanker.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import rs.de.monopolydigibanker.database.DatabaseHelper;
import rs.de.monopolydigibanker.database.DatabaseSource;
import rs.de.monopolydigibanker.dialog.AcceptDialog;
import rs.de.monopolydigibanker.dialog.GameAddDialog;
import rs.de.monopolydigibanker.dialog.GameEditDialog;
import rs.de.monopolydigibanker.dialog.OptionsDialog;
import rs.de.monopolydigibanker.util.Util;

public class MainActivity extends AppCompatActivity implements
        GameAddDialog.OnAddListener, View.OnClickListener, View.OnLongClickListener,
        OptionsDialog.OnOptionSelectionListener,
        GameEditDialog.OnEditDoneListener {

    public static final int OPTION_EDIT = 0;
    public static final int OPTION_REMOVE = 1;
    public static final int OPTION_SHARE = 2;
    public static final int OPTION_RESET = 3;

    private GameListViewAdapter gameListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ma_tb_main);
        setSupportActionBar(toolbar);

        ListView gamesListView = (ListView)findViewById(R.id.ma_lv_games);
        gameListViewAdapter = new GameListViewAdapter(this);
        gamesListView.setAdapter(gameListViewAdapter);

        FloatingActionButton gamesAddFab = (FloatingActionButton) findViewById(R.id.ma_fbtn_add);
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
            case R.id.ma_mitem_settings:
                Intent settingsIntent = new Intent(this, SettingsPreferenceActivity.class);
                startActivity(settingsIntent);
                break;

        }
        return super.onOptionsItemSelected(item);
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

        OptionsDialog optionsDialog = new OptionsDialog(this);
        optionsDialog.setData(data);
        optionsDialog.setTitle(R.string.game_list_options_dialog_title);
        optionsDialog.setItems(R.array.game_list_options_dialog);
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
        long gameId = data.getLong("game_id");
        String gameTitle = data.getString("game_title");
        switch(option) {
            case OPTION_EDIT:
                GameEditDialog editDialog = new GameEditDialog(this, data);
                editDialog.setEditDoneListener(this);
                editDialog.show();
                break;
            case OPTION_REMOVE:
                AcceptDialog removeGameAcceptDialog = new AcceptDialog(this);
                removeGameAcceptDialog.putData(0, gameId);
                removeGameAcceptDialog.setFormattedTitle(R.string.game_list_remove_dialog_title, gameTitle);
                removeGameAcceptDialog.setFormattedMessage(R.string.game_list_remove_dialog_message, gameTitle);
                removeGameAcceptDialog.setPositiveButton(R.string.game_list_remove_dialog_pos_title);
                removeGameAcceptDialog.setNegativeButton(R.string.game_list_remove_dialog_neg_title);
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
            case OPTION_SHARE:
                break;
            case OPTION_RESET:
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                long defaultBalance = Long.parseLong(preferences.getString("preference_default_balance_key", "0"));
                AcceptDialog resetGameAcceptDialog = new AcceptDialog(this);
                resetGameAcceptDialog.putData(0, gameId);
                resetGameAcceptDialog.putData(1, defaultBalance);
                resetGameAcceptDialog.setTitle(R.string.game_reset_dialog_title);
                resetGameAcceptDialog.setFormattedMessage(R.string.game_reset_dialog_message,
                        gameTitle, Util.punctuatedBalance(defaultBalance, preferences.getString("preference_currency_key", "")));
                resetGameAcceptDialog.setPositiveButton(R.string.game_reset_dialog_pos_title);
                resetGameAcceptDialog.setNegativeButton(R.string.game_reset_dialog_neg_title);
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

    private void startGame(long gameId) {
        Intent intent = new Intent(this, GameActivity.class);
        Bundle gameData = new Bundle();
        gameData.putLong(GameActivity.GAME_ID_KEY, gameId);
        intent.putExtra(GameActivity.GAME_DATA_BUNDLE_KEY, gameData);
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
        DatabaseHelper.Game game = source.loadGame(gameId);
        for(DatabaseHelper.Player player : game.getPlayers()) {
            player.setBalance(defaultBalance);
            source.updatePlayer(player);
        }
        source.close();
    }

}
