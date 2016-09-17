package rs.de.monopolydigibanker.dialog;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;

import rs.de.monopolydigibanker.database.DatabaseHelper;
import rs.de.monopolydigibanker.util.Util;

/**
 * Created by Rene on 13.09.2016.
 */
public class PayAmountDialog extends AlertDialog.Builder {

    private OnPaymentDoneListener paymentDoneListener;

    private EditText payAmountEditText;

    private DatabaseHelper.Player currentPlayer;
    private ArrayList<DatabaseHelper.Player> targetPlayers;

    private float higherPayFactor;
    private float lowerPayFactor;

    private boolean addAmountToPlayer;

    public PayAmountDialog(Context context) {
        super(context);

        payAmountEditText = new EditText(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        payAmountEditText.setLayoutParams(lp);
        payAmountEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        setView(payAmountEditText);
    }

    public void setPaymentDoneListener(OnPaymentDoneListener paymentDoneListener) {
        this.paymentDoneListener = paymentDoneListener;
    }

    public void setCurrentPlayer(DatabaseHelper.Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setAddAmountToPlayer(boolean addAmountToPlayer) {
        this.addAmountToPlayer = addAmountToPlayer;
    }

    public void setTargetPlayers(ArrayList<DatabaseHelper.Player> targetPlayers) {
        this.targetPlayers = targetPlayers;
    }

    public void setDialogTitle(int titleId) {
        setTitle(titleId);
    }

    public void setDialogTitle(String title) {
        setTitle(title);
    }

    public void setHigherFactorButton(int titleId, float higherPayFactor) {
        this.setNegativeButton(titleId, null);
        this.higherPayFactor = higherPayFactor;
    }

    public void setLowerFactorButton(int titleId, float lowerPayFactor) {
        this.setNeutralButton(titleId, null);
        this.lowerPayFactor = lowerPayFactor;
    }

    public void setSubmitButtonTitle(int titleId) {
        this.setPositiveButton(titleId, null);
    }

    public DatabaseHelper.Player getCurrentPlayer() {
        return currentPlayer;
    }

    public ArrayList<DatabaseHelper.Player> getTargetPlayers() {
        return targetPlayers;
    }

    public boolean shouldAddAmountToPlayer() {
        return addAmountToPlayer;
    }

    public void showDialog() {
        AlertDialog dialog = show();
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(
                new PayFactorListener(dialog, payAmountEditText, lowerPayFactor)
        );
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(
                new PayFactorListener(dialog, payAmountEditText, higherPayFactor)
        );
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                new PaySubmitListener(dialog, payAmountEditText, this)
        );
    }

    private static class PayFactorListener implements View.OnClickListener {

        private AlertDialog payAmountDialog;
        private EditText payAmountEditText;

        private float payFactor;

        public PayFactorListener(AlertDialog dialog, EditText payAmountEditText, float payFactor) {
            this.payAmountDialog = dialog;
            this.payAmountEditText = payAmountEditText;

            this.payFactor = payFactor;
        }

        @Override
        public void onClick(View v) {
            String payAmountInput = payAmountEditText.getText().toString();
            if(payAmountInput.length() > 0) {
                long rentAmount = (long)(Float.parseFloat(payAmountInput) * payFactor);
                payAmountEditText.setText(String.valueOf(rentAmount));

                payAmountDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
                payAmountDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(false);
            }
        }
    }

    private static class PaySubmitListener implements View.OnClickListener {

        private AlertDialog dialog;
        private PayAmountDialog payAmountDialog;

        private EditText payAmountEditText;

        public PaySubmitListener(AlertDialog dialog, EditText payAmountEditText, PayAmountDialog payAmountDialog) {
            this.dialog = dialog;
            this.payAmountDialog = payAmountDialog;

            this.payAmountEditText = payAmountEditText;
        }

        @Override
        public void onClick(View v) {
            pay();
        }

        private void pay() {
            DatabaseHelper.Player currentPlayer = payAmountDialog.getCurrentPlayer();
            ArrayList<DatabaseHelper.Player> targetPlayers = payAmountDialog.getTargetPlayers();
            long payAmount = Util.isValidLongType(payAmountEditText.getText().toString());
            if(payAmount != Util.NO_VALID_LONG) {
                if(targetPlayers != null && targetPlayers.size() > 0) {
                    payAmount *= targetPlayers.size();
                    if(payAmount <= currentPlayer.getBalance()) {
                        for(DatabaseHelper.Player targetPlayer : targetPlayers) {
                            targetPlayer.addBalance(currentPlayer.subtractBalance(payAmount / targetPlayers.size()));
                        }
                        dialog.dismiss();
                        payAmountDialog.onPaymentDoneEvent(currentPlayer, targetPlayers, payAmount / targetPlayers.size());
                        for(DatabaseHelper.Player targetPlayer : targetPlayers) {
                            payAmountDialog.onUpdateEvent(targetPlayer);
                        }
                        payAmountDialog.onUpdateEvent(currentPlayer);
                    } else {
                        clearPayAmountEditText();
                    }
                } else {
                    if(payAmountDialog.shouldAddAmountToPlayer()) {
                        currentPlayer.addBalance(payAmount);
                    } else {
                        currentPlayer.subtractBalance(payAmount);
                    }
                    dialog.dismiss();
                    payAmountDialog.onPaymentDoneEvent(currentPlayer, targetPlayers, payAmount);
                    payAmountDialog.onUpdateEvent(currentPlayer);
                }
            } else {
               clearPayAmountEditText();
            }
        }

        private void clearPayAmountEditText() {
            payAmountEditText.setBackgroundColor(Color.RED);
            payAmountEditText.setText("");
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(true);
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(true);
        }

    }

    public void onUpdateEvent(DatabaseHelper.Player player) {
        if(paymentDoneListener != null) {
            paymentDoneListener.onUpdate(player);
        }
    }

    public void onPaymentDoneEvent(DatabaseHelper.Player player, ArrayList<DatabaseHelper.Player> targetPlayers, long payAmountValue) {
        if(paymentDoneListener != null) {
            paymentDoneListener.onPaymentDone(player, targetPlayers, payAmountValue);
        }
    }


    public static interface OnPaymentDoneListener {

        public void onUpdate(DatabaseHelper.Player player);
        public void onPaymentDone(DatabaseHelper.Player player, ArrayList<DatabaseHelper.Player> targetPlayers, long payAmountValue);

    }




}
