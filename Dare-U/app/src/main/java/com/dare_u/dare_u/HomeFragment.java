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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;

import com.dare_u.adapters.ChallengeAdapter;
import com.dare_u.adapters.PostAdapter;
import com.dare_u.behaviour.ConnectActivity;
import com.dare_u.connect.DataConnect;
import com.dare_u.constants.Preferences;
import com.dare_u.constants.URLs;
import com.dare_u.domain.Challenge;
import com.dare_u.domain.Post;
import com.dare_u.domain.User;
import com.dare_u.utils.General;

import java.util.Arrays;
import java.util.HashMap;


public class HomeFragment extends Fragment implements ConnectActivity {

    User user;

    SwipeRefreshLayout refreshPosts;

    FrameLayout root;
    ListView lvPosts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = (FrameLayout) inflater.inflate(R.layout.fragment_home, container, false);

        // ListView
        lvPosts = (ListView) root.findViewById(R.id.lvPosts);

        lvPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent contactPost = new Intent(getActivity(), PostChallengeActivity.class);
                Post post = (Post) lvPosts.getAdapter().getItem(position);
                contactPost.putExtra("POST", post);
                startActivity(contactPost);
            }
        });

        // Swipe Refresh
        refreshPosts = (SwipeRefreshLayout) root.findViewById(R.id.refreshPosts);
        refreshPosts.setColorSchemeResources(R.color.blue, R.color.red, R.color.green, R.color.orange);
        refreshPosts.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPosts.setRefreshing(true);
                HashMap<String, String> dataPost = new HashMap();
                dataPost.put(Preferences.USER_ID, Integer.toString(user.getId()));
                new DataConnect(HomeFragment.this, new Post(), URLs.GET_POSTS, dataPost).execute();
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
        inflater.inflate(R.menu.menu_simple, menu);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPreLoad() {
        refreshPosts.setRefreshing(true);
    }

    @Override
    public void onPostLoad(Object[] resultArray, String result) {
        refreshPosts.setRefreshing(false);
        String error = General.attendErrors(getActivity(), result);
        if (error != null) {
            General.showAlert(getActivity(), getString(R.string.app_name), error, getString(R.string.error_ok));
        } else if (resultArray.length != 0) {
            Post[] posts = Arrays.copyOf(resultArray, resultArray.length, Post[].class);
            loadPosts(posts);
        }
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
                .getBoolean(Preferences.HELP_HOME, false);
        if (help) {
            General.showAlert(getActivity(), getString(R.string.help_title), getString(R.string.help_home), getString(R.string.error_ok));
            sharedPreferences.edit().putBoolean(Preferences.HELP_HOME, false).commit();
        } else {
            new DataConnect(HomeFragment.this, new Post(), URLs.GET_POSTS, dataPost).execute();
        }
    }

    /**
     * Load the posts in the list view.
     */
    private void loadPosts(Post[] posts) {
        PostAdapter postAdapter = new PostAdapter(getActivity(), R.layout.listview_challenge, posts);
        lvPosts.setAdapter(postAdapter);
    }

}
