package rs.de.monopolydigibanker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import rs.de.monopolydigibanker.R;
import rs.de.monopolydigibanker.activity.MainActivity;
import rs.de.monopolydigibanker.database.DatabaseHelper;
import rs.de.monopolydigibanker.database.DatabaseSource;
import rs.de.monopolydigibanker.util.Util;

/**
 * Created by Rene on 05.09.2016.
 */
public class GameListViewAdapter extends BaseAdapter {

    private MainActivity activity;
    private LayoutInflater layoutInflater;

    private DatabaseSource source;

    private ArrayList<DatabaseHelper.Game.ListItem> listItems;

    public GameListViewAdapter(MainActivity activity) {
        this.activity = activity;
        this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        source = DatabaseSource.getInstance(activity);
        loadListItems();
    }

    private void loadListItems() {
        source.open();
        listItems = source.loadListItems();
        source.close();

        if(listItems.size() == 0) {
            activity.onEmptyGameList();
        }
    }

    @Override
    public void notifyDataSetChanged() {
        loadListItems();
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_gamelist, parent, false);
        }

        DatabaseHelper.Game.ListItem listItem = listItems.get(position);

        convertView.setTag(R.id.tag_all_game_id, listItem.getId());
        convertView.setTag(R.id.tag_all_game_title, listItem.getTitle());

        convertView.setOnClickListener(activity);
        convertView.setOnLongClickListener(activity);


        TextView gameTitleTextView = (TextView)convertView.findViewById(R.id.textview_main_game_list_title);
        gameTitleTextView.setText(listItem.getTitle());

        TextView playerCountTextView = (TextView)convertView.findViewById(R.id.textview_main_game_list_playercount);
        playerCountTextView.setText(String.format(activity.getString(R.string.game_list_preview_playercount),
                listItem.getPlayerCount()));

        TextView timestampTextView = (TextView)convertView.findViewById(R.id.textview_main_game_list_timestamp);
        timestampTextView.setText(Util.convertToDate(listItem.getTimestamp()));

        return convertView;
    }



}
