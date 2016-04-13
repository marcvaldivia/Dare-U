package com.dare_u.domain;

import com.dare_u.behaviour.ObjectJSON;
import com.dare_u.constants.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Marc Valdivia, Dare-U 2015.
 */
public class Post implements Serializable, ObjectJSON, Cloneable {

    private String id;
    private int userId;
    private String username;
    private int contactId;
    private String contact;
    private String challenge;
    private char type;
    private String url;
    private Date lastModification;

    /**
     * Empty constructor.
     */
    public Post() {

    }

    /**
     * Constructor with all the fields of a challenge.
     *
     * @param id
     * @param userId
     * @param username
     * @param contactId
     * @param challenge
     * @param type
     * @param url
     */
    public Post(String id, int userId, String username, int contactId, String challenge, char type, String url, Date lastModification) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.contactId = contactId;
        this.challenge = challenge;
        this.type = type;
        this.url = url;
        this.lastModification = lastModification;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getLastModification() {
        return lastModification;
    }

    public void setLastModification(Date lastModification) {
        this.lastModification = lastModification;
    }

    @Override
    public Object toObject(JSONObject json) throws JSONException {
        id = json.getString(Preferences.ID);
        userId = json.getInt(Preferences.USER_ID);
        username = json.getString(Preferences.USERNAME);
        contactId = json.getInt(Preferences.CONTACT_ID);
        contact = json.getString(Preferences.CONTACT);
        challenge = json.getString(Preferences.CHALLENGE);
        type = json.getString(Preferences.TYPE).charAt(0);
        url = json.getString(Preferences.URL);
        return this;
    }

    @Override
    public String toJSON() {
        return null;
    }

    @Override
    public Object cloneObject() throws CloneNotSupportedException {
        return clone();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
