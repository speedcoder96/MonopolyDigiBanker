package rs.de.monopolydigibanker.database.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 2016 created by Rene
 * Project:    MonopolyDigiBanker
 * Package:    rs.de.monopolydigibanker.database.model
 * Class:      Player
 */
public class Player extends DAO implements Parcelable {

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

    /**
     * Necessary parcelable constructor for reconstructing the object instance
     * @param in - the parcel containing the values for the fields of the instance
     */
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
