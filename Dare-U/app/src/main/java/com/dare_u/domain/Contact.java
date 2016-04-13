package com.dare_u.domain;

import com.dare_u.behaviour.CompareObject;
import com.dare_u.behaviour.ObjectJSON;
import com.dare_u.constants.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Marc Valdivia, Dare-U 2015.
 */
public class Contact implements Serializable, ObjectJSON, Cloneable, CompareObject {

    private int contactId;
    private String contact;
    private int restrictions;
    private String device;
    private String notification;
    private boolean favorite;

    /**
     * Empty constructor.
     */
    public Contact() {

    }

    /**
     * Constructor with all the fields of a contact.
     *
     * @param contactId
     * @param contact
     * @param restrictions
     * @param device
     * @param notification
     * @param favorite
     */
    public Contact(int contactId, String contact, int restrictions, String device, String notification, boolean favorite) {
        this.contactId = contactId;
        this.contact = contact;
        this.restrictions = restrictions;
        this.device = device;
        this.notification = notification;
        this.favorite = favorite;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public int getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(int restrictions) {
        this.restrictions = restrictions;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public Object toObject(JSONObject json) throws JSONException {
        contactId = json.getInt(Preferences.ID);
        contact = json.getString(Preferences.USERNAME);
        restrictions = json.getInt(Preferences.RESTRICTIONS);
        device = json.getString(Preferences.DEVICE);
        notification = json.getString(Preferences.NOTIFICATION);
        if (json.has(Preferences.FAVORITE)) {
            favorite = (json.getInt(Preferences.FAVORITE) == 1);
        }
        return this;
    }

    @Override
    public String toJSON() {
        String json = "{";
        json += "\"" + Preferences.ID + "\":\"" + this.contactId + "\",";
        json += "\"" + Preferences.USERNAME + "\":\"" + this.contact + "\",";
        json += "\"" + Preferences.RESTRICTIONS + "\":\"" + this.restrictions + "\",";
        json += "\"" + Preferences.DEVICE + "\":\"" + this.device + "\",";
        json += "\"" + Preferences.NOTIFICATION + "\":\"" + this.notification + "\",";
        if (isFavorite()) {
            json += "\"" + Preferences.FAVORITE + "\":\"1\"}";
        } else {
            json += "\"" + Preferences.FAVORITE + "\":\"0\"}";
        }
        return json;
    }

    @Override
    public Object cloneObject() throws CloneNotSupportedException {
        return clone();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String getCompareObject() {
        return getContact();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Contact) {
            return this.contactId == ((Contact)object).getContactId();
        } else {
            return false;
        }
    }
}
