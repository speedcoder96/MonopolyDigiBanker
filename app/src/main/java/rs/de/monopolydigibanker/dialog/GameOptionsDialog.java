package rs.de.monopolydigibanker.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;


/**
 * Created by Rene on 11.09.2016.
 */
public class GameOptionsDialog extends AlertDialog.Builder implements DialogInterface.OnClickListener {

    private Bundle data;

    private OnOptionSelectionListener optionSelectionListener;

    public GameOptionsDialog(Context context, int titleId, int arrayId, Bundle data) {
        super(context);
        this.data = data;

        setCancelable(true);
        setTitle(titleId);
        setItems(arrayId, this);

    }

    public void setOptionSelectionListener(OnOptionSelectionListener optionSelectionListener) {
        this.optionSelectionListener = optionSelectionListener;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(optionSelectionListener != null)
            optionSelectionListener.onSelect(dialog, data, which);
    }

    public static interface OnOptionSelectionListener {
        public void onSelect(DialogInterface dialog, Bundle data, int option);
    }

}
