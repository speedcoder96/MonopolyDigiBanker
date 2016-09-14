package rs.de.monopolydigibanker.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import rs.de.monopolydigibanker.R;

/**
 * Created by Rene on 11.09.2016.
 */
public class GameRemoveDialog extends AlertDialog implements DialogInterface.OnClickListener  {

    private long gameId;

    private OnRemoveListener removeListener;

    public GameRemoveDialog(Context context, Bundle data) {
        super(context);

        this.gameId = data.getLong("game_id");
        String gameTitle = data.getString("game_title");

        setCancelable(true);
        setTitle(String.format(
                context.getString(R.string.game_list_remove_dialog_title), gameTitle));
        setMessage(String.format(
                context.getString(R.string.game_list_remove_dialog_message), gameTitle));

        setButton(BUTTON_POSITIVE, context.getString(R.string.game_list_remove_dialog_pos_title), this);
        setButton(BUTTON_NEGATIVE, context.getString(R.string.game_list_remove_dialog_neg_title), this);

    }

    public void setRemoveListener(OnRemoveListener removeListener) {
        this.removeListener = removeListener;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which) {
            case DialogInterface.BUTTON_POSITIVE:
                if(removeListener != null)
                    removeListener.onRemove(gameId);
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                break;
        }
        dismiss();
    }

    public static interface OnRemoveListener {

        public void onRemove(long gameId);

    }


}
