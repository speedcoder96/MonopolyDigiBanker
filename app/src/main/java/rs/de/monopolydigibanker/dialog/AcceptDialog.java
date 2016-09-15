package rs.de.monopolydigibanker.dialog;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.SparseArray;


/**
 * 2016 created by Rene
 * Project:    MonopolyDigiBanker
 * Package:    rs.de.monopolydigibanker.dialog
 * Class:      AcceptDialog
 */
public class AcceptDialog implements DialogInterface.OnClickListener {

    private OnAcceptDialogListener acceptDialogListener;
    private OnUpdateCallback updateCallback;

    private AcceptDialogInterface acceptDialogInterface;

    private AlertDialog.Builder acceptDialogBuilder;
    private Context context;

    private SparseArray<Object> dataContainer;


    public AcceptDialog(Context context) {
        this.context = context;
        acceptDialogBuilder = new AlertDialog.Builder(context);
        acceptDialogBuilder.setCancelable(false);

        dataContainer = new SparseArray<>();
    }

    public void putData(int key, Object data) {
        dataContainer.put(key, data);
    }

    public void setFormattedTitle(int titleId, Object... arguments) {
        acceptDialogBuilder.setTitle(String.format(context.getString(titleId), arguments));
    }

    public void setTitle(int titleId) {
        acceptDialogBuilder.setTitle(titleId);
    }

    public void setFormattedMessage(int messageId, Object... arguments) {
        acceptDialogBuilder.setMessage(String.format(context.getString(messageId), arguments));
    }

    public void setMessage(int messageId) {
        acceptDialogBuilder.setMessage(messageId);
    }

    public void setPositiveButton(int titleId) {
        acceptDialogBuilder.setPositiveButton(titleId, this);
    }

    public void setNegativeButton(int titleId) {
        acceptDialogBuilder.setNegativeButton(titleId, this);
    }

    public void setAcceptDialogListener(OnAcceptDialogListener acceptDialogListener) {
        this.acceptDialogListener = acceptDialogListener;
    }

    public void setUpdateCallback(OnUpdateCallback updateCallback) {
        this.updateCallback = updateCallback;
    }

    public void show() {
        AlertDialog dialog = acceptDialogBuilder.show();
        acceptDialogInterface = new AcceptDialogInterface(dialog, updateCallback, dataContainer);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(acceptDialogListener != null) {
            switch(which) {
                case DialogInterface.BUTTON_POSITIVE:
                    acceptDialogListener.onPositive(acceptDialogInterface);
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    acceptDialogListener.onNegative(acceptDialogInterface);
                    break;
            }
        }
    }

    public static interface OnAcceptDialogListener {

        public void onPositive(AcceptDialogInterface acceptDialogInterface);
        public void onNegative(AcceptDialogInterface acceptDialogInterface);

    }

    public static interface OnUpdateCallback {

        public void onUpdate();

    }

    public static class AcceptDialogInterface {

        private AlertDialog dialog;
        private SparseArray<Object> dataContainer;

        private OnUpdateCallback updateCallback;

        private AcceptDialogInterface(AlertDialog dialog, OnUpdateCallback updateCallback, SparseArray<Object> dataContainer) {
            this.dialog = dialog;
            this.updateCallback = updateCallback;

            this.dataContainer = dataContainer;
        }

        public <T> T get(int key, Class<T> type) {
            return type.cast(dataContainer.get(key));
        }

        public void dismiss() {
            dialog.dismiss();
        }

        public void update() {
            if(updateCallback != null)
                updateCallback.onUpdate();
        }
    }

}
