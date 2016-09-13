package rs.de.monopolydigibanker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Rene on 08.09.2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "buchapp.db";
    private static final int DATABASE_VERSION = 1;

    public static abstract class DAO {

        protected long id;

        public DAO(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }

    }

    public static class Game extends DAO implements Parcelable {

        public static final byte STATE_SAVED = 0;
        public static final byte STATE_UNSAVED = 1;

        public static final String TABLE_NAME = Game.class.getSimpleName();
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_BALANCE_FLAG = "balanceflag";
        public static final String[] ALL_COLUMNS = {
                COLUMN_ID, COLUMN_TIMESTAMP, COLUMN_TITLE, COLUMN_BALANCE_FLAG
        };

        public static final String CREATE_TABLE = "CREATE TABLE " + Game.class.getSimpleName() + " ( "
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TIMESTAMP + " INTEGER NOT NULL, "
                + COLUMN_TITLE + " TEXT NOT NULL, "
                + COLUMN_BALANCE_FLAG + " INTEGER NULL );";


        private String title;
        private long timestamp;
        private ArrayList<Player> players;
        private long balanceFlag;

        private byte currentStateSaved;

        public Game(long id) {
            super(id);
            players = new ArrayList<Player>();
        }

        public Game(Parcel in) {
            super(in.readLong());
            title = in.readString();
            timestamp = in.readLong();
            players = in.createTypedArrayList(Player.CREATOR);
            balanceFlag = in.readLong();

            currentStateSaved = in.readByte();
        }

        public static final Creator<Game> CREATOR = new Creator<Game>() {
            @Override
            public Game createFromParcel(Parcel in) {
                return new Game(in);
            }

            @Override
            public Game[] newArray(int size) {
                return new Game[size];
            }
        };

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public long getBalanceFlag() {
            return balanceFlag;
        }

        public void setBalanceFlag(long balanceFlag) {
            this.balanceFlag = balanceFlag;
        }

        public ArrayList<Player> getPlayers() {
            return players;
        }

        public void addPlayer(Player player) {
            players.add(player);
        }

        public boolean isCurrentStateSaved() {
            return currentStateSaved == STATE_SAVED;
        }

        public void setCurrentStateSaved(byte currentStateSaved) {
            this.currentStateSaved = currentStateSaved;
        }

        public String[] retrieveOtherPlayersNames(Player currentPlayer) {
            String[] otherPlayersNames = new String[(players.size() - 1)];
            int index = 0;
            for(DatabaseHelper.Player player : players) {
                if(player != currentPlayer) {
                    otherPlayersNames[index] = player.getName();
                    index++;
                }
            }
            return otherPlayersNames;
        }

        public long[] retrieveOtherPlayersIds(Player currentPlayer) {
            long[] otherPlayersIds = new long[(players.size() - 1)];
            int index = 0;
            for(DatabaseHelper.Player player : players) {
                if(player != currentPlayer) {
                    otherPlayersIds[index] = player.getId();
                    index++;
                }
            }
            return otherPlayersIds;
        }

        public Player retrievePlayerData(long[] otherPlayersIds, int index) {
            DatabaseHelper.Player selectedPlayer = null;
            for(DatabaseHelper.Player player : players) {
                if(player.getId() == otherPlayersIds[index]) {
                    selectedPlayer = player;
                    break;
                }
            }
            return selectedPlayer;
        }

        @Override
        public int describeContents() {
            return 6;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(id);
            dest.writeString(title);
            dest.writeLong(timestamp);
            dest.writeTypedList(players);
            dest.writeLong(balanceFlag);

            dest.writeByte(currentStateSaved);
        }

        public static class ListItem extends DAO {

            private long timestamp;
            private String title;
            private int playerCount;

            public ListItem(long id) {
                super(id);
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public void setPlayerCount(int playerCount) {
                this.playerCount = playerCount;
            }

            public String getTitle() {
                return title;
            }

            public int getPlayerCount() {
                return playerCount;
            }

            public long getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(long timestamp) {
                this.timestamp = timestamp;
            }
        }


    }

    public static class GamePlayer extends DAO {
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

        private long gameId;
        private long playerId;

        public GamePlayer(long id) {
            super(id);
        }

        public long getGameId() {
            return gameId;
        }

        public void setGameId(long gameId) {
            this.gameId = gameId;
        }

        public long getPlayerId() {
            return playerId;
        }

        public void setPlayerId(long playerId) {
            this.playerId = playerId;
        }
    }

    public static class Player extends DAO implements Parcelable {
        public static final String TABLE_NAME = Player.class.getSimpleName();
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_BALANCE = "balance";
        public static final String[] ALL_COLUMNS = {
                COLUMN_ID, COLUMN_NAME, COLUMN_BALANCE
        };

        public static final String CREATE_TABLE = "CREATE TABLE " + Player.class.getSimpleName() + " ( "
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_BALANCE + " INTEGER NOT NULL );";

        private String name;
        private long balance;

        public Player(long id) {
            super(id);
        }

        protected Player(Parcel in) {
            super(in.readLong());
            name = in.readString();
            balance = in.readLong();
        }

        public static final Creator<Player> CREATOR = new Creator<Player>() {
            @Override
            public Player createFromParcel(Parcel in) {
                return new Player(in);
            }

            @Override
            public Player[] newArray(int size) {
                return new Player[size];
            }
        };

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getBalance() {
            return balance;
        }

        public long subtractBalance(long change) {
            balance -= change;
            return change;
        }

        public long addBalance(long change) {
            balance += change;
            return change;
        }

        public void setBalance(long balance) {
            this.balance = balance;
        }

        @Override
        public int describeContents() {
            return 3;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(id);
            dest.writeString(name);
            dest.writeLong(balance);
        }
    }

    public static class Event extends DAO {
        public static final String TABLE_NAME = Event.class.getSimpleName();
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String[] ALL_COLUMNS = {
                COLUMN_ID, COLUMN_NAME
        };

        public static final String CREATE_TABLE = "CREATE TABLE " + Event.class.getSimpleName() + " ( "
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL );";

        private String name;

        public Event(long id) {
            super(id);
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }


    }

    public static class Log extends DAO {
        public static final String TABLE_NAME = Log.class.getSimpleName();
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_EVENT_ID = "event_id";
        public static final String COLUMN_GAME_ID = "game_id";
        public static final String COLUMN_FROM_PLAYER_ID = "from_id";
        public static final String COLUMN_TO_PLAYER_ID = "to_id";
        public static final String[] ALL_COLUMNS = {
            COLUMN_ID, COLUMN_TIMESTAMP, COLUMN_EVENT_ID, COLUMN_GAME_ID, COLUMN_FROM_PLAYER_ID, COLUMN_TO_PLAYER_ID
        };

        public static final String CREATE_TABLE = "CREATE TABLE " + Log.class.getSimpleName() + " ( "
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TIMESTAMP + " INTEGER NOT NULL, "
                + COLUMN_EVENT_ID + " INTEGER NULL, "
                + COLUMN_GAME_ID + " INTEGER NOT NULL, "
                + COLUMN_FROM_PLAYER_ID + " INTEGER NOT NULL, "
                + COLUMN_TO_PLAYER_ID + " INTEGER NOT NULL );";

        private long timestamp;
        private long eventId;
        private long gameId;
        private long fromPlayerId;
        private long toPlayerId;

        public Log(long id) {
            super(id);
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public long getEventId() {
            return eventId;
        }

        public void setEventId(long eventId) {
            this.eventId = eventId;
        }

        public long getGameId() {
            return gameId;
        }

        public void setGameId(long gameId) {
            this.gameId = gameId;
        }

        public long getFromPlayerId() {
            return fromPlayerId;
        }

        public void setFromPlayerId(long fromPlayerId) {
            this.fromPlayerId = fromPlayerId;
        }

        public long getToPlayerId() {
            return toPlayerId;
        }

        public void setToPlayerId(long toPlayerId) {
            this.toPlayerId = toPlayerId;
        }
    }


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Game.CREATE_TABLE);
        db.execSQL(Player.CREATE_TABLE);
        db.execSQL(GamePlayer.CREATE_TABLE);
        db.execSQL(Event.CREATE_TABLE);
        db.execSQL(Log.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + Game.CREATE_TABLE);
        db.execSQL("DROP TABLE " + Player.CREATE_TABLE);
        db.execSQL("DROP TABLE " + GamePlayer.CREATE_TABLE);
        db.execSQL("DROP TABLE " + Event.CREATE_TABLE);
        db.execSQL("DROP TABLE " + Log.CREATE_TABLE);
        onCreate(db);
    }

    public static String where(String columnName, long value) {
        return columnName + " = " + value;
    }

}
