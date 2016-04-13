package com.dare_u.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dare_u.constants.Preferences;
import com.dare_u.domain.Contact;
import com.dare_u.domain.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Marc Valdivia, Dare-U 2015.
 */
public class DataAccess {

    /**
     * Method to create a empty file. The file will contain the information of the application.
     *
     * @param context Context of the Activity.
     */
    public static void createEmptyFile(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(Preferences.USER_OBJECT, "").apply();
        sharedPreferences.edit().putString(Preferences.CONTACT_LIST, "").apply();
        /*
        User user = new User();
        try {
            FileOutputStream out = context.openFileOutput("user.dat", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(user);
            oos.close();
            out.close();
        } catch (FileNotFoundException ex) {
            Log.i("e", "Error creating the file user.dat");
        } catch (IOException ex) {
            Log.i("e", "Error saving the empty object");
        }
        */
    }

    /**
     * Returns a boolean to indicate if the user is already registered in the device.
     *
     * @param context
     * @return boolean
     */
    public static boolean isLogIn(Context context) {
        boolean check = false;
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        String sUser = sharedPreferences.getString(Preferences.USER_OBJECT, "");
        if (sUser.isEmpty()) {
            check = false;
        } else {
            try {
                JSONObject jsonObject = new JSONObject(sUser);
                User user = new User();
                user.toObject(jsonObject);
                check = user.getUsername() != null;
            } catch (JSONException e) {
                Log.i("e", "Error reading the file");
                check = false;
            }
        }
        return check;
        /*
        User user;
        boolean check = false;
        try {
            FileInputStream fin = context.openFileInput("user.dat");
            ObjectInputStream ois = new ObjectInputStream(fin);
            user = (User)ois.readObject();
            fin.close();
            ois.close();
            check = user.getUsername() != null;
        } catch (FileNotFoundException ex) {
            Log.i("e", "Error finding the file");
            DataAccess.createEmptyFile(context);
        } catch (IOException ex) {
            Log.i("e", "Error accessing to the file");
        } catch (ClassNotFoundException ex) {
            Log.i("e", "Error reading the file");
        }
        return check;
        */
    }

    /**
     * Method to save the user information in a file.
     * Return a boolean. If it is true the file has been saved successfully.
     *
     * @param user User object with tha data to save in a file.
     * @return boolean
     */
    public static boolean createUser(Context context, User user) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String sUser = user.toJSON();
        try {
            JSONArray jsonArray = new JSONArray();
            for (Contact contact : user.getContacts()) {
                String sContact = contact.toJSON();
                JSONObject jsonObject = new JSONObject(sContact);
                jsonArray.put(jsonObject);
            }
            sharedPreferences.edit().putString(Preferences.CONTACT_LIST, jsonArray.toString()).apply();
        } catch (JSONException e) {
            Log.i("e", "Error saving the user object");
            return false;
        }
        sharedPreferences.edit().putString(Preferences.USER_OBJECT, sUser).apply();
        return true;
        /*
        try {
            String filename = "user.dat";
            FileOutputStream out = context.openFileOutput(filename, Context.MODE_PRIVATE);
            user.setLastUpdate(new Date());
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(user);
            oos.close();
            out.close();
            return true;
        } catch (FileNotFoundException ex) {
            Log.i("e", "Error creating the file user.dat");
            return false;
        } catch (IOException ex) {
            Log.i("e", "Error saving the user object");
            return false;
        }
        */
    }

    /**
     * Method to get the user information from a file.
     *
     * @return boolean
     */
    public static User getUser(Context context) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        String sUser = sharedPreferences.getString(Preferences.USER_OBJECT, "");
        String sContacts = sharedPreferences.getString(Preferences.CONTACT_LIST, "");
        if (!sUser.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(sUser);
                User user = new User();
                user.toObject(jsonObject);
                if (!sContacts.isEmpty()) {
                    JSONArray jsonArray = new JSONArray(sContacts);
                    ArrayList<Contact> contactArrayList = new ArrayList();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        Contact contact = (Contact) new Contact().toObject(object);
                        contactArrayList.add(contact);
                    }
                    user.setContacts(contactArrayList);
                }
                return user;
            } catch (JSONException e) {
                Log.i("e", "Error reading the file");
            }
        }
        /*
        try {
            User user;
            FileInputStream fin = context.openFileInput("user.dat");
            ObjectInputStream ois = new ObjectInputStream(fin);
            user = (User) ois.readObject();
            fin.close();
            ois.close();
            return user;
        } catch (FileNotFoundException ex) {
            Log.i("e", "Error finding the file");
            DataAccess.createEmptyFile(context);
        } catch (IOException ex) {
            Log.i("e", "Error accessing to the file");
        } catch (ClassNotFoundException ex) {
            Log.i("e", "Error reading the file");
        }
        */
        return null;
    }

}
