package rs.de.monopolydigibanker.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import rs.de.monopolydigibanker.R;


/**
 * Created by Rene on 11.09.2016.
 */
public class OptionsDialog  implements DialogInterface.OnClickListener {

    private Bundle data;

    private Context context;
    private AlertDialog.Builder optionsDialogBuilder;
    private LinearLayout linearLayout;

    private OnOptionSelectionListener optionSelectionListener;

    public OptionsDialog(Context context) {
        this.context = context;
        optionsDialogBuilder = new AlertDialog.Builder(context);
        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        optionsDialogBuilder.setView(linearLayout);
        optionsDialogBuilder.setCancelable(true);
    }

    public void setTitle(int titleId) {
        optionsDialogBuilder.setTitle(titleId);
    }

    public void setItems(String[] items) {
        optionsDialogBuilder.setItems(items, this);
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
