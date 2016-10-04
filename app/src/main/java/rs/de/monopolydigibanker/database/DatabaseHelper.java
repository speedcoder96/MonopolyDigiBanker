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
import rs.de.monopolydigibanker.database.model.Game;
import rs.de.monopolydigibanker.database.model.GamePlayer;
import rs.de.monopolydigibanker.database.model.Log;
import rs.de.monopolydigibanker.database.model.Player;
import rs.de.monopolydigibanker.database.model.PlayerStreet;
import rs.de.monopolydigibanker.util.Util;

/**
 * Created by Rene on 08.09.2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    /**
     * Holds the name of the database where all data gets stored
     */
    private static final String DATABASE_NAME = "mdb_database.db";

    /**
     * Holds the version of the database
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructor of DatabaseHelper class
     * @param context - the context of the application
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Game.CREATE_TABLE);
        db.execSQL(Player.CREATE_TABLE);
        db.execSQL(PlayerStreet.CREATE_TABLE);
        db.execSQL(GamePlayer.CREATE_TABLE);
        db.execSQL(Log.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + Game.TABLE_NAME);
        db.execSQL("DROP TABLE " + Player.TABLE_NAME);
        db.execSQL("DROP TABLE " + GamePlayer.TABLE_NAME);
        db.execSQL("DROP TABLE " + Log.TABLE_NAME);
        db.execSQL("DROP TABLE " + PlayerStreet.TABLE_NAME);
        onCreate(db);

    }

    public static String where(String columnName, long value) {
        return columnName + " = " + value;
    }


}
