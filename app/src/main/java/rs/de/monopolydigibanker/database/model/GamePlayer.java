package rs.de.monopolydigibanker.database.model;

/**
 * 2016 created by Rene
 * Project:    MonopolyDigiBanker
 * Package:    rs.de.monopolydigibanker.database.model
 * Class:      GamePlayer
 */
public class GamePlayer {

    public static final String TABLE_NAME = GamePlayer.class.getSimpleName();
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_GAME_ID = "game_id";
    public static final String COLUMN_PLAYER_ID = "player_id";
    public static final String[] ALL_COLUMNS = {
            COLUMN_ID, COLUMN_GAME_ID, COLUMN_PLAYER_ID
    };

    public static final String CREATE_TABLE = "CREATE TABLE " + GamePlayer.class.getSimpleName() + " ( "
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_GAME_ID + " INTEGER NOT NULL, "
            + COLUMN_PLAYER_ID + " INTEGER NOT NULL );";

}
