package rs.de.monopolydigibanker.database.model;

/**
 * 2016 created by Rene
 * Project:    MonopolyDigiBanker
 * Package:    rs.de.monopolydigibanker.database.model
 * Class:      PlayerStreet
 */
public final class PlayerStreet {

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PLAYER_ID = "player_id";
    public static final String COLUMN_STREET_ID = "street_id";
    public static final String COLUMN_GROUP_ID = "group_id";
    public static final String COLUMN_HOUSE_COUNT = "house_count";

    public static final String[] ALL_COLUMNS = {
            COLUMN_ID, COLUMN_PLAYER_ID, COLUMN_STREET_ID, COLUMN_GROUP_ID, COLUMN_HOUSE_COUNT
    };

    public static final String TABLE_NAME = PlayerStreet.class.getSimpleName();

    public static final String CREATE_TABLE = "CREATE TABLE " + PlayerStreet.class.getSimpleName() + " ( "
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_PLAYER_ID + " INTEGER NOT NULL, "
            + COLUMN_STREET_ID + " INTEGER NOT NULL, "
            + COLUMN_GROUP_ID + " INTEGER NOT NULL, "
            + COLUMN_HOUSE_COUNT + " INTEGER NULL );";

}
