package com.dare_u.behaviour;

/**
 * Created by Marc Valdivia, Dare-U 2015.
 */
public interface ConnectActivity {

    /**
     * Method to call before execute the background process.
     */
    void onPreLoad();

    /**
     * Method to call after execute the background process.
     * The parameter is the response from the server.
     *
     * @param resultArray
     * @param result
     */
    void onPostLoad(Object[] resultArray, String result);

}
