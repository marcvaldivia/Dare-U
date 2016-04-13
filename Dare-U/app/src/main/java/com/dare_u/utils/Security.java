package com.dare_u.utils;


import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by Marc Valdivia, Dare-U 2015.
 */
public class Security {

    public static final int MOD = 128; // ASCII max value

    /**
     * Empty constructor.
     */
    public Security() {
    }

    /**
     * Validate if the String contains characters with a ASCII value above 128.
     *
     * @param string String to validate
     * @return boolean
     */
    public boolean isValidASCII(String string) {
        if (string.isEmpty() || string == null) return false;
        else {
            boolean isValid = true;
            for (int i = 0; i < string.length(); i++) {
                if ((int) string.charAt(i) > (MOD - 1) || string.charAt(i) == "'".charAt(0))
                    isValid = false;
            }
            return isValid;
        }
    }

    /**
     * Returns a boolean to indicate if the email is valid.
     *
     * @param email
     * @return boolean
     */
    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * Returns an int based in the username.
     *
     * @param username
     * @return int
     */
    public int getAdd(String username) {
        return username.length();
    }

    /**
     * Returns an int based in the username.
     *
     * @param username
     * @return int
     */
    public int getFactor(String username) {
        return 1;
    }

    /**
     * Returns the max birthYear.
     *
     * @return int
     */
    public int getMinYear() {
        return 1900;
    }

    /**
     * Returns the min birthYear.
     *
     * @return int
     */
    public int getMaxYear() {
        return 2003;
    }

    /**
     * Simple encryption by factor and addition (Cesar encryption).
     *
     * @param toEncrypt
     * @param factor
     * @param add
     * @return String
     */
    public String simpleEncryption(String toEncrypt, int factor, int add) {
        String encrypted = "";
        for (int i = 0; i < toEncrypt.length(); i++) {
            int numeric = (((int) toEncrypt.charAt(i)/**factor*/)+add)%MOD;
            encrypted += Character.toString((char) numeric);
        }
        return encrypted;
    }

    /**
     * Simple decryption by factor and addition (Cesar encryption).
     *
     * @param toDecrypt
     * @param factor
     * @param add
     * @return String
     */
    public String simpleDecryption(String toDecrypt, int factor, int add) {
        String decrypted = "";
        for (int i = 0; i < toDecrypt.length(); i++) {
            int numeric = (((int) toDecrypt.charAt(i)/**factor*/)-add)%MOD;
            if (numeric < 0)  numeric = MOD + numeric;
            decrypted += Character.toString((char) numeric);
        }
        return decrypted;
    }

    /**
     * Returns the auth credentials to send to the server.
     *
     * @param date
     * @return String
     */
    public String createCredentials(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        String auth = "";
        for (int i = 0; i < 3; i++) {
            auth += new Random().nextInt(10);
        }
        auth += day+month;
        return simpleEncryption(auth, 0, Math.abs(minute-second));
    }

}
