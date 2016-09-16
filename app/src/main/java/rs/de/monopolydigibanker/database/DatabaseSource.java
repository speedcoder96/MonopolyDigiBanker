package rs.de.monopolydigibanker.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;

import rs.de.monopolydigibanker.activity.SettingsPreferenceActivity;


public class DatabaseSource {

    /**
     * Holds the one and only instance of this class.
     * Singleton pattern
     */
    private static DatabaseSource instance;

    /**
     * Holds the instance of the SQLDatabase
     * that is currently open
     */
    private SQLiteDatabase database;

    /**
     * Holds the instance of the DatabaseHelper
     * that contains the database structure and
     * its table definition
     */
    private DatabaseHelper dbHelper;

    /**
     * Holds the instance of the application context
     */
    private Context context;

    /**
     * DatabaseSource private constructor to insure
     * that only one instance of this class is created
     *
     * @param context - the context of the application
     */
    private DatabaseSource(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Static method that returns the singleton instance of
     * this class
     *
     * @param context - the context of the application
     * @return returns the singleton instance
     */
    public static DatabaseSource getInstance(Context context) {
        if(instance == null) {
            instance = new DatabaseSource(context);
        }
        return instance;
    }

    /**
     * Opens a writable database instance with the help of the database helper
     */
    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Closes the database instance that is currently open
     */
    public void close() {
        if(database != null) {
            database.close();
        }
    }

    /**
     * Stores a game with its title and players in database. The connection
     * between each player and their games which they belong to, is stored
     * in a separate table. Each player is stored with the preference balance
     * value set in the settings of the application.
     *
     * @param gameTitle - the title of the game
     * @param playerNames - a list of the player names
     * @return returns the id of the game that the database has allocated
     */
    public long storeGame(String gameTitle, ArrayList<String> playerNames) {

        /**
         * The settings of the app gets loaded in order to get the current set values
         */
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        /**
         * Stores all attributes of the game including title, creation timestamp and balance flag
         */
        ContentValues gameValues = new ContentValues();
        gameValues.put(DatabaseHelper.Game.COLUMN_TITLE, gameTitle);
        gameValues.put(DatabaseHelper.Game.COLUMN_TIMESTAMP, System.currentTimeMillis());
        gameValues.put(DatabaseHelper.Game.COLUMN_BALANCE_FLAG, 0);
        long gameId = database.insert(DatabaseHelper.Game.TABLE_NAME, null, gameValues);

        /**
         * Stores each player including name, start default balance and their connection to
         * the game they belong to
         */
        for(String playerName : playerNames) {

            ContentValues playerValues = new ContentValues();
            playerValues.put(DatabaseHelper.Player.COLUMN_NAME, playerName);
            playerValues.put(DatabaseHelper.Player.COLUMN_BALANCE,
                    Long.parseLong(preferences.getString(SettingsPreferenceActivity.SETTING_BALANCE, "0")));
            long playerId = database.insert(DatabaseHelper.Player.TABLE_NAME, null, playerValues);

            ContentValues gpValues = new ContentValues();
            gpValues.put(DatabaseHelper.GamePlayer.COLUMN_GAME_ID, gameId);
            gpValues.put(DatabaseHelper.GamePlayer.COLUMN_PLAYER_ID, playerId);
            database.insert(DatabaseHelper.GamePlayer.TABLE_NAME, null, gpValues);

        }

        return gameId;
    }

    /**
     * Saves an already created game instance to database.
     * The balance of each player gets updated.
     *
     * @param game - the game instance to save
     */
    public void saveGame(DatabaseHelper.Game game) {
        ArrayList<DatabaseHelper.Player> players = game.getPlayers();
        for(DatabaseHelper.Player player : players) {
            ContentValues playerData = new ContentValues();
            playerData.put(DatabaseHelper.Player.COLUMN_BALANCE, player.getBalance());
            database.update(DatabaseHelper.Player.TABLE_NAME, playerData,
                    DatabaseHelper.where(DatabaseHelper.Player.COLUMN_ID, player.getId()), null);
        }
        game.setCurrentStateSaved(DatabaseHelper.Game.STATE_SAVED);
    }

    /**
     * Loads an already existing game from database, stores it into a game object
     * instance and returns it.
     *
     * @param gameId - the id to identify the game to load
     * @return the loaded game in a game object instance
     */
    public DatabaseHelper.Game loadGame(long gameId) {

        DatabaseHelper.Game game = new DatabaseHelper.Game(gameId);


        Cursor gameData = database.query(DatabaseHelper.Game.TABLE_NAME,
                DatabaseHelper.Game.ALL_COLUMNS,
                DatabaseHelper.where(DatabaseHelper.Game.COLUMN_ID, gameId), null, null, null, null);
        gameData.moveToFirst(); //important!!! Make sure that a cursor starts at index 0, not -1


        game.setTimestamp(gameData.getLong(gameData.getColumnIndex(DatabaseHelper.Game.COLUMN_TIMESTAMP)));
        game.setTitle(gameData.getString(gameData.getColumnIndex(DatabaseHelper.Game.COLUMN_TITLE)));
        game.setBalanceFlag(gameData.getLong(gameData.getColumnIndex(DatabaseHelper.Game.COLUMN_BALANCE_FLAG)));
        gameData.close();


        Cursor playerList = database.query(DatabaseHelper.GamePlayer.TABLE_NAME,
                DatabaseHelper.GamePlayer.ALL_COLUMNS,
                DatabaseHelper.where(DatabaseHelper.GamePlayer.COLUMN_GAME_ID, gameId), null, null, null, null);
        playerList.moveToFirst(); //important!!! Make sure that a cursor starts at index 0, not -1
        while(!playerList.isAfterLast()) {
            long playerId = playerList.getLong(playerList.getColumnIndex(DatabaseHelper.GamePlayer.COLUMN_PLAYER_ID));

            Cursor playerData = database.query(DatabaseHelper.Player.TABLE_NAME,
                    DatabaseHelper.Player.ALL_COLUMNS,
                    DatabaseHelper.where(DatabaseHelper.Player.COLUMN_ID, playerId), null, null, null, null);
            playerData.moveToFirst(); //important!!! Make sure that a cursor starts at index 0, not -1


            DatabaseHelper.Player player = new DatabaseHelper.Player(playerId);
            player.setName(playerData.getString(playerData.getColumnIndex(DatabaseHelper.Player.COLUMN_NAME)));
            player.setBalance(playerData.getLong(playerData.getColumnIndex(DatabaseHelper.Player.COLUMN_BALANCE)));

            game.addPlayer(player);

            playerList.moveToNext();
            playerData.close();
        }
        playerList.close();
        return game;
    }

    /**
     * Updates the title of the game in database.
     * @param game - the game to update the title of
     */
    public void updateGameTitle(DatabaseHelper.Game game) {

        ContentValues gameData = new ContentValues();
        gameData.put(DatabaseHelper.Game.COLUMN_TITLE, game.getTitle());

        database.update(DatabaseHelper.Game.TABLE_NAME, gameData,
                DatabaseHelper.where(DatabaseHelper.Game.COLUMN_ID, game.getId()), null);
    }

    /**
     * Removes a game including the players that belong to it
     * from the database.
     *
     * @param gameId - the id to identify the game to remove
     */
    public void removeGame(long gameId) {


        Cursor playerList = database.query(DatabaseHelper.GamePlayer.TABLE_NAME,
                DatabaseHelper.GamePlayer.ALL_COLUMNS,
                DatabaseHelper.where(DatabaseHelper.GamePlayer.COLUMN_GAME_ID, gameId), null, null, null, null);
        playerList.moveToFirst();


        while(!playerList.isAfterLast()) {
            long playerId = playerList.getLong(playerList.getColumnIndex(DatabaseHelper.GamePlayer.COLUMN_PLAYER_ID));
            database.delete(DatabaseHelper.Player.TABLE_NAME,
                    DatabaseHelper.where(DatabaseHelper.Player.COLUMN_ID, playerId), null);
            playerList.moveToNext();
        }
        playerList.close();


        database.delete(DatabaseHelper.GamePlayer.TABLE_NAME,
                DatabaseHelper.where(DatabaseHelper.GamePlayer.COLUMN_GAME_ID, gameId), null);


        database.delete(DatabaseHelper.Game.TABLE_NAME,
                DatabaseHelper.where(DatabaseHelper.Game.COLUMN_ID, gameId), null);

    }

    /**
     * Updates a player object instance values in database.
     *
     * @param player - the player object to update
     */
    public void updatePlayer(DatabaseHelper.Player player) {

        ContentValues playerData = new ContentValues();
        playerData.put(DatabaseHelper.Player.COLUMN_NAME, player.getName());
        playerData.put(DatabaseHelper.Player.COLUMN_BALANCE, player.getBalance());

        database.update(DatabaseHelper.Player.TABLE_NAME, playerData,
                DatabaseHelper.where(DatabaseHelper.Player.COLUMN_ID, player.getId()), null);

    }

    /**
     * Removes a player from database.
     *
     * @param playerId - the id to identify the player to remove
     */
    public void removePlayer(long playerId) {

        database.delete(DatabaseHelper.GamePlayer.TABLE_NAME,
                DatabaseHelper.where(DatabaseHelper.GamePlayer.COLUMN_PLAYER_ID, playerId), null);

        database.delete(DatabaseHelper.Player.TABLE_NAME,
                DatabaseHelper.where(DatabaseHelper.Player.COLUMN_ID, playerId), null);

    }

    /**
     * Wrapper-Method! Removes a player from database.
     *
     * @param player - the player object instance to remove from database
     */
    public void removePlayer(DatabaseHelper.Player player) {
        removePlayer(player.getId());
    }

    /**
     * Loads the necessary data from all games to display. The data of each game gets
     * stored into a list item object instance and put into a list. A list of all items
     * gets returned.
     *
     * @return returns a list of items containing game data to display for preview
     */
    public ArrayList<DatabaseHelper.Game.ListItem> loadListItems() {
        ArrayList<DatabaseHelper.Game.ListItem> listItems = new ArrayList<>();


        Cursor gameList = database.query(DatabaseHelper.Game.TABLE_NAME,
                DatabaseHelper.Game.ALL_COLUMNS, null, null, null, null, DatabaseHelper.Game.COLUMN_TIMESTAMP + " DESC");
        gameList.moveToFirst(); //important!!! Make sure that a cursor starts at index 0, not -1
        while(!gameList.isAfterLast()) {

            long gameId = gameList.getLong(gameList.getColumnIndex(DatabaseHelper.Game.COLUMN_ID));
            long timestamp = gameList.getLong(gameList.getColumnIndex(DatabaseHelper.Game.COLUMN_TIMESTAMP));
            String title = gameList.getString(gameList.getColumnIndex(DatabaseHelper.Game.COLUMN_TITLE));
            DatabaseHelper.Game.ListItem listItem = new DatabaseHelper.Game.ListItem(gameId);
            listItem.setTimestamp(timestamp);
            listItem.setTitle(title);

            Cursor playerCountCursor = database.query(DatabaseHelper.GamePlayer.TABLE_NAME,
                    new String[]{DatabaseHelper.GamePlayer.COLUMN_GAME_ID},
                    DatabaseHelper.where(DatabaseHelper.GamePlayer.COLUMN_GAME_ID, gameId), null, null, null, null);
            playerCountCursor.moveToFirst(); //important!!! Make sure that a cursor starts at index 0, not -1
            listItem.setPlayerCount(playerCountCursor.getCount());
            playerCountCursor.close();

            listItems.add(listItem);

            gameList.moveToNext();
        }
        gameList.close();
        return listItems;
    }


}
