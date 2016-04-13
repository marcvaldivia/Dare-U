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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by Marc Valdivia, Dare-U 2015.
 */
public class DataFileDownload extends AsyncTask<String, Integer, String> {

    ConnectFileActivity context;
    ObjectJSON object;
    String url;
    HashMap<String, String> dataPost = new HashMap();
    File file;

    /**
     * Constructor with all the fields of a DataFileConnect.
     *
     * @param context
     * @param object
     * @param url
     * @param dataPost
     * @param file
     */
    public DataFileDownload(ConnectFileActivity context, ObjectJSON object, String url, HashMap<String, String> dataPost, File file) {
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

        /*String response = Errors.ERROR_CON;

        byte[] buffer = new byte[1024];

        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        try {
            HttpResponse execute = client.execute(httpPost);
            InputStream content = execute.getEntity().getContent();

            long filesize = execute.getEntity().getContentLength();
            FileOutputStream fileOutput = new FileOutputStream(file);

            int done = 0;
            int len;
            while ((len = content.read(buffer, 0, 1024)) > 0) {
                fileOutput.write(buffer, 0, len);
                done += len;
                publishProgress((int) ((done / (float) filesize) * 100));
                Thread.sleep(10);
            }

            // All is OK
            fileOutput.close();
            response = Errors.ERROR_OK;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;*/

        String response = Errors.ERROR_CON;

        byte[] buffer = new byte[1024];

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
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
                InputStream content = conn.getInputStream();

                FileOutputStream fileOutput = new FileOutputStream(file);
                int fileSize = conn.getContentLength();

                int done = 0;
                int len;
                while ((len = content.read(buffer, 0, 1024)) > 0) {
                    fileOutput.write(buffer, 0, len);
                    done += len;
                    publishProgress((int) ((done / (float) fileSize) * 100));
                    //Thread.sleep(100);
                }

                fileOutput.close();
                response = Errors.ERROR_OK;
            }
        } catch (Exception e) {
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
        context.onPostLoad(null, result);
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