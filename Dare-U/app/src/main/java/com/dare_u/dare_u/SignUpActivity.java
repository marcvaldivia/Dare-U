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
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dare_u.behaviour.ConnectActivity;
import com.dare_u.constants.Preferences;
import com.dare_u.constants.URLs;
import com.dare_u.gcm.RegistrationIntentService;
import com.dare_u.objects.ButtonU;
import com.dare_u.objects.EditTextU;
import com.dare_u.domain.User;
import com.dare_u.utils.DataAccess;
import com.dare_u.connect.DataConnect;
import com.dare_u.utils.General;
import com.dare_u.utils.Security;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity implements ConnectActivity {

    Security sec;
    BroadcastReceiver mRegistrationBroadcastReceiver;

    HashMap<String, String> dataPost;

    EditTextU etUsername, etPassword, etEmail, etBirthYear;
    TextView txtTermsPrivacy;
    ButtonU btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // EditText
        etUsername = (EditTextU) findViewById(R.id.etUsername);
        etPassword = (EditTextU) findViewById(R.id.etPassword);
        etEmail = (EditTextU) findViewById(R.id.etEmail);
        etBirthYear = (EditTextU) findViewById(R.id.etBirthYear);

        // TextView
        txtTermsPrivacy = (TextView) findViewById(R.id.txtTemsPrivacy);
        txtTermsPrivacy.setMovementMethod(LinkMovementMethod.getInstance());

        // Button
        btnSignUp = (ButtonU) findViewById(R.id.btnSignUp);

        // OnClickListener
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String email = etEmail.getText().toString();
                String birthYear = etBirthYear.getText().toString();
                if (checkData(username, password, email, birthYear)) {
                    dataPost = new HashMap();
                    dataPost.put(Preferences.USERNAME, username);
                    dataPost.put(Preferences.PASSWORD, sec.simpleEncryption(password, 1, sec.getAdd(username)));
                    dataPost.put(Preferences.EMAIL, email);
                    dataPost.put(Preferences.BIRTHYEAR, birthYear);
                    dataPost.put(Preferences.COUNTRY, General.getCountryCode(getApplicationContext()));
                    dataPost.put(Preferences.LANGUAGE, getString(R.string.app_language));
                    dataPost.put(Preferences.DEVICE, getString(R.string.app_device));
                    dataPost.put(Preferences.IMEI, General.getIMEI(getApplicationContext()));
                    if (General.checkPlayServices(getApplicationContext())) {
                        Intent intent = new Intent(SignUpActivity.this, RegistrationIntentService.class);
                        startService(intent);
                        onPreLoad();
                    } else {
                        dataPost.put(Preferences.NOTIFICATION, "");
                        new DataConnect(SignUpActivity.this, new User(), URLs.SIGN_UP, dataPost).execute();
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
                new DataConnect(SignUpActivity.this, new User(), URLs.SIGN_UP, dataPost).execute();
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
        btnSignUp.setEnabled(false);
        btnSignUp.setText(R.string.app_loading);
    }

    @Override
    public void onPostLoad(Object[] resultArray, String result) {
        btnSignUp.setEnabled(true);
        btnSignUp.setText(R.string.sign_up_button);
        String error = General.attendErrors(this, result);
        if (error != null) {
            General.showAlert(this, getString(R.string.title_activity_sign_up), error, getString(R.string.error_ok));
        } else if (resultArray.length == 1) {
            User user = (User) resultArray[0];
            DataAccess.createUser(getApplicationContext(), user);
            Intent main = new Intent(this, MainActivity.class);
            // Help Guides
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPreferences.edit().putBoolean(Preferences.HELP_HOME, true).apply();
            sharedPreferences.edit().putBoolean(Preferences.HELP_CHALLENGES, true).apply();
            sharedPreferences.edit().putBoolean(Preferences.HELP_CONTACTS, true).apply();
            startActivity(main);
            finish();
        } else {
            General.showAlert(this, getString(R.string.title_activity_sign_up), getString(R.string.error_GEN), getString(R.string.error_ok));
        }
    }

    /**
     * Method to initialize the components.
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
     * @param email
     * @param birthYear
     * @return boolean
     */
    private boolean checkData(String username, String password, String email, String birthYear) {
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
        if (!sec.isValidEmailAddress(email)) {
            etEmail.setTextColor(Color.RED);
            isValid = false;
        } else
            etEmail.setTextColor(Color.BLACK);
        if (Integer.parseInt(birthYear) < sec.getMinYear() || Integer.parseInt(birthYear) > sec.getMaxYear()) {
            etBirthYear.setTextColor(Color.RED);
            isValid = false;
        } else
            etBirthYear.setTextColor(Color.BLACK);
        return isValid;
    }
}
