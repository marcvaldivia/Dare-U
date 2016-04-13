package com.dare_u.behaviour;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Marc Valdivia, Dare-U 2015.
 */
public interface ObjectJSON {

    /**
     * Method to convert a JSON string into the referenced Object.
     * Example:
     *      -> The method gets a JSON and converts it to an User object.
     *
     * @param json
     * @return Object
     */
    Object toObject(JSONObject json) throws JSONException;

    /**
     * Create a JSON String from the object where the method is placed.
     *
     * @return String
     */
    String toJSON();

    /**
     * Returns a copy of the object.
     *
     * @return Object
     */
    Object cloneObject() throws CloneNotSupportedException;

}
