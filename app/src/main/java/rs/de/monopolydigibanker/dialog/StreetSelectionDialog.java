package rs.de.monopolydigibanker.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;

import rs.de.monopolydigibanker.database.model.MonopolyStreet;

/**
 * 2016 created by Rene
 * Project:    MonopolyDigiBanker
 * Package:    rs.de.monopolydigibanker.dialog
 * Class:      StreetSelectionDialog
 */
public class StreetSelectionDialog {

    private AlertDialog.Builder builder;

    public StreetSelectionDialog(Context context) {
        builder = new AlertDialog.Builder(context);
    }

    public void show(ArrayList<MonopolyStreet> streets) {

    }





}
