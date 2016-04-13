package com.dare_u.dare_u;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dare_u.behaviour.ConnectActivity;
import com.dare_u.connect.DataConnect;
import com.dare_u.constants.Errors;
import com.dare_u.constants.Preferences;
import com.dare_u.constants.URLs;
import com.dare_u.domain.User;
import com.dare_u.objects.ButtonU;
import com.dare_u.utils.DataAccess;
import com.dare_u.utils.General;

import java.util.HashMap;


public class ProfileFragment extends Fragment implements ConnectActivity {

    User user;

    FrameLayout root;
    TextView txtUsername;
    ButtonU btnLogOut;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = (FrameLayout) inflater.inflate(R.layout.fragment_profile, container, false);

        txtUsername = (TextView) root.findViewById(R.id.txtUsername);

        btnLogOut = (ButtonU) root.findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> dataPost = new HashMap();
                dataPost.put(Preferences.USER_ID, Integer.toString(user.getId()));
                new DataConnect(ProfileFragment.this, new User(), URLs.LOG_OUT, dataPost).execute();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.action_settings:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPreLoad() {
        btnLogOut.setText(R.string.app_loading);
    }

    @Override
    public void onPostLoad(Object[] resultArray, String result) {
        btnLogOut.setText(R.string.profile_log_out);
        String error = General.attendErrors(getActivity(), result);
        if (error != null) {
            General.showAlert(getActivity(), getString(R.string.title_fragment_profile), error, getString(R.string.error_ok));
        } else if (result.equals(Errors.ERROR_OK)) {
            DataAccess.createEmptyFile(getActivity());
            getActivity().finish();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * Method to initialize the components.
     */
    private void init() {
        setHasOptionsMenu(true);
        user = ((MainActivity) getActivity()).getUserObject();
        // Load user info
        txtUsername.setText(user.getUsername());
    }
}
