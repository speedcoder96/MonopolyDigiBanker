package rs.de.monopolydigibanker.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Rene on 08.09.2016.
 */
public class DatabaseSource {

    private static DatabaseSource instance;

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    private Context context;

    private DatabaseSource(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public static DatabaseSource getInstance(Context context) {
        if(instance == null) {
            instance = new DatabaseSource(context);
        }
        return instance;
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        if(database != null) {
            database.close();
        }
    }

    public long storeGame(String gameTitle, ArrayList<String> playerNames) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        /**
         * Stores the games title, timestamp and balance flag at the moment
         * of creation.
         */
        ContentValues gameValues = new ContentValues();
        gameValues.put(DatabaseHelper.Game.COLUMN_TITLE, gameTitle);
        gameValues.put(DatabaseHelper.Game.COLUMN_TIMESTAMP, System.currentTimeMillis());
        gameValues.put(DatabaseHelper.Game.COLUMN_BALANCE_FLAG, 0);
        long gameId = database.insert(DatabaseHelper.Game.TABLE_NAME, null, gameValues);

        for(String playerName : playerNames) {

            /**
             * Stores a players name and balance at the moment of creation.
             */

            ContentValues playerValues = new ContentValues();
            playerValues.put(DatabaseHelper.Player.COLUMN_NAME, playerName);
            playerValues.put(DatabaseHelper.Player.COLUMN_BALANCE,
                    Long.parseLong(preferences.getString("preference_default_balance_key", "15000000")));
            long playerId = database.insert(DatabaseHelper.Player.TABLE_NAME, null, playerValues);

            /**
             * Stores the connection between player and game. The player with "playerId" belongs
             * to a certain game with "gameId"
             */
            ContentValues gpValues = new ContentValues();
            gpValues.put(DatabaseHelper.GamePlayer.COLUMN_GAME_ID, gameId);
            gpValues.put(DatabaseHelper.GamePlayer.COLUMN_PLAYER_ID, playerId);
            database.insert(DatabaseHelper.GamePlayer.TABLE_NAME, null, gpValues);
        }

        /**
         * Returns the gameId of the game that was created
         */
        return gameId;
    }

    public void saveGame(DatabaseHelper.Game game) {

        /**
         * Updates the balance of each player of the game that is passed in database
         */
        ArrayList<DatabaseHelper.Player> players = game.getPlayers();
        for(DatabaseHelper.Player player : players) {
            ContentValues playerData = new ContentValues();
            playerData.put(DatabaseHelper.Player.COLUMN_BALANCE, player.getBalance());
            database.update(DatabaseHelper.Player.TABLE_NAME, playerData,
                    DatabaseHelper.where(DatabaseHelper.Player.COLUMN_ID, player.getId()), null);
        }

        /**
         * Makes sure that the current game is marked as saved
         */
        game.setCurrentStateSaved(DatabaseHelper.Game.STATE_SAVED);

    }

    public DatabaseHelper.Game loadGame(long gameId) {

        DatabaseHelper.Game game = new DatabaseHelper.Game(gameId);

        /**
         * Loads the game data of game with "gameId" from the database
         */
        Cursor gameData = database.query(DatabaseHelper.Game.TABLE_NAME,
                DatabaseHelper.Game.ALL_COLUMNS,
                DatabaseHelper.where(DatabaseHelper.Game.COLUMN_ID, gameId), null, null, null, null);
        gameData.moveToFirst(); //important!!! Make sure that a cursor starts at index 0, not -1

        /**
         * Stores the game data from database into the game DAO ("Game") instance
         */
        game.setTimestamp(gameData.getLong(gameData.getColumnIndex(DatabaseHelper.Game.COLUMN_TIMESTAMP)));
        game.setTitle(gameData.getString(gameData.getColumnIndex(DatabaseHelper.Game.COLUMN_TITLE)));
        game.setBalanceFlag(gameData.getLong(gameData.getColumnIndex(DatabaseHelper.Game.COLUMN_BALANCE_FLAG)));
        gameData.close();

        /**
         * Loads all players with "playerId" that belongs to the game with "gameId"
         */
        Cursor playerList = database.query(DatabaseHelper.GamePlayer.TABLE_NAME,
                DatabaseHelper.GamePlayer.ALL_COLUMNS,
                DatabaseHelper.where(DatabaseHelper.GamePlayer.COLUMN_GAME_ID, gameId), null, null, null, null);
        playerList.moveToFirst(); //important!!! Make sure that a cursor starts at index 0, not -1
        while(!playerList.isAfterLast()) {
            long playerId = playerList.getLong(playerList.getColumnIndex(DatabaseHelper.GamePlayer.COLUMN_PLAYER_ID));

            /**
             * Loads the data of player with "playerId" from the database
             */
            Cursor playerData = database.query(DatabaseHelper.Player.TABLE_NAME,
                    DatabaseHelper.Player.ALL_COLUMNS,
                    DatabaseHelper.where(DatabaseHelper.Player.COLUMN_ID, playerId), null, null, null, null);
            playerData.moveToFirst(); //important!!! Make sure that a cursor starts at index 0, not -1

            /**
             * Stores the player data from database into the player DAO ("Player") instance
             */
            DatabaseHelper.Player player = new DatabaseHelper.Player(playerId);
            player.setName(playerData.getString(playerData.getColumnIndex(DatabaseHelper.Player.COLUMN_NAME)));
            player.setBalance(playerData.getLong(playerData.getColumnIndex(DatabaseHelper.Player.COLUMN_BALANCE)));

            /**
             * Adds a player DAO ("Player") to the game DAO ("Game")
             */
            game.addPlayer(player);

            playerList.moveToNext();
            playerData.close();
        }
        playerList.close();
        return game;
    }

    public void removeGame(long gameId) {

        /**
         * Loads all players with "playerId" that belongs to the game with "gameId"
         */
        Cursor playerList = database.query(DatabaseHelper.GamePlayer.TABLE_NAME,
                DatabaseHelper.GamePlayer.ALL_COLUMNS,
                DatabaseHelper.where(DatabaseHelper.GamePlayer.COLUMN_GAME_ID, gameId), null, null, null, null);
        playerList.moveToFirst();

        /**
         * Deletes all players that are associated with the game with "gameId"
         */
        while(!playerList.isAfterLast()) {
            long playerId = playerList.getLong(playerList.getColumnIndex(DatabaseHelper.GamePlayer.COLUMN_PLAYER_ID));
            database.delete(DatabaseHelper.Player.TABLE_NAME,
                    DatabaseHelper.where(DatabaseHelper.Player.COLUMN_ID, playerId), null);
            playerList.moveToNext();
        }
        playerList.close();

        /**
         * Deletes all player connections to the game with "gameId"
         */
        database.delete(DatabaseHelper.GamePlayer.TABLE_NAME,
                DatabaseHelper.where(DatabaseHelper.GamePlayer.COLUMN_GAME_ID, gameId), null);


        /**
         * Deletes the game with "gameId"
         */
        database.delete(DatabaseHelper.Game.TABLE_NAME,
                DatabaseHelper.where(DatabaseHelper.Game.COLUMN_ID, gameId), null);

    }

    public ArrayList<DatabaseHelper.Game.ListItem> loadListItems() {
        ArrayList<DatabaseHelper.Game.ListItem> listItems = new ArrayList<>();

        /**
         * Loads all games stored in the database
         */
        Cursor gameList = database.query(DatabaseHelper.Game.TABLE_NAME,
                DatabaseHelper.Game.ALL_COLUMNS, null, null, null, null, DatabaseHelper.Game.COLUMN_TIMESTAMP + " DESC");
        gameList.moveToFirst(); //important!!! Make sure that a cursor starts at index 0, not -1
        while(!gameList.isAfterLast()) {

            /**
             * Stores the game data from database into list item DAO ("ListItem")
             */
            long gameId = gameList.getLong(gameList.getColumnIndex(DatabaseHelper.Game.COLUMN_ID));
            long timestamp = gameList.getLong(gameList.getColumnIndex(DatabaseHelper.Game.COLUMN_TIMESTAMP));
            String title = gameList.getString(gameList.getColumnIndex(DatabaseHelper.Game.COLUMN_TITLE));
            DatabaseHelper.Game.ListItem listItem = new DatabaseHelper.Game.ListItem(gameId);
            listItem.setTimestamp(timestamp);
            listItem.setTitle(title);

            /**
             * Loads the number of players associated with the game with "gameId"
             */
            Cursor playerCountCursor = database.query(DatabaseHelper.GamePlayer.TABLE_NAME,
                    new String[]{DatabaseHelper.GamePlayer.COLUMN_GAME_ID},
                    DatabaseHelper.where(DatabaseHelper.GamePlayer.COLUMN_GAME_ID, gameId), null, null, null, null);
            playerCountCursor.moveToFirst(); //important!!! Make sure that a cursor starts at index 0, not -1
            listItem.setPlayerCount(playerCountCursor.getCount());
            playerCountCursor.close();

            /**
             * Adds a list item DAO ("ListItem") to the list of list items
             */
            listItems.add(listItem);

            gameList.moveToNext();
        }
        gameList.close();
        return listItems;
    }

}
