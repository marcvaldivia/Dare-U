package com.dare_u.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.telephony.TelephonyManager;

import com.dare_u.behaviour.CompareObject;
import com.dare_u.constants.Errors;
import com.dare_u.constants.Notifications;
import com.dare_u.dare_u.R;
import com.dare_u.domain.Contact;
import com.dare_u.domain.Header;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Marc Valdivia, Dare-U 2015.
 */
public class General {

    /**
     * Returns the IMEI of the device.
     *
     * @param context
     * @return String
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            return telephonyManager.getDeviceId();
        } else return null;
    }

    /**
     * Returns the CountryCode of the device.
     *
     * @param context
     * @return String
     */
    public static String getCountryCode(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            return telephonyManager.getSimCountryIso();
        } else return null;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     *
     * @return boolean
     */
    public static boolean checkPlayServices(Context context) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        if (apiAvailability == null) return false;
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    /**
     * Show an alert to the user with the title and the content given by the parameters.
     *
     * @param context
     * @param title
     * @param message
     * @param positive
     */
    public static void showAlert(Context context, String title, String message, String positive) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setTitle(title)
                .setCancelable(false)
                .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO: nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Returns the correspondent String to the error given by the parameter.
     * If the parameter has not any error the method returns null.
     *
     * @param context
     * @param result
     * @return String
     */
    public static String attendErrors(Activity context, String result) {
        String error = null;
        switch (result) {
            case Errors.ERROR_GEN:
                error = context.getString(R.string.error_GEN);
                break;
            case Errors.ERROR_CON:
                error = context.getString(R.string.error_CON);
                break;
            case Errors.ERROR_FORM:
                error = context.getString(R.string.error_FORM);
                break;
            case Errors.ERROR_LOGIN:
                error = context.getString(R.string.error_LOGIN);
                break;
            case Errors.ERROR_SIGNUP:
                error = context.getString(R.string.error_SIGNUP);
                break;
            case Errors.ERROR_FILE:
                // Error file
                error = context.getString(R.string.error_FILE);
                break;
            case Errors.ERROR_CONTACT:
                // Error contact
                error = context.getString(R.string.error_CONTACT);
                break;
            case Errors.ERROR_CHALLENGE:
                // Error challenge
                error = context.getString(R.string.error_CHALLENGE);
                break;
        }
        return error;
    }

    /**
     * Returns the correspondent String to the notification message given by the parameter.
     * If the parameter has not any message the method returns null.
     *
     * @param context
     * @param msg
     * @return String
     */
    public static String attendNotifications(Context context, String msg) {
        String message = null;
        switch (msg) {
            case Notifications.NOTIFICATION_CONTACT:
                message = context.getString(R.string.notification_CONTACT);
                break;
            case Notifications.NOTIFICATION_CHALLENGE:
                message = context.getString(R.string.notification_CHALLENGE);
                break;
            case Notifications.NOTIFICATION_ANSWER:
                message = context.getString(R.string.notification_ANSWER);
                break;
        }
        return message;
    }

    /**
     * Method to sort any kind of object that implements 'CompareObject' interface.
     * Example of use: Collections.sort(contacts, General.sorter);
     */
    public static Comparator<CompareObject> sorter = new Comparator<CompareObject>() {

        public int compare(CompareObject object1, CompareObject object2) {

            String sObject1 = object1.getCompareObject();
            String sObject2 = object2.getCompareObject();

            return sObject1.compareTo(sObject2);
        }

    };

    /**
     * Method to sort contacts by the favorite and the username.
     * Example of use: Collections.sort(contacts, General.contactSorter);
     */
    public static Comparator<Contact> contactSorter = new Comparator<Contact>() {

        public int compare(Contact contact1, Contact contact2) {

            if (contact1.isFavorite() && !contact2.isFavorite()) {
                return -1;
            } else if (!contact1.isFavorite() && contact2.isFavorite()) {
                return 1;
            } else {
                return contact1.getContact().compareTo(contact2.getContact());
            }
        }

    };

    /**
     * Returns an int from a boolean.
     * 1 -> true / 0 -> false
     *
     * @param bol
     * @return int
     */
    public static int booleanToInt(boolean bol) {
        if (bol) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Returns a boolean, if the boolean is true the given date is one day before the current date.
     *
     * @param date
     * @return boolean
     */
    public static boolean oneDayBefore(Date date) {
        if (date != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, -1);
            Date oneDayBefore = cal.getTime();
            return date.before(oneDayBefore);
        } else return true;
    }

    /**
     * Returns a Contact object with the id given by the parameter.
     *
     * @param contacts
     * @param id
     * @return Contact
     */
    public static Contact getContactFromList(ArrayList<Contact> contacts, int id) {
        Contact cont = null;
        for (Contact c : contacts) {
            if (c.getContactId() == id) {
               cont = c;
            }
        }
        return cont;
    }

    /**
     * Returns an ArrayList with all the contacts with the headers.
     *
     * @param contacts
     * @return ArrayList<Object>
     */
    public static ArrayList<Object> generateContactList(Context context, ArrayList<Contact> contacts) {
        ArrayList<Object> contactList = new ArrayList();
        for (int i = 0; i < contacts.size(); i++) {
            Contact contact = contacts.get(i);
            if (contact.isFavorite()) {
                if (i == 0) {
                    contactList.add(new Header(context.getString(R.string.listview_favorite)));
                    contactList.add(contact);
                } else {
                    contactList.add(contact);
                }
            } else {
                if (i == 0) {
                    contactList.add(new Header(Character.toString(contact.getContact().charAt(0))));
                    contactList.add(contact);
                } else {
                    if (contact.getContact().charAt(0) == contacts.get(i-1).getContact().charAt(0)) {
                        contactList.add(contact);
                    } else {
                        contactList.add(new Header(Character.toString(contact.getContact().charAt(0))));
                        contactList.add(contact);
                    }
                }
            }
        }
        return contactList;
    }
}
