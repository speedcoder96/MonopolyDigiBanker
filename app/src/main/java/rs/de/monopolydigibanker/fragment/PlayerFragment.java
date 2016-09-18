package rs.de.monopolydigibanker.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import rs.de.monopolydigibanker.R;
import rs.de.monopolydigibanker.database.DatabaseHelper;
import rs.de.monopolydigibanker.database.DatabaseSource;
import rs.de.monopolydigibanker.listener.GoButtonListener;
import rs.de.monopolydigibanker.listener.ManageButtonListener;
import rs.de.monopolydigibanker.listener.RentButtonListener;
import rs.de.monopolydigibanker.listener.TransferButtonListener;
import rs.de.monopolydigibanker.util.Util;

/**
 * Created by Rene on 05.09.2016.
 */
public class PlayerFragment extends Fragment {


    private DatabaseHelper.Game game;
    private DatabaseHelper.Player player;

    private ImageButton goButton;
    private ImageButton rentButton;
    private ImageButton transferButton;
    private ImageButton manageButton;

    private TextView logTextView;

    public static PlayerFragment newInstance(DatabaseHelper.Game game, DatabaseHelper.Player player) {
        Bundle args = new Bundle();
        PlayerFragment fragment = new PlayerFragment();
        args.putParcelable(fragment.getContext().getString(R.string.key_all_game_data), game);
        args.putParcelable(fragment.getContext().getString(R.string.key_all_player_data), player);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        game = getArguments().getParcelable(getContext().getString(R.string.key_all_game_data));
        player = getArguments().getParcelable(getContext().getString(R.string.key_all_player_data));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        View view = inflater.inflate(R.layout.fragment_player, container, false);

        goButton = (ImageButton)view.findViewById(R.id.imagebutton_playerfragment_action_go);
        GoButtonListener goButtonListener = new GoButtonListener(this, game, player);
        goButton.setOnClickListener(goButtonListener);
        goButton.setOnLongClickListener(goButtonListener);

        rentButton = (ImageButton)view.findViewById(R.id.imagebutton_playerfragment_action_rent);
        RentButtonListener rentButtonListener = new RentButtonListener(this, game, player);
        rentButton.setOnClickListener(rentButtonListener);

        transferButton = (ImageButton)view.findViewById(R.id.imagebutton_playerfragment_action_transfer);
        TransferButtonListener transferButtonListener = new TransferButtonListener(this, game, player);
        transferButton.setOnClickListener(transferButtonListener);

        manageButton = (ImageButton)view.findViewById(R.id.imagebutton_playerfragment_action_manage);
        ManageButtonListener manageButtonListener = new ManageButtonListener(this, game, player);
        manageButton.setOnClickListener(manageButtonListener);
        manageButton.setOnLongClickListener(manageButtonListener);

        logTextView = (TextView)view.findViewById(R.id.textview_playerfragment_logging);
        logTextView.setMovementMethod(new ScrollingMovementMethod());

        loadLogs();

        TextView playerNameTextView = (TextView) view.findViewById(R.id.textview_playerfragment_playername);
        playerNameTextView.setText(player.getName());

        TextView balanceTextView = (TextView) view.findViewById(R.id.textview_playerfragment_balance);
        balanceTextView.setText(Util.punctuatedBalance(player.getBalance(),
                preferences.getString(
                        getContext().getString(R.string.key_preference_currency),
                        getContext().getString(R.string.value_preference_currency))));

        return view;
    }

    private void loadLogs() {
        if(Util.isLoggingActivated(getContext())) {
            if(game.hasLogs()) {
                logTextView.setText(DatabaseHelper.Log.loadLogs(game, getContext()));
            } else {
                logTextView.setText(R.string.game_no_log_available);
            }
        } else {
            logTextView.setText(R.string.game_log_disabled);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(!game.isCurrentStateSaved()) {
            DatabaseSource source = DatabaseSource.getInstance(getContext());
            source.open();
            source.saveGame(game);
            source.close();
        }

    }

    public PlayerFragment findFragment(DatabaseHelper.Player targetPlayer) {
        Fragment targetFragment = null;
        for(Fragment fragment : getFragmentManager().getFragments()) {
            DatabaseHelper.Player player = fragment.getArguments().getParcelable(
                    getContext().getString(R.string.key_all_player_data));
            if(player == targetPlayer) {
                targetFragment = fragment;
                break;
            }
        }
        return (PlayerFragment)targetFragment;
    }

    public void updateFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.detach(this);
        transaction.attach(this);
        transaction.commit();
    }

    public void setGoButtonEnabled(boolean enabled) {
        goButton.setEnabled(enabled);
    }

    public void setRentButtonEnabled(boolean enabled) {
        rentButton.setEnabled(enabled);
    }

    public void setTransferButtonEnabled(boolean enabled) {
        transferButton.setEnabled(enabled);
    }

    public void setManageButtonEnabled(boolean enabled) {
        manageButton.setEnabled(enabled);
    }

}
