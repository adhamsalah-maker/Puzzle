package fr.ul.puzzle.view;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class AideUtils {

    public static void afficherAide(Context context, String titre, String message) {
        new AlertDialog.Builder(context)
                .setTitle(titre)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}