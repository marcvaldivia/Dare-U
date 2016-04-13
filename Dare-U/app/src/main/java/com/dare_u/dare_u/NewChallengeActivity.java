package com.dare_u.dare_u;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;

import com.dare_u.adapters.ContactSpinnerAdapter;
import com.dare_u.behaviour.ConnectActivity;
import com.dare_u.connect.DataConnect;
import com.dare_u.constants.Errors;
import com.dare_u.constants.Preferences;
import com.dare_u.constants.URLs;
import com.dare_u.domain.Challenge;
import com.dare_u.domain.Contact;
import com.dare_u.domain.User;
import com.dare_u.objects.ButtonU;
import com.dare_u.objects.EditTextU;
import com.dare_u.utils.DataAccess;
import com.dare_u.utils.General;

import java.util.HashMap;

public class NewChallengeActivity extends AppCompatActivity implements ConnectActivity {

    User user;

    Spinner spContact;
    EditTextU etChallenge;
    ButtonU btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_challenge);

        // EditText
        etChallenge = (EditTextU) findViewById(R.id.etChallenge);

        // Spinner
        spContact = (Spinner) findViewById(R.id.spContact);

        // Button
        btnSend = (ButtonU) findViewById(R.id.btnSend);

        // OnClickListener
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact contact = (Contact) spContact.getSelectedItem();
                String challenge = etChallenge.getText().toString();
                if (checkData(contact, challenge)) {
                    HashMap<String, String> dataPost = new HashMap();
                    dataPost.put(Preferences.USER_ID, Integer.toString(user.getId()));
                    dataPost.put(Preferences.USERNAME, user.getUsername());
                    dataPost.put(Preferences.CONTACT_ID, Integer.toString(contact.getContactId()));
                    dataPost.put(Preferences.CONTACT, contact.getContact());
                    dataPost.put(Preferences.NOTIFICATION, contact.getNotification());
                    dataPost.put(Preferences.CHALLENGE, challenge);
                    new DataConnect(NewChallengeActivity.this, new Challenge(), URLs.NEW_CHALLENGE, dataPost).execute();
                }
            }
        });

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
    public void onPreLoad() {
        btnSend.setEnabled(false);
        btnSend.setText(R.string.app_loading);
    }

    @Override
    public void onPostLoad(Object[] resultArray, String result) {
        btnSend.setText(R.string.new_challenge_send);
        btnSend.setEnabled(true);
        String error = General.attendErrors(this, result);
        if (error != null) {
            General.showAlert(this, getString(R.string.title_activity_new_challenge), error, getString(R.string.error_ok));
        } else if (result.equals(Errors.ERROR_OK)) {
            finish();
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
        user = DataAccess.getUser(this);
        // Load contacts
        loadContacts();
    }

    /**
     * Load the contacts in the spinner.
     */
    private void loadContacts() {
        ContactSpinnerAdapter contactAdapter = new ContactSpinnerAdapter(this, R.layout.spinner_contact, user.getContacts().toArray(new Contact[user.getContacts().size()]));
        contactAdapter.setDropDownViewResource(R.layout.spinner_contact);
        spContact.setAdapter(contactAdapter);
        int id = getIntent().getIntExtra("contactId", -1);
        if (id != -1) {
            Contact contact = General.getContactFromList(user.getContacts(), id);
            int selected = contactAdapter.getPosition(contact);
            spContact.setSelection(selected);
        }
    }

    /**
     * Returns a boolean that indicates if the information given by the user is valid or not.
     *
     * @param contact
     * @param challenge
     * @return
     */
    private boolean checkData(Contact contact, String challenge) {
        return contact != null && challenge != null && !challenge.isEmpty();
    }
}
