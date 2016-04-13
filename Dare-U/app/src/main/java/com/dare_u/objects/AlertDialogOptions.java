package com.dare_u.objects;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.dare_u.behaviour.OptionsResponsive;

/**
 * Created by Marc Valdivia, Dare-U 2015.
 */
public class AlertDialogOptions {

    OptionsResponsive activity;

    /**
     * Constructor with all the parameters.
     *
     * @param context
     * @param title
     * @param message
     * @param positive
     * @param negative
     */
    public AlertDialogOptions (Context context, String title, String message, String positive, String negative) {
        this(context, (OptionsResponsive) context, title, message, positive, negative);
    }

    /**
     * Constructor with all the parameters.
     *
     * @param context
     * @param title
     * @param message
     * @param positive
     * @param negative
     */
    public AlertDialogOptions (Context context, OptionsResponsive optionsResponsive, String title, String message, String positive, String negative) {
        activity = optionsResponsive;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setTitle(title)
                .setCancelable(false)
                .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.clickResponse(true);
                    }
                })
                .setNegativeButton(negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity.clickResponse(false);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
