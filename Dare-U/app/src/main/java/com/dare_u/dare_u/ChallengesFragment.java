package com.dare_u.dare_u;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
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
import android.widget.LinearLayout;
import android.widget.ListView;

import com.dare_u.adapters.ChallengeAdapter;
import com.dare_u.behaviour.ConnectActivity;
import com.dare_u.behaviour.OptionsResponsive;
import com.dare_u.connect.DataConnect;
import com.dare_u.constants.Preferences;
import com.dare_u.constants.URLs;
import com.dare_u.domain.Challenge;
import com.dare_u.domain.User;
import com.dare_u.objects.AlertDialogOptions;
import com.dare_u.utils.General;

import java.util.Arrays;
import java.util.HashMap;


public class ChallengesFragment extends Fragment implements ConnectActivity, OptionsResponsive {

    User user;
    Challenge challengeSelected;

    SwipeRefreshLayout refreshChallenges;

    FrameLayout root;
    LinearLayout guideChallenges;
    ListView lvChallenges;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = (FrameLayout) inflater.inflate(R.layout.fragment_challenges, container, false);

        // Guide
        guideChallenges = (LinearLayout) root.findViewById(R.id.guideChallenges);

        // ListView
        lvChallenges = (ListView) root.findViewById(R.id.lvChallenges);

        lvChallenges.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Challenge challenge = (Challenge) lvChallenges.getAdapter().getItem(position);
                if (challenge.getUserId() != user.getId() && challenge.getTurn() == 0) {
                    Intent contactChallenge = new Intent(getActivity(), ContactChallengeActivity.class);
                    contactChallenge.putExtra("CHALLENGE", challenge);
                    startActivity(contactChallenge);
                } else if (challenge.getUserId() == user.getId()) {
                    Intent camera = new Intent(getActivity(), CameraActivity.class);
                    camera.putExtra("CHALLENGE", challenge);
                    startActivity(camera);
                }
            }
        });

        lvChallenges.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Challenge challenge = (Challenge) lvChallenges.getAdapter().getItem(position);
                if (challenge.getUserId() != user.getId() && challenge.getTurn() == 1) {
                    new AlertDialogOptions(getActivity(), ChallengesFragment.this, getString(R.string.title_fragment_challenge),
                            getString(R.string.challenges_option_delete), getString(R.string.YES), getString(R.string.NO));
                    challengeSelected = challenge;
                }
                return true;
            }
        });

        // Swipe Refresh
        refreshChallenges = (SwipeRefreshLayout) root.findViewById(R.id.refreshChallenges);
        refreshChallenges.setColorSchemeResources(R.color.blue, R.color.red, R.color.green, R.color.orange);
        refreshChallenges.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshChallenges.setRefreshing(true);
                HashMap<String, String> dataPost = new HashMap();
                dataPost.put(Preferences.USER_ID, Integer.toString(user.getId()));
                new DataConnect(ChallengesFragment.this, new Challenge(), URLs.GET_CHALLENGES, dataPost).execute();
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
        inflater.inflate(R.menu.menu_challenges, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.action_new_challenge:
                if (user.getContacts().size() > 0) {
                    Intent newChallenge = new Intent(getActivity(), NewChallengeActivity.class);
                    startActivity(newChallenge);
                } else {
                    Fragment contacts = new ContactsFragment();
                    ((MainActivity) getActivity()).changeFragment(contacts);
                }
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
        super.onResume();
        getChallenges();
    }

    @Override
    public void onPreLoad() {
        refreshChallenges.setRefreshing(true);
    }

    @Override
    public void onPostLoad(Object[] resultArray, String result) {
        refreshChallenges.setRefreshing(false);
        String error = General.attendErrors(getActivity(), result);
        if (error != null) {
            General.showAlert(getActivity(), getString(R.string.title_fragment_challenge), error, getString(R.string.error_ok));
        } else if (resultArray.length != 0) {
            Challenge[] challenges = Arrays.copyOf(resultArray, resultArray.length, Challenge[].class);
            loadChallenges(challenges);
            guideChallenges.setVisibility(View.GONE);
        } else {
            // The user doesn't have any challenge
            lvChallenges.setAdapter(null);
            guideChallenges.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void clickResponse(boolean response) {
        if (response) deleteChallenge();
    }

    /**
     * Method to initialize the components.
     */
    private void init() {
        setHasOptionsMenu(true);
        user = ((MainActivity) getActivity()).getUserObject();
        HashMap<String, String> dataPost = new HashMap();
        dataPost.put(Preferences.USER_ID, Integer.toString(user.getId()));
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean help = sharedPreferences
                .getBoolean(Preferences.HELP_CHALLENGES, false);
        if (help) {
            General.showAlert(getActivity(), getString(R.string.help_title), getString(R.string.help_challenges), getString(R.string.error_ok));
            sharedPreferences.edit().putBoolean(Preferences.HELP_CHALLENGES, false).commit();
        } else {
            new DataConnect(ChallengesFragment.this, new Challenge(), URLs.GET_CHALLENGES, dataPost).execute();
        }
    }

    /**
     * Get user challenges
     */
    private void getChallenges() {
        HashMap<String, String> dataPost = new HashMap();
        dataPost.put(Preferences.USER_ID, Integer.toString(user.getId()));
        new DataConnect(ChallengesFragment.this, new Challenge(), URLs.GET_CHALLENGES, dataPost).execute();
    }

    /**
     * Load the challenges in the list view.
     */
    private void loadChallenges(Challenge[] challenges) {
        ChallengeAdapter challengeAdapter = new ChallengeAdapter(getActivity(), R.layout.listview_challenge, user.getId(), challenges);
        lvChallenges.setAdapter(challengeAdapter);
    }

    /**
     * Delete the challenge from the database.
     */
    private void deleteChallenge() {
        if (challengeSelected != null) {
            HashMap<String, String> dataPost = new HashMap();
            dataPost.put(Preferences.USER_ID, Integer.toString(challengeSelected.getUserId()));
            dataPost.put(Preferences.CONTACT_ID, Integer.toString(challengeSelected.getContactId()));
            dataPost.put(Preferences.FILE, challengeSelected.getUrl());
            dataPost.put(Preferences.DEL, "1");
            new DataConnect(this, new Challenge(), URLs.UPDATE_CHALLENGE, dataPost).execute();
        }
        challengeSelected = null;
    }
}
