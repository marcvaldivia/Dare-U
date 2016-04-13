package com.dare_u.dare_u;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.dare_u.behaviour.ConnectActivity;
import com.dare_u.behaviour.MainContent;
import com.dare_u.connect.DataConnect;
import com.dare_u.constants.Preferences;
import com.dare_u.constants.URLs;
import com.dare_u.domain.Contact;
import com.dare_u.domain.User;
import com.dare_u.utils.DataAccess;
import com.dare_u.utils.General;
import com.dare_u.utils.Security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ConnectActivity, MainContent {

    Security sec;

    User user;

    ImageView home, challenges, contacts, profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ImageButton
        home = (ImageView) findViewById(R.id.home);
        challenges = (ImageView) findViewById(R.id.challenges);
        contacts = (ImageView) findViewById(R.id.contacts);
        profile = (ImageView) findViewById(R.id.profile);

        // OnClickListener
        home.setOnClickListener(this);
        challenges.setOnClickListener(this);
        contacts.setOnClickListener(this);
        profile.setOnClickListener(this);

        // Init components
        init();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            // TODO nothing
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home:
                Fragment home = new HomeFragment();
                changeFragment(home);
                break;
            case R.id.challenges:
                Fragment challenges = new ChallengesFragment();
                changeFragment(challenges);
                break;
            case R.id.contacts:
                Fragment contacts = new ContactsFragment();
                changeFragment(contacts);
                break;
            case R.id.profile:
                Fragment profile = new ProfileFragment();
                changeFragment(profile);
                break;
        }
    }

    @Override
    public void onPreLoad() {

    }

    @Override
    public void onPostLoad(Object[] resultArray, String result) {
        String error = General.attendErrors(this, result);
        if (error != null) {
            General.showAlert(this, getString(R.string.app_name), error, getString(R.string.error_ok));
        } else if (resultArray.length != 0) {
            Contact[] contacts = Arrays.copyOf(resultArray, resultArray.length, Contact[].class);
            user.setContacts(new ArrayList(Arrays.asList(contacts)));
            user.setLastUpdate(new Date());
            DataAccess.createUser(this, user);
        } else {
            // At this point, the user doesn't have any contact
            user.setContacts(new ArrayList<Contact>());
            user.setLastUpdate(new Date());
            DataAccess.createUser(this, user);
        }
    }

    @Override
    public User getUserObject() {
        user = DataAccess.getUser(getApplicationContext());
        return user;
    }

    /**
     * Method where the app check if the user is logged and initialize the components.
     */
    private void init() {
        if (DataAccess.isLogIn(getApplicationContext())) {
            user = DataAccess.getUser(getApplicationContext());
            // Security
            sec = new Security();
            // Init Fragment
            Fragment home = new ChallengesFragment();
            changeFragment(home);
            // Check if the contacts need to be updated
            if (user.getContacts().size() == 0/*General.oneDayBefore(user.getLastUpdate())*/) {
                user.setLastUpdate(new Date());
                HashMap<String, String> dataPost = new HashMap();
                dataPost.put(Preferences.USER_ID, Integer.toString(user.getId()));
                new DataConnect(this, new Contact(), URLs.GET_CONTACTS, dataPost).execute();
            }
        } else {
            Intent welcome = new Intent(this, WelcomeActivity.class);
            startActivity(welcome);
            finish();
        }
    }

    /**
     * Replace the fragment.
     *
     * @param newFragment
     */
    public void changeFragment(Fragment newFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);

        // Change the title of the actionBar
        changeTitle(newFragment);

        // Commit the transaction
        transaction.commit();
    }

    /**
     * Change the title of the actionBar and put in it the title of the fragment.
     * Moreover, the method focus the button of the selection bar selected.
     *
     * @param newFragment
     */
    private void changeTitle(Fragment newFragment) {
        String title;
        setFilterNull();
        if (newFragment instanceof ChallengesFragment) {
            title = getString(R.string.title_fragment_challenge);
            challenges.setColorFilter(0xFF4D4D4D, PorterDuff.Mode.MULTIPLY);
        } else if (newFragment instanceof ContactsFragment) {
            title = getString(R.string.title_fragment_contacts);
            contacts.setColorFilter(0xFF4D4D4D, PorterDuff.Mode.MULTIPLY);
        } else if (newFragment instanceof ProfileFragment) {
            title = getString(R.string.title_fragment_profile);
            profile.setColorFilter(0xFF4D4D4D, PorterDuff.Mode.MULTIPLY);
            profile.setBackgroundColor(0xFFFFFF);
        } else {
            title = getString(R.string.app_name);
            home.setColorFilter(0xFF4D4D4D, PorterDuff.Mode.MULTIPLY);
        }
        // Home button to come back
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    /**
     * Set all the selection bar buttons to their normal state.
     */
    private void setFilterNull() {
        home.setColorFilter(null);
        challenges.setColorFilter(null);
        contacts.setColorFilter(null);
        profile.setColorFilter(null);
    }
}
