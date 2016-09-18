package rs.de.monopolydigibanker.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.LinearLayout;



/**
 * Created by Rene on 11.09.2016.
 */
public class OptionsDialog  implements DialogInterface.OnClickListener {

    private Bundle data;

    private AlertDialog.Builder optionsDialogBuilder;
    private LinearLayout linearLayout;

    private OnOptionSelectionListener optionSelectionListener;

    public OptionsDialog(Context context) {
        optionsDialogBuilder = new AlertDialog.Builder(context);
        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        optionsDialogBuilder.setView(linearLayout);
        optionsDialogBuilder.setCancelable(true);
    }

    public void setTitle(int titleId) {
        optionsDialogBuilder.setTitle(titleId);
    }

    public void setItems(int itemsId) {
        optionsDialogBuilder.setItems(itemsId, this);
    }

    public void setData(Bundle data) {
        this.data = data;
    }

    public void show() {
        optionsDialogBuilder.show();
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
