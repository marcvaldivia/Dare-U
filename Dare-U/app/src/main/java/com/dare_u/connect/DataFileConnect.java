package com.dare_u.connect;

import android.os.AsyncTask;

import com.dare_u.behaviour.ConnectFileActivity;
import com.dare_u.behaviour.ObjectJSON;
import com.dare_u.constants.Errors;
import com.dare_u.constants.Preferences;
import com.dare_u.utils.Security;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Marc Valdivia, Dare-U 2015.
 */
public class DataFileConnect extends AsyncTask<String, Integer, String> {

    ConnectFileActivity context;
    ObjectJSON object;
    String url;
    HashMap<String, String> dataPost = new HashMap();
    File file;

    // Internal use
    long totalSize = 0;

    /**
     * Constructor with all the fields of a DataFileConnect.
     *
     * @param context
     * @param object
     * @param url
     * @param dataPost
     * @param file
     */
    public DataFileConnect(ConnectFileActivity context, ObjectJSON object, String url, HashMap<String, String> dataPost, File file) {
        this.context = context;
        this.object = object;
        this.url = url;
        if (dataPost != null) this.dataPost = dataPost;
        this.file = file;
    }

    @Override
    protected String doInBackground(String... params) {
        return uploadFile();
    }

    /**
     * Upload the file to the server and add the parameters.
     *
     * @return String
     */
    @SuppressWarnings("deprecation")
    private String uploadFile() {
        String response = Errors.ERROR_CON;

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                    new AndroidMultiPartEntity.ProgressListener() {
                        @Override
                        public void transferred(long num) {
                            publishProgress((int) ((num / (float) totalSize) * 100));
                        }
                    });

            // Adding file data to http body
            entity.addPart(Preferences.FILE, new FileBody(file));

            // Extra parameters if you want to pass to server
            dataPost = getDataWithAuth(dataPost);
            for(Map.Entry<String, String> entry : dataPost.entrySet()){
                entity.addPart(entry.getKey(), new StringBody(entry.getValue()));
            }

            totalSize = entity.getContentLength();
            httpPost.setEntity(entity);

            // Making server call
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();

            response = EntityUtils.toString(httpEntity);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    protected void onPreExecute() {
        context.onPreLoad();
    }

    protected void onProgressUpdate(Integer... progress) {
        context.onUpdate(progress[0]);
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
    private HashMap<String, String> getDataWithAuth(HashMap<String, String> dataPost) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date actDate = new Date();
        String date = dateFormat.format(actDate);
        Security sec = new Security();
        String auth = sec.createCredentials(actDate);
        dataPost.put(Preferences.DATE, date);
        dataPost.put(Preferences.AUTH, auth);
        return dataPost;
    }

}