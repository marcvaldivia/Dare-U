package com.dare_u.connect;

import android.os.AsyncTask;

import com.dare_u.behaviour.ConnectActivity;
import com.dare_u.behaviour.ObjectJSON;
import com.dare_u.constants.Errors;
import com.dare_u.constants.Preferences;
import com.dare_u.utils.Security;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Marc Valdivia, Dare-U 2015.
 */
public class DataConnect extends AsyncTask<String, Integer, String>{

    ConnectActivity context;
    ObjectJSON object;
    String url;
    HashMap<String, String> dataPost = new HashMap();

    public DataConnect(ConnectActivity context, ObjectJSON object, String url, HashMap<String, String> dataPost) {
        this.context = context;
        this.object = object;
        this.url = url;
        this.dataPost = dataPost;
    }

    @Override
    protected String doInBackground(String... params) {

        String response = Errors.ERROR_CON;

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            dataPost = getDataWithAuth(dataPost);
            writer.write(getPostDataString(dataPost));

            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                response = "";
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            }
        } catch (java.net.SocketTimeoutException e) {
            e.printStackTrace();
        }  catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    protected void onPreExecute () {
        context.onPreLoad();
    }

    protected void onProgressUpdate(Integer... progress) {
    }

    protected void onPostExecute(String result) {
        Object[] resultArray = new Object[0];
        if (!result.equals(Errors.ERROR_CON)) {
            try {
                JSONArray resultJSON = new JSONArray(result);
                resultArray = new Object[resultJSON.length()];
                for (int i = 0; i < resultJSON.length(); i++) {
                    JSONObject objectJSON = resultJSON.getJSONObject(i);
                    object.toObject(objectJSON);
                    resultArray[i] = object.cloneObject();
                }
            } catch (Exception e) {
                resultArray = new Object[0];
            }
        }
        context.onPostLoad(resultArray, result);
    }

    /**
     * Method to add the auth credentials to the POST.
     *
     * @param dataPost
     * @return HashMap
     */
    private HashMap<String,String> getDataWithAuth(HashMap<String, String> dataPost) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date actDate = new Date();
        String date  = dateFormat.format(actDate);
        Security sec = new Security();
        String auth = sec.createCredentials(actDate);
        dataPost.put(Preferences.DATE, date);
        dataPost.put(Preferences.AUTH, auth);
        return dataPost;
    }

    /**
     * Method to convert a HashMap into a POST String.
     *
     * @param params
     * @return String
     * @throws UnsupportedEncodingException
     */
    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first) first = false;
            else result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

}
