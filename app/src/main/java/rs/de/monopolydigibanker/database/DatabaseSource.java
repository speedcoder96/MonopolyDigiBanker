package rs.de.monopolydigibanker.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import java.util.ArrayList;

import rs.de.monopolydigibanker.R;
import rs.de.monopolydigibanker.activity.SettingsPreferenceActivity;
import rs.de.monopolydigibanker.database.model.Game;
import rs.de.monopolydigibanker.database.model.GamePlayer;
import rs.de.monopolydigibanker.database.model.Log;
import rs.de.monopolydigibanker.database.model.Player;
import rs.de.monopolydigibanker.util.Util;


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
        gameValues.put(Game.COLUMN_TITLE, gameTitle);
        gameValues.put(Game.COLUMN_TIMESTAMP, System.currentTimeMillis());
        gameValues.put(Game.COLUMN_BALANCE_FLAG, 0);
        long gameId = database.insert(Game.TABLE_NAME, null, gameValues);

        /**
         * Stores each player including name, start default balance and their connection to
         * the game they belong to
         */
        for(String playerName : playerNames) {

            ContentValues playerValues = new ContentValues();
            playerValues.put(Player.COLUMN_NAME, playerName);
            playerValues.put(Player.COLUMN_BALANCE,
                    Long.parseLong(preferences.getString(context.getString(R.string.key_preference_default_balance),
                            context.getString(R.string.value_preference_default_balance))));
            long playerId = database.insert(Player.TABLE_NAME, null, playerValues);

            ContentValues gpValues = new ContentValues();
            gpValues.put(GamePlayer.COLUMN_GAME_ID, gameId);
            gpValues.put(GamePlayer.COLUMN_PLAYER_ID, playerId);
            database.insert(GamePlayer.TABLE_NAME, null, gpValues);

        }

        return gameId;
    }

    /**
     * Saves an already created game instance to database.
     * The balance of each player gets updated.
     *
     * @param game - the game instance to save
     */
    public void saveGame(Game game) {
        ArrayList<Player> players = game.getPlayers();
        ArrayList<Log> logs = game.getLogs();

        for(Player player : players) {
            ContentValues playerData = new ContentValues();
            playerData.put(Player.COLUMN_BALANCE, player.getBalance());
            database.update(Player.TABLE_NAME, playerData,
                    DatabaseHelper.where(Player.COLUMN_ID, player.getId()), null);

        }

        for(Log log : logs) {
            if(!log.isRegistered()) {
                ContentValues logData = new ContentValues();
                logData.put(Log.COLUMN_TIMESTAMP, log.getTimestamp());
                logData.put(Log.COLUMN_EVENT_ID, log.getEventId());
                logData.put(Log.COLUMN_GAME_ID, log.getGameId());
                logData.put(Log.COLUMN_FROM_PLAYER_ID, log.getFromPlayerId());
                logData.put(Log.COLUMN_TO_PLAYER_ID, log.getToPlayerId());
                logData.put(Log.COLUMN_EVENT_VALUE, log.getEventValue());

                long logId = database.insert(Log.TABLE_NAME, null, logData);
                log.setId(logId);
            }
        }

        game.setCurrentStateSaved(Game.STATE_SAVED);
    }

    /**
     * Loads an already existing game from database, stores it into a game object
     * instance and returns it.
     *
     * @param gameId - the id to identify the game to load
     * @return the loaded game in a game object instance
     */
    public Game loadGame(long gameId, Context context) {


        Game game = new Game(gameId);


        Cursor gameData = database.query(Game.TABLE_NAME,
                Game.ALL_COLUMNS,
                DatabaseHelper.where(Game.COLUMN_ID, gameId), null, null, null, null);
        gameData.moveToFirst(); //important!!! Make sure that a cursor starts at index 0, not -1


        game.setTimestamp(gameData.getLong(gameData.getColumnIndex(Game.COLUMN_TIMESTAMP)));
        game.setTitle(gameData.getString(gameData.getColumnIndex(Game.COLUMN_TITLE)));
        game.setBalanceFlag(gameData.getLong(gameData.getColumnIndex(Game.COLUMN_BALANCE_FLAG)));
        gameData.close();

        if (Util.isLoggingActivated(context)) {
            Cursor logData = database.query(Log.TABLE_NAME,
                    Log.ALL_COLUMNS,
                    DatabaseHelper.where(Log.COLUMN_GAME_ID, gameId), null, null, null, null);
            logData.moveToFirst();

            while(!logData.isAfterLast()) {
                Log log = new Log(logData.getLong(logData.getColumnIndex(Log.COLUMN_ID)));
                log.setTimestamp(logData.getLong(logData.getColumnIndex(Log.COLUMN_TIMESTAMP)));
                log.setGameId(gameId);
                log.setEventId(logData.getInt(logData.getColumnIndex(Log.COLUMN_EVENT_ID)));
                log.setEventValue(logData.getLong(logData.getColumnIndex(Log.COLUMN_EVENT_VALUE)));

                if(!logData.isNull(logData.getColumnIndex(Log.COLUMN_FROM_PLAYER_ID))) {
                    log.setFromPlayerId(logData.getLong(logData.getColumnIndex(Log.COLUMN_FROM_PLAYER_ID)));
                } else {
                    log.setFromPlayerId(Log.NOT_REGISTERED);
                }

                if(!logData.isNull(logData.getColumnIndex(Log.COLUMN_TO_PLAYER_ID))) {
                    log.setToPlayerId(logData.getLong(logData.getColumnIndex(Log.COLUMN_TO_PLAYER_ID)));
                } else {
                    log.setToPlayerId(Log.NOT_REGISTERED);
                }

                game.addLog(log);
                logData.moveToNext();
            }
            logData.close();
        }

        Cursor playerList = database.query(GamePlayer.TABLE_NAME,
                GamePlayer.ALL_COLUMNS,
                DatabaseHelper.where(GamePlayer.COLUMN_GAME_ID, gameId), null, null, null, null);
        playerList.moveToFirst(); //important!!! Make sure that a cursor starts at index 0, not -1
        while(!playerList.isAfterLast()) {
            long playerId = playerList.getLong(playerList.getColumnIndex(GamePlayer.COLUMN_PLAYER_ID));

            Cursor playerData = database.query(Player.TABLE_NAME,
                    Player.ALL_COLUMNS,
                    DatabaseHelper.where(Player.COLUMN_ID, playerId), null, null, null, null);
            playerData.moveToFirst(); //important!!! Make sure that a cursor starts at index 0, not -1


            Player player = new Player(playerId);
            player.setName(playerData.getString(playerData.getColumnIndex(Player.COLUMN_NAME)));
            player.setBalance(playerData.getLong(playerData.getColumnIndex(Player.COLUMN_BALANCE)));

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
    public void updateGameTitle(Game game) {

        ContentValues gameData = new ContentValues();
        gameData.put(Game.COLUMN_TITLE, game.getTitle());

        database.update(Game.TABLE_NAME, gameData,
                DatabaseHelper.where(Game.COLUMN_ID, game.getId()), null);
    }

    /**
     * Removes a game including the players that belong to it
     * from the database.
     *
     * @param gameId - the id to identify the game to remove
     */
    public void removeGame(long gameId) {


        Cursor playerList = database.query(GamePlayer.TABLE_NAME,
                GamePlayer.ALL_COLUMNS,
                DatabaseHelper.where(GamePlayer.COLUMN_GAME_ID, gameId), null, null, null, null);
        playerList.moveToFirst();


        while(!playerList.isAfterLast()) {
            long playerId = playerList.getLong(playerList.getColumnIndex(GamePlayer.COLUMN_PLAYER_ID));
            database.delete(Player.TABLE_NAME,
                    DatabaseHelper.where(Player.COLUMN_ID, playerId), null);
            playerList.moveToNext();
        }
        playerList.close();


        database.delete(GamePlayer.TABLE_NAME,
                DatabaseHelper.where(GamePlayer.COLUMN_GAME_ID, gameId), null);

        database.delete(Game.TABLE_NAME,
                DatabaseHelper.where(Game.COLUMN_ID, gameId), null);

        database.delete(Log.TABLE_NAME,
                DatabaseHelper.where(Log.COLUMN_GAME_ID, gameId), null);

    }

    /**
     * Removes all logs taken in the game with "gameId".
     * @param gameId - the id to identify the game to remove the logs from
     */
    public void removeLogs(long gameId) {
        database.delete(Log.TABLE_NAME,
                DatabaseHelper.where(Log.COLUMN_GAME_ID, gameId), null);
    }

    /**
     * Updates a player object instance values in database.
     *
     * @param player - the player object to update
     */
    public void updatePlayer(Player player) {

        ContentValues playerData = new ContentValues();
        playerData.put(Player.COLUMN_NAME, player.getName());
        playerData.put(Player.COLUMN_BALANCE, player.getBalance());

        database.update(Player.TABLE_NAME, playerData,
                DatabaseHelper.where(Player.COLUMN_ID, player.getId()), null);

    }

    /**
     * Removes a player from database.
     *
     * @param playerId - the id to identify the player to remove
     */
    public void removePlayer(long playerId) {

        database.delete(GamePlayer.TABLE_NAME,
                DatabaseHelper.where(GamePlayer.COLUMN_PLAYER_ID, playerId), null);

        database.delete(Player.TABLE_NAME,
                DatabaseHelper.where(Player.COLUMN_ID, playerId), null);

    }

    /**
     * Wrapper-Method! Removes a player from database.
     *
     * @param player - the player object instance to remove from database
     */
    public void removePlayer(Player player) {
        removePlayer(player.getId());
    }

    /**
     * Loads the necessary data from all games to display. The data of each game gets
     * stored into a list item object instance and put into a list. A list of all items
     * gets returned.
     *
     * @return returns a list of items containing game data to display for preview
     */
    public ArrayList<Game.ListItem> loadListItems() {
        ArrayList<Game.ListItem> listItems = new ArrayList<>();


        Cursor gameList = database.query(Game.TABLE_NAME,
                Game.ALL_COLUMNS, null, null, null, null, Game.COLUMN_TIMESTAMP + " DESC");
        gameList.moveToFirst(); //important!!! Make sure that a cursor starts at index 0, not -1
        while(!gameList.isAfterLast()) {

            long gameId = gameList.getLong(gameList.getColumnIndex(Game.COLUMN_ID));
            long timestamp = gameList.getLong(gameList.getColumnIndex(Game.COLUMN_TIMESTAMP));
            String title = gameList.getString(gameList.getColumnIndex(Game.COLUMN_TITLE));
            Game.ListItem listItem = new Game.ListItem(gameId);
            listItem.setTimestamp(timestamp);
            listItem.setTitle(title);

            Cursor playerCountCursor = database.query(GamePlayer.TABLE_NAME,
                    new String[]{GamePlayer.COLUMN_GAME_ID},
                    DatabaseHelper.where(GamePlayer.COLUMN_GAME_ID, gameId), null, null, null, null);
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
