package rs.de.monopolydigibanker.database.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;

import rs.de.monopolydigibanker.R;
import rs.de.monopolydigibanker.util.Util;

/**
 * 2016 created by Rene
 * Project:    MonopolyDigiBanker
 * Package:    rs.de.monopolydigibanker.database.model
 * Class:      Log
 */
public class Log extends DAO implements Parcelable {

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

    /**
     * Necessary parcelable constructor for reconstructing the object instance
     * @param in - the parcel containing the values for the fields of the instance
     */
    protected Log(Parcel in) {
        super(in.readLong());
        timestamp = in.readLong();
        eventId = in.readInt();
        gameId = in.readLong();
        fromPlayerId = in.readLong();
        toPlayerId = in.readLong();
        eventValue = in.readLong();
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
                            Util.punctuatedBalance(log.getEventValue(),
                                    preferences.getString(
                                            context.getString(R.string.key_preference_currency),
                                            context.getString(R.string.value_preference_currency)))));
                    break;
                case Event.DOUBLE_GO_MONEY_EVENT:
                    logBuilder.append(String.format(
                            context.getString(R.string.game_log_double_go_money_event),
                            playerNames.get(log.getFromPlayerId()).getName(),
                            Util.punctuatedBalance(log.getEventValue(), preferences.getString(
                                    context.getString(R.string.key_preference_currency),
                                    context.getString(R.string.value_preference_currency)))));
                    break;
                case Event.PAY_RENT_EVENT:
                    logBuilder.append(String.format(
                            context.getString(R.string.game_log_pay_rent_event),
                            playerNames.get(log.getFromPlayerId()).getName(),
                            Util.punctuatedBalance(log.getEventValue(), preferences.getString(
                                    context.getString(R.string.key_preference_currency),
                                    context.getString(R.string.value_preference_currency))),
                            playerNames.get(log.getToPlayerId()).getName()));
                    break;
                case Event.SINGLE_TRANSFER_EVENT:
                case Event.MULTIPLE_TRANSFER_EVENT:
                    logBuilder.append(String.format(
                            context.getString(R.string.game_log_transfer_event),
                            playerNames.get(log.getFromPlayerId()).getName(),
                            Util.punctuatedBalance(log.getEventValue(), preferences.getString(
                                    context.getString(R.string.key_preference_currency),
                                    context.getString(R.string.value_preference_currency))),
                            playerNames.get(log.getToPlayerId()).getName()));
                    break;
                case Event.MANAGE_ADD_MONEY_EVENT:
                    logBuilder.append(String.format(
                            context.getString(R.string.game_log_manage_add_money_event),
                            playerNames.get(log.getFromPlayerId()).getName(),
                            Util.punctuatedBalance(log.getEventValue(), preferences.getString(
                                    context.getString(R.string.key_preference_currency),
                                    context.getString(R.string.value_preference_currency)))));
                    break;
                case Event.MANAGE_SUBTRACT_MONEY_EVENT:
                    logBuilder.append(String.format(
                            context.getString(R.string.game_log_manage_subtract_money_event),
                            playerNames.get(log.getFromPlayerId()).getName(),
                            Util.punctuatedBalance(log.getEventValue(), preferences.getString(
                                    context.getString(R.string.key_preference_currency),
                                    context.getString(R.string.value_preference_currency)))));
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

    /**
     *
     */


}
