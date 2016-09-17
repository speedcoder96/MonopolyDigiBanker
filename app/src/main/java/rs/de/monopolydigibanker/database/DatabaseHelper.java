package rs.de.monopolydigibanker.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.SparseArray;
import android.util.SparseLongArray;

import java.util.ArrayList;
import java.util.HashMap;

import rs.de.monopolydigibanker.R;
import rs.de.monopolydigibanker.activity.SettingsPreferenceActivity;
import rs.de.monopolydigibanker.util.Util;

/**
 * Created by Rene on 08.09.2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    /**
     *
     */
    private static final String DATABASE_NAME = "buchapp.db";

    /**
     *
     */
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Game.CREATE_TABLE);
        db.execSQL(Player.CREATE_TABLE);
        db.execSQL(GamePlayer.CREATE_TABLE);
        db.execSQL(Log.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + Game.CREATE_TABLE);
        db.execSQL("DROP TABLE " + Player.CREATE_TABLE);
        db.execSQL("DROP TABLE " + GamePlayer.CREATE_TABLE);
        db.execSQL("DROP TABLE " + Log.CREATE_TABLE);
        onCreate(db);
    }

    public static String where(String columnName, long value) {
        return columnName + " = " + value;
    }


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
        private ArrayList<Log> logs;
        private long balanceFlag;

        private byte currentStateSaved;

        public Game(long id) {
            super(id);
            players = new ArrayList<Player>();
            logs = new ArrayList<Log>();
        }

        public Game(Parcel in) {
            super(in.readLong());
            title = in.readString();
            timestamp = in.readLong();
            players = in.createTypedArrayList(Player.CREATOR);
            balanceFlag = in.readLong();
            currentStateSaved = in.readByte();
            logs = in.createTypedArrayList(Log.CREATOR);
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

        public void addLog(Log log) {
            logs.add(log);
        }

        public void newLog(int eventId, long eventValue, long fromPlayerId, long toPlayerId, boolean logActivated) {
            if(logActivated) {
                Log log = new Log(Log.NOT_REGISTERED);
                log.setTimestamp(System.currentTimeMillis());
                log.setGameId(id);
                log.setFromPlayerId(fromPlayerId);
                log.setToPlayerId(toPlayerId);
                log.setEventId(eventId);
                log.setEventValue(eventValue);
                logs.add(log);
            }
        }

        public void newLog(int eventId, long eventValue, long fromPlayerId, boolean logActivated) {
            if(logActivated) {
                Log log = new Log(Log.NOT_REGISTERED);
                log.setTimestamp(System.currentTimeMillis());
                log.setGameId(id);
                log.setFromPlayerId(fromPlayerId);
                log.setEventId(eventId);
                log.setEventValue(eventValue);
                logs.add(log);
            }
        }

        public ArrayList<Log> getLogs() {
            return logs;
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

    public static class Event {

        public static final String GO_MONEY_EVENT = "go_money_event";
        public static final String DOUBLE_GO_MONEY_EVENT = "double_go_money_event";
        public static final String PAY_RENT_EVENT = "pay_rent_event";
        public static final String SINGLE_TRANSFER_EVENT = "single_transfer_event";
        public static final String MULTIPLE_TRANSFER_EVENT = "multiple_transfer_event";
        public static final String MANAGE_ADD_MONEY_EVENT = "manage_add_money_event";
        public static final String MANAGE_SUBTRACT_MONEY_EVENT = "manage_subtract_money_event";

        private static final SparseArray<String> EVENT_IDENTIFIERS =
                new SparseArray<>();

        static {
            EVENT_IDENTIFIERS.put(0, GO_MONEY_EVENT);
            EVENT_IDENTIFIERS.put(1, DOUBLE_GO_MONEY_EVENT);
            EVENT_IDENTIFIERS.put(2, PAY_RENT_EVENT);
            EVENT_IDENTIFIERS.put(3, SINGLE_TRANSFER_EVENT);
            EVENT_IDENTIFIERS.put(4, MULTIPLE_TRANSFER_EVENT);
            EVENT_IDENTIFIERS.put(5, MANAGE_ADD_MONEY_EVENT);
            EVENT_IDENTIFIERS.put(6, MANAGE_SUBTRACT_MONEY_EVENT);
        }

        public static int i(String eventIdentifier) {
            return EVENT_IDENTIFIERS.keyAt(EVENT_IDENTIFIERS.indexOfValue(eventIdentifier));
        }

        public static String n(int eventId) {
            return EVENT_IDENTIFIERS.valueAt(EVENT_IDENTIFIERS.indexOfKey(eventId));
        }
    }

    public static class Log extends DAO implements Parcelable {

        public static final long NOT_REGISTERED = -1;

        public static final String TABLE_NAME = Log.class.getSimpleName();
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_EVENT_ID = "event_id";
        public static final String COLUMN_GAME_ID = "game_id";
        public static final String COLUMN_FROM_PLAYER_ID = "from_id";
        public static final String COLUMN_TO_PLAYER_ID = "to_id";
        public static final String COLUMN_EVENT_VALUE = "event_value";
        public static final String[] ALL_COLUMNS = {
            COLUMN_ID, COLUMN_TIMESTAMP, COLUMN_EVENT_ID, COLUMN_GAME_ID, COLUMN_FROM_PLAYER_ID, COLUMN_TO_PLAYER_ID,
                COLUMN_EVENT_VALUE
        };

        public static final String CREATE_TABLE = "CREATE TABLE " + Log.class.getSimpleName() + " ( "
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TIMESTAMP + " INTEGER NOT NULL, "
                + COLUMN_EVENT_ID + " INTEGER NOT NULL, "
                + COLUMN_GAME_ID + " INTEGER NOT NULL, "
                + COLUMN_FROM_PLAYER_ID + " INTEGER NULL, "
                + COLUMN_TO_PLAYER_ID + " INTEGER NULL, "
                + COLUMN_EVENT_VALUE + " INTEGER NOT NULL );";

        private long timestamp;
        private int eventId;
        private long gameId;
        private long fromPlayerId;
        private long toPlayerId;
        private long eventValue;

        public Log(long id) {
            super(id);
        }

        protected Log(Parcel in) {
            super(in.readLong());
            timestamp = in.readLong();
            eventId = in.readInt();
            gameId = in.readLong();
            fromPlayerId = in.readLong();
            toPlayerId = in.readLong();
            eventValue = in.readLong();
        }

        public static String loadLogs(Game game, Context context) {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

            HashMap<Long, Player> playerNames = new HashMap<>();
            for(Player player : game.getPlayers()) {
                playerNames.put(player.getId(), player);
            }

            StringBuilder logBuilder = new StringBuilder();
            ArrayList<Log> logs = game.getLogs();
            for(int i = logs.size() - 1; i >= 0; i--) {

                Log log = logs.get(i);
                logBuilder.append(Util.convertToLogDate(log.getTimestamp()));
                logBuilder.append(":");
                logBuilder.append(System.getProperty("line.separator"));

                String eventName = Event.n(log.getEventId());
                switch(eventName) {
                    case Event.GO_MONEY_EVENT:
                        logBuilder.append(String.format(
                                context.getString(R.string.game_log_go_money_event),
                                playerNames.get(log.getFromPlayerId()).getName(),
                                Util.punctuatedBalance(log.getEventValue(), preferences.getString(
                                        SettingsPreferenceActivity.SETTING_CURRENCY_CHAR, ""))));
                        break;
                    case Event.DOUBLE_GO_MONEY_EVENT:
                        logBuilder.append(String.format(
                                context.getString(R.string.game_log_double_go_money_event),
                                playerNames.get(log.getFromPlayerId()).getName(),
                                Util.punctuatedBalance(log.getEventValue(), preferences.getString(
                                        SettingsPreferenceActivity.SETTING_CURRENCY_CHAR, ""))));
                        break;
                    case Event.PAY_RENT_EVENT:
                        logBuilder.append(String.format(
                                context.getString(R.string.game_log_pay_rent_event),
                                playerNames.get(log.getFromPlayerId()).getName(),
                                Util.punctuatedBalance(log.getEventValue(), preferences.getString(
                                        SettingsPreferenceActivity.SETTING_CURRENCY_CHAR, "")),
                                playerNames.get(log.getToPlayerId()).getName()));
                        break;
                    case Event.SINGLE_TRANSFER_EVENT:
                    case Event.MULTIPLE_TRANSFER_EVENT:
                        logBuilder.append(String.format(
                                context.getString(R.string.game_log_transfer_event),
                                playerNames.get(log.getFromPlayerId()).getName(),
                                Util.punctuatedBalance(log.getEventValue(), preferences.getString(
                                        SettingsPreferenceActivity.SETTING_CURRENCY_CHAR, "")),
                                playerNames.get(log.getToPlayerId()).getName()));
                        break;
                    case Event.MANAGE_ADD_MONEY_EVENT:
                        logBuilder.append(String.format(
                                context.getString(R.string.game_log_manage_add_money_event),
                                playerNames.get(log.getFromPlayerId()).getName(),
                                Util.punctuatedBalance(log.getEventValue(), preferences.getString(
                                        SettingsPreferenceActivity.SETTING_CURRENCY_CHAR, ""))));
                        break;
                    case Event.MANAGE_SUBTRACT_MONEY_EVENT:
                        logBuilder.append(String.format(
                                context.getString(R.string.game_log_manage_subtract_money_event),
                                playerNames.get(log.getFromPlayerId()).getName(),
                                Util.punctuatedBalance(log.getEventValue(), preferences.getString(
                                        SettingsPreferenceActivity.SETTING_CURRENCY_CHAR, ""))));
                        break;
                }
                logBuilder.append(System.getProperty("line.separator"));
            }
            return logBuilder.toString();
        }

        public void setId(long id) {
            this.id = id;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public int getEventId() {
            return eventId;
        }

        public void setEventId(int eventId) {
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

        public void setEventValue(long eventValue) {
            this.eventValue = eventValue;
        }

        public long getEventValue() {
            return eventValue;
        }

        public boolean isRegistered() {
            return id != NOT_REGISTERED;
        }

        public static final Creator<Log> CREATOR = new Creator<Log>() {
            @Override
            public Log createFromParcel(Parcel in) {
                return new Log(in);
            }

            @Override
            public Log[] newArray(int size) {
                return new Log[size];
            }
        };

        @Override
        public int describeContents() {
            return 6;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(id);
            dest.writeLong(timestamp);
            dest.writeInt(eventId);
            dest.writeLong(gameId);
            dest.writeLong(fromPlayerId);
            dest.writeLong(toPlayerId);
            dest.writeLong(eventValue);
        }


    }




}
