package com.dare_u.dare_u;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dare_u.adapters.NewContactAdapter;
import com.dare_u.behaviour.ConnectActivity;
import com.dare_u.behaviour.ListResponsive;
import com.dare_u.connect.DataConnect;
import com.dare_u.constants.Errors;
import com.dare_u.constants.Preferences;
import com.dare_u.constants.URLs;
import com.dare_u.domain.Contact;
import com.dare_u.domain.User;
import com.dare_u.objects.EditTextU;
import com.dare_u.utils.DataAccess;
import com.dare_u.utils.General;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class NewContactActivity extends AppCompatActivity implements ConnectActivity, ListResponsive {

    User user;
    Contact newContact = null;

    LinearLayout guideInvite;
    EditTextU etToSearch;
    ImageButton btnSearch;
    ListView lvSearchContacts;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        // Guide
        guideInvite = (LinearLayout) findViewById(R.id.guideInvite);

        // EditText
        etToSearch = (EditTextU) findViewById(R.id.etToSearch);

        // ImageButton
        btnSearch = (ImageButton) findViewById(R.id.btnSearch);

        // ListView
        lvSearchContacts = (ListView) findViewById(R.id.lvSearchContacts);

        // OnClickListener
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchContact(v);
            }
        });

        // OnEditorActionListener
        etToSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchContact(v);
                return true;
            }
            return false;
            }
        });

        // OnItemClickListener
        lvSearchContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                General.showAlert(NewContactActivity.this, getString(R.string.title_activity_new_contact), "", getString(R.string.error_ok));
            }
        });

        // Init components
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_contact, menu);
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
            case R.id.action_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.help_invite_friends) + " " + user.getUsername());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getString(R.string.app_name)));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPreLoad() {
        progressDialog.setTitle(getString(R.string.title_activity_new_contact));
        progressDialog.setMessage(getString(R.string.app_loading));
        progressDialog.show();
    }

    @Override
    public void onPostLoad(Object[] resultArray, String result) {
        progressDialog.dismiss();
        String error = General.attendErrors(this, result);
        if (error != null) {
            General.showAlert(this, getString(R.string.title_activity_new_contact), error, getString(R.string.error_ok));
        } else if (resultArray.length != 0) {
            Contact[] contacts = Arrays.copyOf(resultArray, resultArray.length, Contact[].class);
            loadContacts(contacts);
            guideInvite.setVisibility(View.GONE);
        } else if (result.equals(Errors.ERROR_OK)) {
            if (newContact != null) user.getContacts().add(newContact);
            newContact = null;
            Collections.sort(user.getContacts(), General.contactSorter);
            DataAccess.createUser(this, user);
            finish();
        }
    }

    @Override
    public void clickResponse(View view, Object object) {
        if (object != null && object instanceof Contact) {
            newContact = (Contact) object;
            HashMap<String, String> dataPost = new HashMap();
            dataPost.put(Preferences.USER_ID, Integer.toString(user.getId()));
            dataPost.put(Preferences.USERNAME, user.getUsername());
            dataPost.put(Preferences.CONTACT_ID, Integer.toString(((Contact) object).getContactId()));
            dataPost.put(Preferences.NOTIFICATION, newContact.getNotification());
            new DataConnect(NewContactActivity.this, new Contact(), URLs.NEW_CONTACT, dataPost).execute();
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
        // Progress Dialog
        progressDialog = new ProgressDialog(this);
    }

    /**
     * Load the contacts given by the parameter, but only these contacts who are not in the contact
     * list of the user.
     *
     * @param contacts
     */
    private void loadContacts(Contact[] contacts) {
        ArrayList<Contact> noContacts = new ArrayList();
        for (Contact c : contacts) {
            if (!user.getContacts().contains(c)) {
                noContacts.add(c);
            }
        }
        contacts = noContacts.toArray(new Contact[noContacts.size()]);
        NewContactAdapter contactAdapter = new NewContactAdapter(this, R.layout.listview_new_contact, contacts);
        lvSearchContacts.setAdapter(contactAdapter);
    }

    /**
     * Search in the database the contacts like the given by the EditText.
     *
     * @param v
     */
    private void searchContact(View v) {
        if (v != null) {
            InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (keyboard != null) keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        if (!etToSearch.getText().toString().isEmpty()) {
            HashMap<String, String> dataPost = new HashMap();
            dataPost.put(Preferences.USER_ID, Integer.toString(user.getId()));
            dataPost.put(Preferences.CONTACT, etToSearch.getText().toString());
            new DataConnect(NewContactActivity.this, new Contact(), URLs.SEARCH_CONTACTS, dataPost).execute();
        }
    }
}
