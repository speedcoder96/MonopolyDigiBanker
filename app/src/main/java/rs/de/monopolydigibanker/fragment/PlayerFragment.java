package rs.de.monopolydigibanker.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

    public static final String GAME_DATA_KEY = "gamedata";
    public static final String PLAYER_DATA_KEY = "playerdata";

    private DatabaseHelper.Game game;
    private DatabaseHelper.Player player;

    private ImageButton goButton;
    private ImageButton rentButton;
    private ImageButton transferButton;
    private ImageButton manageButton;

    public static PlayerFragment newInstance(DatabaseHelper.Game game, DatabaseHelper.Player player) {
        Bundle args = new Bundle();
        args.putParcelable(GAME_DATA_KEY, game);
        args.putParcelable(PLAYER_DATA_KEY, player);
        PlayerFragment fragment = new PlayerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        game = getArguments().getParcelable(GAME_DATA_KEY);
        player = getArguments().getParcelable(PLAYER_DATA_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        View view = inflater.inflate(R.layout.player_fragment, container, false);

        goButton = (ImageButton)view.findViewById(R.id.fragment_action_button_go);
        GoButtonListener goButtonListener = new GoButtonListener(this, game, player);
        goButton.setOnClickListener(goButtonListener);
        goButton.setOnLongClickListener(goButtonListener);

        rentButton = (ImageButton)view.findViewById(R.id.fragment_action_button_rent);
        RentButtonListener rentButtonListener = new RentButtonListener(this, game, player);
        rentButton.setOnClickListener(rentButtonListener);

        transferButton = (ImageButton)view.findViewById(R.id.fragment_action_button_transfer);
        TransferButtonListener transferButtonListener = new TransferButtonListener(this, game, player);
        transferButton.setOnClickListener(transferButtonListener);

        manageButton = (ImageButton)view.findViewById(R.id.fragment_action_button_manage);
        ManageButtonListener manageButtonListener = new ManageButtonListener(this, game, player);
        manageButton.setOnClickListener(manageButtonListener);

        TextView playerNameTextView = (TextView) view.findViewById(R.id.player_name_textview);
        playerNameTextView.setText(player.getName());

        TextView balanceTextView = (TextView) view.findViewById(R.id.player_balance_textview);
        balanceTextView.setText(Util.punctuatedBalance(player.getBalance(),
                preferences.getString("preference_currency_key", "")));

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        /**
         * Saves the game to database if the current state isn't saved yet
         */
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
            DatabaseHelper.Player player = fragment.getArguments().getParcelable(PlayerFragment.PLAYER_DATA_KEY);
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

    public ImageButton getGoButton() {
        return goButton;
    }

    public ImageButton getRentButton() {
        return rentButton;
    }

    public ImageButton getTransferButton() {
        return transferButton;
    }

    public ImageButton getManageButton() {
        return manageButton;
    }
}
