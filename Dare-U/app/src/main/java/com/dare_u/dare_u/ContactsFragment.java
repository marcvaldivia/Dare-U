package com.dare_u.dare_u;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.dare_u.adapters.ContactAdapter;
import com.dare_u.behaviour.ConnectActivity;
import com.dare_u.behaviour.ListResponsive;
import com.dare_u.behaviour.MainContent;
import com.dare_u.behaviour.OptionsResponsive;
import com.dare_u.connect.DataConnect;
import com.dare_u.constants.Preferences;
import com.dare_u.constants.URLs;
import com.dare_u.domain.Contact;
import com.dare_u.domain.User;
import com.dare_u.objects.AlertDialogOptions;
import com.dare_u.objects.ButtonU;
import com.dare_u.utils.DataAccess;
import com.dare_u.utils.General;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;


public class ContactsFragment extends Fragment implements ConnectActivity, ListResponsive, OptionsResponsive {

    User user;
    Contact cSelected;

    SwipeRefreshLayout refreshContacts;

    FrameLayout root;
    LinearLayout guideContacts;
    ButtonU btnInvite;
    ListView lvContacts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = (FrameLayout) inflater.inflate(R.layout.fragment_contacts, container, false);

        // Guide
        guideContacts = (LinearLayout) root.findViewById(R.id.guideContacts);

        // Button Guide
        btnInvite = (ButtonU) root.findViewById(R.id.btnInvite);
        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.help_invite_friends) + " " + user.getUsername());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getString(R.string.app_name)));
            }
        });

        // ListView
        lvContacts = (ListView) root.findViewById(R.id.lvContacts);

        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object object = lvContacts.getAdapter().getItem(position);
                if (object != null && object instanceof Contact) {
                    int contactId = ((Contact) object).getContactId();
                    Intent newChallenge = new Intent(getActivity(), NewChallengeActivity.class);
                    newChallenge.putExtra("contactId", contactId);
                    startActivity(newChallenge);
                }
            }
        });

        lvContacts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Object object = lvContacts.getAdapter().getItem(position);
                if (object != null && object instanceof Contact) {
                    cSelected = (Contact) object;
                    new AlertDialogOptions(getActivity(), ContactsFragment.this, getString(R.string.title_fragment_contacts),
                            getString(R.string.contacts_option_delete), getString(R.string.YES), getString(R.string.NO));
                }
                return true;
            }
        });

        // Swipe Refresh
        refreshContacts = (SwipeRefreshLayout) root.findViewById(R.id.refreshContacts);
        refreshContacts.setColorSchemeResources(R.color.blue, R.color.red, R.color.green, R.color.orange);
        refreshContacts.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContacts.setRefreshing(true);
                HashMap<String, String> dataPost = new HashMap();
                dataPost.put(Preferences.USER_ID, Integer.toString(user.getId()));
                new DataConnect(ContactsFragment.this, new Contact(), URLs.GET_CONTACTS, dataPost).execute();
            }
        });

        // Init components
        init();

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_contacts, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.action_add_contact:
                Intent newContact = new Intent(getActivity(), NewContactActivity.class);
                startActivity(newContact);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        init();
        super.onResume();
    }

    @Override
    public void onPreLoad() {
        refreshContacts.setRefreshing(true);
    }

    @Override
    public void onPostLoad(Object[] resultArray, String result) {
        refreshContacts.setRefreshing(false);
        String error = General.attendErrors(getActivity(), result);
        if (error != null) {
            General.showAlert(getActivity(), getString(R.string.title_fragment_contacts), error, getString(R.string.error_ok));
        } else if (resultArray.length != 0) {
            Contact[] contacts = Arrays.copyOf(resultArray, resultArray.length, Contact[].class);
            user.setContacts(new ArrayList(Arrays.asList(contacts)));
            user.setLastUpdate(new Date());
            DataAccess.createUser(getActivity(), user);
            loadContacts();
            guideContacts.setVisibility(View.GONE);
        } else {
            // At this point, the user doesn't have any contact
            guideContacts.setVisibility(View.VISIBLE);
            user.setContacts(new ArrayList<Contact>());
            user.setLastUpdate(new Date());
            DataAccess.createUser(getActivity(), user);
        }
        loadContacts();
    }

    @Override
    public void clickResponse(View view, Object object) {
        if (object != null && object instanceof Contact) {
            boolean favorite = !((Contact) object).isFavorite();
            HashMap<String, String> dataPost = new HashMap();
            dataPost.put(Preferences.USER_ID, Integer.toString(user.getId()));
            dataPost.put(Preferences.CONTACT_ID, Integer.toString(((Contact) object).getContactId()));
            dataPost.put(Preferences.FAVORITE, Boolean.toString(favorite));
            new DataConnect(this, new Contact(), URLs.UPDATE_CONTACT, dataPost).execute();
        }
    }

    @Override
    public void clickResponse(boolean response) {
        if (response) deleteContact();
    }

    /**
     * Method to initialize the components.
     */
    private void init() {
        setHasOptionsMenu(true);
        user = ((MainActivity) getActivity()).getUserObject();
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean help = sharedPreferences
                .getBoolean(Preferences.HELP_CONTACTS, false);
        if (help) {
            General.showAlert(getActivity(), getString(R.string.help_title), getString(R.string.help_contacts), getString(R.string.error_ok));
            sharedPreferences.edit().putBoolean(Preferences.HELP_CONTACTS, false).commit();
        } else {
            if (user.getContacts().size() > 0 && !General.oneDayBefore(user.getLastUpdate())) {
                guideContacts.setVisibility(View.GONE);
                loadContacts();
            } else {
                user.setLastUpdate(new Date());
                HashMap<String, String> dataPost = new HashMap();
                dataPost.put(Preferences.USER_ID, Integer.toString(user.getId()));
                new DataConnect(this, new Contact(), URLs.GET_CONTACTS, dataPost).execute();
            }
        }
    }

    /**
     * Load the contacts in the list view.
     */
    private void loadContacts() {
        ArrayList<Object> contactList = General.generateContactList(getActivity(), user.getContacts());
        ContactAdapter contactAdapter = new ContactAdapter(getActivity(), this, R.layout.listview_contact, contactList.toArray(new Object[contactList.size()]));
        lvContacts.setAdapter(contactAdapter);
    }

    /**
     * Deletes the contact selected.
     */
    private void deleteContact() {
        if (cSelected != null) {
            HashMap<String, String> dataPost = new HashMap();
            dataPost.put(Preferences.USER_ID, Integer.toString(user.getId()));
            dataPost.put(Preferences.CONTACT_ID, Integer.toString(cSelected.getContactId()));
            dataPost.put(Preferences.DEL, Boolean.toString(true));
            new DataConnect(this, new Contact(), URLs.UPDATE_CONTACT, dataPost).execute();
        }
        cSelected = null;
    }
}
