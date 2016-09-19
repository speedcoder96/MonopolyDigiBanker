package rs.de.monopolydigibanker.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.EditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import rs.de.monopolydigibanker.R;
import rs.de.monopolydigibanker.activity.SettingsPreferenceActivity;
import rs.de.monopolydigibanker.database.DatabaseHelper;

/**
 * Created by Rene on 05.09.2016.
 */
public class Util {

    public static final int NO_VALID_LONG = -1;

    public static ArrayList<String> toStringValues(ArrayList<EditText> inputs) {
        ArrayList<String> values = new ArrayList<String>();
        for(EditText input : inputs) {
            values.add(input.getText().toString());
        }
        return values;
    }

    public static String convertToDate(long timestamp) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(timestamp);
        return DateFormat.format("dd.MM.yyyy", calendar).toString();
    }

    public static String convertToLogDate(long timestamp) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(timestamp);
        return DateFormat.format("dd.MM.yyyy HH:mm:ss", calendar).toString();
    }

    public static boolean isLoggingActivated(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(
                context.getString(R.string.key_preference_log_activated_flag),
                context.getResources().getBoolean(R.bool.value_preference_log_activated_flag)
        );
    }

    public static String punctuatedBalance(long balance, String currencyCharacter) {
        return new DecimalFormat(",###").format(balance) + currencyCharacter;
    }

    public static long isValidLongType(String value) {
        try {
            return Long.parseLong(value);
        } catch(NumberFormatException e) {
            e.printStackTrace();
            return -1;
        }
    }



}
