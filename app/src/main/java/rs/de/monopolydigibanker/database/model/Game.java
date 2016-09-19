package rs.de.monopolydigibanker.database.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * 2016 created by Rene
 * Project:    MonopolyDigiBanker
 * Package:    rs.de.monopolydigibanker.database.model
 * Class:      Game
 */
public class Game extends DAO implements Parcelable {

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

    /**
     * Necessary parcelable constructor for reconstructing the object instance
     * @param in - the parcel containing the values for the fields of the instance
     */
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

    public boolean hasLogs() {
        return logs.size() > 0;
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
        for(Player player : players) {
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
        for(Player player : players) {
            if(player != currentPlayer) {
                otherPlayersIds[index] = player.getId();
                index++;
            }
        }
        return otherPlayersIds;
    }

    public Player retrievePlayerData(long[] otherPlayersIds, int index) {
        Player selectedPlayer = null;
        for(Player player : players) {
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
