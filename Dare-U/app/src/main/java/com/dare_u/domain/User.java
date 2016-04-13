package com.dare_u.domain;

import android.media.Image;

import com.dare_u.behaviour.ObjectJSON;
import com.dare_u.constants.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Marc Valdivia, Dare-U 2015.
 */
public class User implements Serializable, ObjectJSON, Cloneable {

    private int id;
    private String username;
    private String password;
    private Image image;
    private String email;
    private int birthYear;
    private int restrictions;
    private String country;
    private String language;
    private String device;
    private String IMEI;
    private String notification;
    private Timestamp createdAt;

    // Transient
    private ArrayList<Contact> contacts = new ArrayList();
    private Date lastUpdate;

    /**
     * Empty constructor.
     */
    public User() {
        this.username = null;
    }

    /**
     * Basic user profile constructor.
     */
    public User(int id, String username, String password, String email, int birthYear, int restrictions, String language, String device) {
        this(id, username, password, null, email, birthYear, restrictions, language, device, null, null, null, null);
    }

    /**
     * Constructor with all the fields of an user except the image.
     *
     * @param id
     * @param username
     * @param password
     * @param email
     * @param birthYear
     * @param restrictions
     * @param country
     * @param language
     * @param device
     * @param IMEI
     * @param notification
     * @param createdAt
     */
    public User(int id, String username, String password, String email, int birthYear, int restrictions, String country, String language, String device, String IMEI, String notification, Timestamp createdAt) {
        this(id, username, password, null, email, birthYear, restrictions, language, device, null, null, null, null);
    }

    /**
     * Constructor with all the fields of a user.
     *
     * @param id
     * @param username
     * @param password
     * @param image
     * @param email
     * @param birthYear
     * @param restrictions
     * @param country
     * @param language
     * @param device
     * @param IMEI
     * @param notification
     * @param createdAt
     */
    public User(int id, String username, String password, Image image, String email, int birthYear, int restrictions, String country, String language, String device, String IMEI, String notification, Timestamp createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.image = image;
        this.email = email;
        this.birthYear = birthYear;
        this.restrictions = restrictions;
        this.country = country;
        this.language = language;
        this.device = device;
        this.IMEI = IMEI;
        this.notification = notification;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public int getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(int restrictions) {
        this.restrictions = restrictions;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public ArrayList<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public Object toObject(JSONObject json) throws JSONException {
        id = json.getInt(Preferences.ID);
        username = json.getString(Preferences.USERNAME);
        password = json.getString(Preferences.PASSWORD);
        email = json.getString(Preferences.EMAIL);
        birthYear = json.getInt(Preferences.BIRTHYEAR);
        restrictions = json.getInt(Preferences.RESTRICTIONS);
        country = json.getString(Preferences.COUNTRY);
        language = json.getString(Preferences.LANGUAGE);
        device = json.getString(Preferences.DEVICE);
        IMEI = json.getString(Preferences.IMEI);
        notification = json.getString(Preferences.NOTIFICATION);
        lastUpdate = null;
        return this;
    }

    @Override
    public String toJSON() {
        String json = "{";
        json += "\"" + Preferences.ID + "\":\"" + this.id + "\",";
        json += "\"" + Preferences.USERNAME + "\":\"" + this.username + "\",";
        json += "\"" + Preferences.PASSWORD + "\":\"" + this.password + "\",";
        json += "\"" + Preferences.EMAIL + "\":\"" + this.email + "\",";
        json += "\"" + Preferences.BIRTHYEAR + "\":\"" + this.birthYear + "\",";
        json += "\"" + Preferences.RESTRICTIONS + "\":\"" + this.restrictions + "\",";
        json += "\"" + Preferences.COUNTRY + "\":\"" + this.country + "\",";
        json += "\"" + Preferences.LANGUAGE + "\":\"" + this.language + "\",";
        json += "\"" + Preferences.DEVICE + "\":\"" + this.device + "\",";
        json += "\"" + Preferences.IMEI + "\":\"" + this.IMEI + "\",";
        json += "\"" + Preferences.NOTIFICATION + "\":\"" + this.notification + "\"}";
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
}
