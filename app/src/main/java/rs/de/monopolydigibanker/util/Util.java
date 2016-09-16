package rs.de.monopolydigibanker.util;

import android.text.format.DateFormat;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

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
