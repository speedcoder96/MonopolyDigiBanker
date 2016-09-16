package rs.de.monopolydigibanker.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import rs.de.monopolydigibanker.R;

/**
 * Created by Rene on 13.09.2016.
 */
public class SettingsPreferenceActivity extends PreferenceActivity {

    public static final String SETTING_BALANCE = "preference_default_balance_key";
    public static final String SETTING_GO_MONEY = "preference_go_money_key";
    public static final String SETTING_GO_MONEY_FLAG = "preference_go_flag_key";
    public static final String SETTING_CURRENCY_CHAR = "preference_currency_key";

    public static final String SETTING_LOG_ACTIVATED = "preference_log_flag_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsPreferenceFragment()).commit();
    }


    public static class SettingsPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }


}
