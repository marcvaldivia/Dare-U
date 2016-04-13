package com.dare_u.dare_u;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dare_u.behaviour.ConnectActivity;
import com.dare_u.connect.DataConnect;
import com.dare_u.constants.Preferences;
import com.dare_u.constants.URLs;
import com.dare_u.gcm.RegistrationIntentService;
import com.dare_u.objects.ButtonU;
import com.dare_u.objects.EditTextU;
import com.dare_u.domain.User;
import com.dare_u.utils.DataAccess;
import com.dare_u.utils.General;
import com.dare_u.utils.Security;

import java.util.HashMap;

public class LogInActivity extends AppCompatActivity implements ConnectActivity {

    Security sec;
    BroadcastReceiver mRegistrationBroadcastReceiver;

    HashMap<String, String> dataPost;

    ButtonU btnLogIn;
    EditTextU etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // Button
        btnLogIn = (ButtonU) findViewById(R.id.btnLogIn);

        // EditText
        etUsername = (EditTextU) findViewById(R.id.etUsername);
        etPassword = (EditTextU) findViewById(R.id.etPassword);

        // OnClickListener
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                if (checkData(username, password)) {
                    dataPost = new HashMap();
                    dataPost.put(Preferences.USERNAME, username);
                    dataPost.put(Preferences.PASSWORD, sec.simpleEncryption(password, 1, sec.getAdd(username)));
                    dataPost.put(Preferences.DEVICE, getString(R.string.app_device));
                    dataPost.put(Preferences.IMEI, General.getIMEI(getApplicationContext()));
                    if (General.checkPlayServices(getApplicationContext())) {
                        Intent intent = new Intent(LogInActivity.this, RegistrationIntentService.class);
                        startService(intent);
                        onPreLoad();
                    } else {
                        dataPost.put(Preferences.NOTIFICATION, "");
                        new DataConnect(LogInActivity.this, new User(), URLs.LOG_IN, dataPost).execute();
                    }
                }
            }
        });

        // Broadcast
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                String sentToken = sharedPreferences
                        .getString(Preferences.SENT_TOKEN_TO_SERVER, "");
                dataPost.put(Preferences.NOTIFICATION, sentToken);
                new DataConnect(LogInActivity.this, new User(), URLs.LOG_IN, dataPost).execute();
            }
        };

        // Init components
        init();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_simple, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Preferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onPreLoad() {
        btnLogIn.setEnabled(false);
        btnLogIn.setText(R.string.app_loading);
    }

    @Override
    public void onPostLoad(Object[] resultArray, String result) {
        btnLogIn.setText(R.string.log_in_button);
        btnLogIn.setEnabled(true);
        String error = General.attendErrors(this, result);
        if (error != null) {
            General.showAlert(this, getString(R.string.title_activity_log_in), error, getString(R.string.error_ok));
        } else if (resultArray.length == 1) {
            User user = (User) resultArray[0];
            DataAccess.createUser(getApplicationContext(), user);
            Intent main = new Intent(this, MainActivity.class);
            startActivity(main);
            finish();
        } else {
            General.showAlert(this, getString(R.string.title_activity_log_in), getString(R.string.error_GEN), getString(R.string.error_ok));
        }
    }

    /**
     * Method to initialize the action bar.
     */
    private void init() {
        // Action Bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // Security
        sec = new Security();
    }

    /**
     * Method to validate the data to create a new user.
     *
     * @param username
     * @param password
     * @return boolean
     */
    private boolean checkData(String username, String password) {
        boolean isValid = true;
        if (!sec.isValidASCII(username)) {
            etUsername.setTextColor(Color.RED);
            isValid = false;
        } else
            etUsername.setTextColor(Color.BLACK);
        if (!sec.isValidASCII(password)) {
            etPassword.setTextColor(Color.RED);
            isValid = false;
        } else
            etPassword.setTextColor(Color.BLACK);
        return isValid;
    }
}
